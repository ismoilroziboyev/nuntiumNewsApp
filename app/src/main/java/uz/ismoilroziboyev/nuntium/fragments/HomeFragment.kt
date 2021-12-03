package uz.ismoilroziboyev.nuntium.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import uz.ismoilroziboyev.nuntium.App
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.adapters.BannerRvAdapter
import uz.ismoilroziboyev.nuntium.adapters.NewsRvAdapter
import uz.ismoilroziboyev.nuntium.adapters.TopicNewsRvAdapter
import uz.ismoilroziboyev.nuntium.adapters.TopicsRvAdapter
import uz.ismoilroziboyev.nuntium.database.entities.SavedNewsEntity
import uz.ismoilroziboyev.nuntium.databinding.FragmentHomeBinding
import uz.ismoilroziboyev.nuntium.models.News
import uz.ismoilroziboyev.nuntium.utils.MySharedPreferences
import uz.ismoilroziboyev.nuntium.utils.RequestResult
import uz.ismoilroziboyev.nuntium.viewmodels.ViewModel
import java.lang.StringBuilder
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment(), CoroutineScope {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        App.appComponent.injectHomeFragment(this)
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var topicsRvAdapter: TopicsRvAdapter
    private lateinit var bannerRvAdapter: BannerRvAdapter
    private lateinit var newsRvAdapter: NewsRvAdapter
    private val job = Job()
    private lateinit var topicNewsRvAdapter: TopicNewsRvAdapter

    @Inject
    lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setUpTopics()
        setUpClicks()
        setUpBannerNews()
        setUpRecommendedNews()

        topicNewsRvAdapter = TopicNewsRvAdapter(object : TopicNewsRvAdapter.OnItemClickListener {
            override fun onItemClickListener(item: News, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("news", item)
                findNavController().navigate(R.id.newsViewFragment, bundle)
            }

            override fun onSaveButtonClickListener(item: News, position: Int) {
                val stringBuilder = StringBuilder()

                item.category.forEachIndexed { index, it ->
                    if (index == item.category.size - 1) {
                        stringBuilder.append(it[0].uppercase() + it.substring(1))
                    } else {
                        stringBuilder.append("${it[0].uppercase() + it.substring(1)}, ")
                    }
                }

                val categoryString = stringBuilder.toString()
                val savedNewsEntity = SavedNewsEntity(
                    title = item.title,
                    description = item.description,
                    url = item.url,
                    imageUrl = item.image,
                    topic = categoryString
                )

                if (viewModel.repository.getCountThisSavedNewsEntity(
                        savedNewsEntity.title,
                        savedNewsEntity.description
                    ) == 0
                ) {
                    viewModel.repository.insertNewSavedNewsEntity(savedNewsEntity)
                } else {
                    viewModel.repository.deleteSavedNewsEntity(
                        viewModel.repository.getSavedNewsEntity(
                            savedNewsEntity.title,
                            savedNewsEntity.description
                        )
                    )
                }

                topicNewsRvAdapter.notifyItemChanged(position)
            }
        }, viewModel.repository)

        bannerRvAdapter = BannerRvAdapter(object : BannerRvAdapter.OnItemClickListener {
            override fun onSaveButtonClickListener(item: News, position: Int) {
                val stringBuilder = StringBuilder()

                item.category.forEachIndexed { index, it ->
                    if (index == item.category.size - 1) {
                        stringBuilder.append(it[0].uppercase() + it.substring(1))
                    } else {
                        stringBuilder.append("${it[0].uppercase() + it.substring(1)}, ")
                    }
                }

                val categoryString = stringBuilder.toString()
                val savedNewsEntity = SavedNewsEntity(
                    title = item.title,
                    description = item.description,
                    url = item.url,
                    imageUrl = item.image,
                    topic = categoryString
                )

                if (viewModel.repository.getCountThisSavedNewsEntity(
                        savedNewsEntity.title,
                        savedNewsEntity.description
                    ) == 0
                ) {
                    viewModel.repository.insertNewSavedNewsEntity(savedNewsEntity)
                } else {

                    viewModel.repository.deleteSavedNewsEntity(
                        viewModel.repository.getSavedNewsEntity(
                            savedNewsEntity.title,
                            savedNewsEntity.description
                        )
                    )
                }

                bannerRvAdapter.notifyItemChanged(position)

            }

            override fun onItemClickListener(item: News, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("news", item)
                findNavController().navigate(R.id.newsViewFragment, bundle)
            }
        }, viewModel.repository)

        newsRvAdapter = NewsRvAdapter(object : NewsRvAdapter.OnItemClickListener {
            override fun onItemClickListener(item: News, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("news", item)
                findNavController().navigate(R.id.newsViewFragment, bundle)
            }
        })

        binding.apply {
            bannerRv.adapter = bannerRvAdapter
            recommendedRv.adapter = newsRvAdapter
            topicNewsRv.adapter = topicNewsRvAdapter
        }

        return binding.root
    }

    private fun setUpRecommendedNews() {
        launch {
            val repository = viewModel.repository

            if (repository.getCountRecommendNewsFromDb() == 0) {
                viewModel.fetchRecommendedNews()

                viewModel.recommendedNewsFlow.collect {
                    if (it is RequestResult.Success) {
                        repository.insertRecommendedNewsToDb(it.data)
                        newsRvAdapter.submitList(it.data.news)
                        binding.apply {
                            progressRecommended.visibility = View.GONE
                            recommendedRv.visibility = View.VISIBLE
                            recommendSeeAllButton.visibility = View.VISIBLE
                        }
                    }
                }

            } else {
                viewModel.repository.getRecommendedNewsFromDb().collect {
                    newsRvAdapter.submitList(it.news)
                    binding.apply {
                        progressRecommended.visibility = View.GONE
                        recommendedRv.visibility = View.VISIBLE
                        recommendSeeAllButton.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    private fun setUpBannerNews() {
        launch {
            val repository = viewModel.repository

            if (repository.getCountBannerNewsInDb() == 0) {
                viewModel.fetchBannerNews()
                viewModel.bannerNewsFlow.collect {
                    if (it is RequestResult.Success) {
                        repository.insertToDbBannerNews(it.data)
                        bannerRvAdapter.submitList(it.data.news)
                        binding.apply {
                            progressBanner.visibility = View.GONE
                            bannerRv.visibility = View.VISIBLE
                        }
                    }
                }

            } else {
                viewModel.repository.getBannerNewsFromDb().collect {
                    bannerRvAdapter.submitList(it.news)
                    binding.apply {
                        progressBanner.visibility = View.GONE
                        bannerRv.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun setUpTopics() {
        launch {
            viewModel.getFavouritesList().collect {
                if (it is RequestResult.Success) {

                    val list = ArrayList<String>()
                    list.add("Random")
                    list.addAll(it.data.allTopics)


                    topicsRvAdapter = TopicsRvAdapter(list,
                        object : TopicsRvAdapter.OnItemClickListener {
                            override fun onItemClickListener(item: String, position: Int) {
                                val lastSelectedItem = topicsRvAdapter.activeItem
                                topicsRvAdapter.activeItem = position
                                topicsRvAdapter.notifyItemChanged(position)
                                topicsRvAdapter.notifyItemChanged(lastSelectedItem)
                                viewModel.fetchTopicNewsModel(item, 0)

                                binding.apply {
                                    recommendSeeAllButton.visibility = View.GONE
                                    topicNewsRvProgress.visibility = View.VISIBLE
                                    if (item == "Random") {
                                        topLayout.visibility = View.GONE
                                        bottomLayout.visibility = View.VISIBLE
                                    } else {
                                        topLayout.visibility = View.VISIBLE
                                        bottomLayout.visibility = View.GONE
                                        setUpTopicNewsRv()
                                    }
                                }

                            }
                        })

                    binding.topicsRv.adapter = topicsRvAdapter

                }
            }
        }
    }

    private fun setUpTopicNewsRv() {
        launch {
            binding.apply {
                viewModel.topicNewsFlow.collect {
                    when (it) {
                        is RequestResult.Loading -> {
                            topicNewsRvProgress.visibility = View.VISIBLE
                            topicNewsRv.visibility = View.GONE
                            recommendSeeAllButton.visibility = View.GONE
                        }

                        is RequestResult.Error -> {
                            topicNewsRv.visibility = View.GONE
                            topicNewsRvProgress.visibility = View.GONE
                            recommendSeeAllButton.visibility = View.GONE
                        }

                        is RequestResult.Success -> {
                            topicNewsRvAdapter.submitList(it.data.news)
                            topicNewsRv.visibility = View.VISIBLE
                            recommendSeeAllButton.visibility = View.VISIBLE
                            topicNewsRvProgress.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun setUpClicks() {
        binding.apply {

            micSearch.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("param1", "mic")

                findNavController().navigate(R.id.searchFragment, bundle)
            }

            searchLayout.setOnClickListener {
                findNavController().navigate(R.id.searchFragment)
            }


            seeAllButton.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("topic", viewModel.getTopicsList())
                findNavController().navigate(R.id.seeAllFragment, bundle)
            }

            recommendSeeAllButton.setOnClickListener {
                val bundle = Bundle()

                bundle.putString(
                    "topic",
                    if (topicsRvAdapter.list[topicsRvAdapter.activeItem] == "Random") viewModel.getTopicsList() else topicsRvAdapter.list[topicsRvAdapter.activeItem]
                )


                findNavController().navigate(R.id.seeAllFragment, bundle)
            }
        }
    }


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}