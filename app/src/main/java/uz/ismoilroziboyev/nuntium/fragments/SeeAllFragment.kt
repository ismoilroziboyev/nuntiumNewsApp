package uz.ismoilroziboyev.nuntium.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.ismoilroziboyev.nuntium.App
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.adapters.SeeAllPagingAdapter
import uz.ismoilroziboyev.nuntium.adapters.TopicsRvAdapter
import uz.ismoilroziboyev.nuntium.databinding.FragmentSeeAllBinding
import uz.ismoilroziboyev.nuntium.models.News
import uz.ismoilroziboyev.nuntium.utils.RequestResult
import uz.ismoilroziboyev.nuntium.viewmodels.ViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

private const val ARG_PARAM2 = "topic"

class SeeAllFragment : Fragment(), CoroutineScope {

    private var topic: String? = null
    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  /* forward= */true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        arguments?.let {
            topic = it.getString(ARG_PARAM2)
        }
        App.appComponent.injectSeeAllFragment(this)
    }

    private lateinit var binding: FragmentSeeAllBinding
    private lateinit var topicsRvAdapter: TopicsRvAdapter
    private lateinit var seeAllPagingAdapter: SeeAllPagingAdapter

    @Inject
    lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeeAllBinding.inflate(inflater, container, false)

        setUpDatas()
        setUpTopicsRv()

        binding.backButton.setOnClickListener { findNavController().popBackStack() }

        return binding.root
    }

    private fun setUpDatas() {
        if (topic != "Latest news") {
            viewModel.fetchTopicNewsModel(topic!!, 0)
        } else {
            viewModel.fetchTopicNewsModel("", 0)
        }

        launch {
            viewModel.topicNewsFlow.collect {
                binding.apply {
                    when (it) {
                        is RequestResult.Loading -> {
                            progressBar.visibility = View.VISIBLE
                        }
                        is RequestResult.Error -> {
                            progressBar.visibility = View.GONE
                        }
                        is RequestResult.Success -> {
                            progressBar.visibility = View.GONE
                            newsRecyclerView.visibility = View.VISIBLE

                            seeAllPagingAdapter = SeeAllPagingAdapter(
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

                            binding.newsRecyclerView.adapter = seeAllPagingAdapter
                        }
                    }
                }
            }
        }
    }

    private fun setUpTopicsRv() {
        launch {
            viewModel.getFavouritesList().collect {
                if (it is RequestResult.Success) {

                    val list = ArrayList<String>()
                    list.add("Latest news")
                    list.add("Recommended")
                    list.addAll(it.data.allTopics)

                    topicsRvAdapter = TopicsRvAdapter(list,
                        object : TopicsRvAdapter.OnItemClickListener {
                            override fun onItemClickListener(item: String, position: Int) {
                                val lastSelectedItem = topicsRvAdapter.activeItem
                                topicsRvAdapter.activeItem = position
                                topicsRvAdapter.notifyItemChanged(position)
                                topic = item
                                topicsRvAdapter.notifyItemChanged(lastSelectedItem)

                                when (item) {
                                    "Latest news" -> {
                                        viewModel.fetchTopicNewsModel("", 0)
                                    }
                                    "Recommended" -> {
                                        viewModel.fetchTopicNewsModel(viewModel.getTopicsList(), 0)
                                    }
                                    else -> {
                                        viewModel.fetchTopicNewsModel(item, 0)
                                    }
                                }
                            }
                        })

                    binding.topicsRv.adapter = topicsRvAdapter

                    val index = topicsRvAdapter.list.indexOf(topic)


                    if (index == -1) {
                        topicsRvAdapter.activeItem = 1
                        topicsRvAdapter.notifyItemChanged(0)
                        topicsRvAdapter.notifyItemChanged(1)
                    } else {
                        topicsRvAdapter.activeItem = index
                        topicsRvAdapter.notifyItemChanged(index)
                        topicsRvAdapter.notifyItemChanged(0)
                    }

                    binding.topicsRv.scrollToPosition(topicsRvAdapter.activeItem)


                }
            }
        }
    }

}