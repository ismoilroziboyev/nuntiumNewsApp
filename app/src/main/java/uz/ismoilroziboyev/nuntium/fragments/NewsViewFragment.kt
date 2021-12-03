package uz.ismoilroziboyev.nuntium.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.databinding.FragmentNewsViewBinding
import uz.ismoilroziboyev.nuntium.models.Article
import uz.ismoilroziboyev.nuntium.models.News
import uz.ismoilroziboyev.nuntium.utils.setImageWithUrl
import java.lang.StringBuilder
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.ismoilroziboyev.nuntium.App
import uz.ismoilroziboyev.nuntium.adapters.SeeAllPagingAdapter
import uz.ismoilroziboyev.nuntium.database.entities.SavedNewsEntity
import uz.ismoilroziboyev.nuntium.utils.RequestResult
import uz.ismoilroziboyev.nuntium.viewmodels.ViewModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.coroutines.CoroutineContext

private const val ARG_PARAM1 = "news"
private const val ARG_PARAM2 = "article"

class NewsViewFragment : Fragment(), MotionLayout.TransitionListener, CoroutineScope {

    private var param1: News? = null
    private var param2: Article? = null

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    private val job = Job()

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        App.appComponent.injectNewsViewFragment(this)

        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as News?
            param2 = it.getSerializable(ARG_PARAM2) as Article?
        }
    }


    private lateinit var binding: FragmentNewsViewBinding

    @Inject
    lateinit var viewModel: ViewModel
    private lateinit var topicNewsRvAdapter: SeeAllPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchRecommendedNews()
        observeRecommendedNews()

        binding.root.addTransitionListener(this)

        binding.apply {

            shareButton.setOnClickListener {
                var sendingText = ""
                if (param1 != null) {
                    sendingText =
                        "This content shared from Nuntium app. \nTitle : ${param1?.title} \nDescription : ${param1?.description} \nUrl link: ${param1?.url}"
                } else {
                    sendingText =
                        "This content shared from Nuntium app. \nTitle : ${param2?.title} \nDescription : ${param2?.description} \nUrl link: ${param2?.url}"
                }

                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        sendingText
                    )
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }


            backButton.setOnClickListener { findNavController().popBackStack() }

            if (param1 != null) {

                if (viewModel.repository.getCountThisSavedNewsEntity(
                        param1?.title ?: "",
                        param1?.description ?: ""
                    ) == 0
                ) {
                    saveButton.setImageResource(R.drawable.ic_save_button_white)
                } else {
                    saveButton.setImageResource(R.drawable.ic_bookmark_active_white)
                }

                imageView.setImageWithUrl(param1?.image ?: "")
                descriptionTv.text = param1?.description
                titleTv.text = param1?.title

                val stringBuilder = StringBuilder()
                param1!!.category.forEachIndexed { index, it ->

                    if (index == param1!!.category.size - 1) {
                        stringBuilder.append(it[0].uppercase() + it.substring(1))
                    } else {
                        stringBuilder.append("${it[0].uppercase() + it.substring(1)}, ")
                    }

                }
                val categoryString = stringBuilder.toString()
                topic.text = categoryString

                saveButton.setOnClickListener {
                    val stringMaker = StringBuilder()

                    param1!!.category.forEachIndexed { index, string ->
                        if (index == param1!!.category.size - 1) {
                            stringMaker.append(string[0].uppercase() + string.substring(1))
                        } else {
                            stringMaker.append("${string[0].uppercase() + string.substring(1)}, ")
                        }
                    }

                    val category = stringMaker.toString()
                    val savedNewsEntity = SavedNewsEntity(
                        title = param1!!.title,
                        description = param1!!.description,
                        url = param1!!.url,
                        imageUrl = param1!!.image,
                        topic = category
                    )

                    if (viewModel.repository.getCountThisSavedNewsEntity(
                            savedNewsEntity.title,
                            savedNewsEntity.description
                        ) == 0
                    ) {
                        viewModel.repository.insertNewSavedNewsEntity(savedNewsEntity)
                        if (root.currentState == R.id.end) {
                            saveButton.setImageResource(R.drawable.ic_bookmark_active)
                        } else {
                            saveButton.setImageResource(R.drawable.ic_bookmark_active_white)
                        }
                    } else {
                        if (root.currentState == R.id.end) {
                            saveButton.setImageResource(R.drawable.ic_saved)
                        } else {
                            saveButton.setImageResource(R.drawable.ic_save_button_white)
                        }

                        viewModel.repository.deleteSavedNewsEntity(
                            viewModel.repository.getSavedNewsEntity(
                                savedNewsEntity.title,
                                savedNewsEntity.description
                            )
                        )
                    }
                }

                browseTv.setOnClickListener {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(param1?.url))
                    startActivity(browserIntent)
                }
            } else {

                saveButton.setOnClickListener {

                    val category = param2!!.source.name
                    val savedNewsEntity = SavedNewsEntity(
                        title = param2!!.title,
                        description = param2!!.description,
                        url = param2!!.url,
                        imageUrl = param2!!.urlToImage ?: "",
                        topic = category
                    )

                    if (viewModel.repository.getCountThisSavedNewsEntity(
                            savedNewsEntity.title,
                            savedNewsEntity.description
                        ) == 0
                    ) {
                        viewModel.repository.insertNewSavedNewsEntity(savedNewsEntity)
                        if (root.currentState == R.id.end) {
                            saveButton.setImageResource(R.drawable.ic_bookmark_active)
                        } else {
                            saveButton.setImageResource(R.drawable.ic_bookmark_active_white)
                        }
                    } else {
                        if (root.currentState == R.id.end) {
                            saveButton.setImageResource(R.drawable.ic_saved)
                        } else {
                            saveButton.setImageResource(R.drawable.ic_save_button_white)
                        }

                        viewModel.repository.deleteSavedNewsEntity(
                            viewModel.repository.getSavedNewsEntity(
                                savedNewsEntity.title,
                                savedNewsEntity.description
                            )
                        )
                    }
                }


                if (viewModel.repository.getCountThisSavedNewsEntity(
                        param2?.title ?: "",
                        param2?.description ?: ""
                    ) == 0
                ) {
                    saveButton.setImageResource(R.drawable.ic_save_button_white)
                } else {
                    saveButton.setImageResource(R.drawable.ic_bookmark_active_white)
                }

                imageView.setImageWithUrl(param2?.urlToImage ?: "")
                descriptionTv.text = param2?.description
                topic.text = param2?.source?.name
                titleTv.text = param2?.title

                browseTv.setOnClickListener {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(param2?.url))
                    startActivity(browserIntent)
                }
            }


        }
    }

    private fun observeRecommendedNews() {
        launch {
            viewModel.topicNewsFlow.collect {
                when (it) {
                    is RequestResult.Loading -> {
                        binding.apply {
                            progressBar.visibility = View.VISIBLE
                            newsRecyclerView.visibility = View.GONE
                        }
                    }
                    is RequestResult.Error -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            newsRecyclerView.visibility = View.GONE
                        }
                    }
                    is RequestResult.Success -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            newsRecyclerView.visibility = View.VISIBLE
                            topicNewsRvAdapter = SeeAllPagingAdapter(
                                ArrayList(it.data.news),
                                object : SeeAllPagingAdapter.OnItemClickListener {
                                    override fun onItemClickListener(item: News, position: Int) {
                                        val bundle = Bundle()
                                        bundle.putSerializable("news", item)
                                        findNavController().navigate(R.id.newsViewFragment, bundle)
                                    }

                                    override fun bottomReachedListener(nextPage: Int) {

                                    }
                                })

                            newsRecyclerView.adapter = topicNewsRvAdapter
                        }
                    }
                }

            }
        }
    }


    private fun fetchRecommendedNews() {
        var topicsList = ArrayList<String>()
        launch {
            viewModel.repository.getFavouriteTopicsFromDb().collect {
                topicsList.addAll(it.selectedTopics)
                sendRequest(topicsList)
            }
        }
    }

    private fun sendRequest(topicsList: List<String>) {
        val random = Random()
        val topic = topicsList[random.nextInt(topicsList.size)]
        val page = random.nextInt(10)

        viewModel.fetchTopicNewsModel(topic, page)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsViewBinding.inflate(inflater, container, false)


        return binding.root


    }

    override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {

    }

    override fun onTransitionChange(
        motionLayout: MotionLayout?,
        startId: Int,
        endId: Int,
        progress: Float
    ) {

    }

    override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {

        var title = ""
        var description = ""

        if (param1 != null) {
            title = param1!!.title
            description = param1!!.description
        } else {
            title = param2!!.title
            description = param2!!.description
        }

        binding.apply {
            if (currentId == R.id.end) {
                titleTv.setTextColor(root.context.getColor(R.color.main_txt_color))
                imageCard.radius = 25f
                backButton.setImageResource(R.drawable.ic_back_button_dark)
                shareButton.setImageResource(R.drawable.ic_share_button_dark)

                if (viewModel.repository.getCountThisSavedNewsEntity(title, description) == 0) {
                    saveButton.setImageResource(R.drawable.ic_saved)
                } else {
                    saveButton.setImageResource(R.drawable.ic_bookmark_active)
                }
            } else {
                imageCard.radius = 0f
                titleTv.setTextColor(Color.WHITE)
                backButton.setImageResource(R.drawable.ic_back_button_white)
                shareButton.setImageResource(R.drawable.ic_share_button_white)

                if (viewModel.repository.getCountThisSavedNewsEntity(title, description) == 0) {
                    saveButton.setImageResource(R.drawable.ic_save_button_white)
                } else {
                    saveButton.setImageResource(R.drawable.ic_bookmark_active_white)
                }
            }
        }
    }

    override fun onTransitionTrigger(
        motionLayout: MotionLayout?,
        triggerId: Int,
        positive: Boolean,
        progress: Float
    ) {

    }

}