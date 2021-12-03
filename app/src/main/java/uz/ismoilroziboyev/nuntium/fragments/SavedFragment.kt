package uz.ismoilroziboyev.nuntium.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.ismoilroziboyev.nuntium.App
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.adapters.NewsRvAdapter
import uz.ismoilroziboyev.nuntium.databinding.FragmentSavedBinding
import uz.ismoilroziboyev.nuntium.models.News
import uz.ismoilroziboyev.nuntium.utils.RequestResult
import uz.ismoilroziboyev.nuntium.viewmodels.ViewModel
import javax.inject.Inject

class SavedFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  /* forward= */true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        App.appComponent.injectSavedFragment(this)
    }


    private lateinit var binding: FragmentSavedBinding
    private lateinit var newsRvAdapter: NewsRvAdapter

    @Inject
    lateinit var viewModel: ViewModel

    override fun onResume() {
        super.onResume()
        viewModel.fetchSavedNewsModels()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(inflater, container, false)

        newsRvAdapter = NewsRvAdapter(object : NewsRvAdapter.OnItemClickListener {
            override fun onItemClickListener(item: News, position: Int) {
                val bundle = Bundle()
                bundle.putSerializable("news", item)
                findNavController().navigate(R.id.newsViewFragment, bundle)
            }
        })

        binding.newsRecyclerView.adapter = newsRvAdapter

        observeNews()

        return binding.root
    }

    private fun observeNews() {
        lifecycleScope.launch {
            viewModel.savedNewsFlow.collect {
                when (it) {
                    is RequestResult.Loading -> {
                        binding.newsRecyclerView.visibility = View.GONE
                        binding.emptyLayout.visibility = View.VISIBLE

                    }

                    is RequestResult.Error -> {
                        binding.newsRecyclerView.visibility = View.GONE
                        binding.emptyLayout.visibility = View.GONE

                    }

                    is RequestResult.Success -> {
                        if (it.data.isNotEmpty()) {
                            val newsList = ArrayList<News>()

                            it.data.forEach {
                                newsList.add(
                                    News(
                                        "",
                                        listOf(it.topic),
                                        it.description,
                                        "",
                                        it.imageUrl,
                                        "",
                                        "",
                                        it.title,
                                        it.url
                                    )
                                )
                            }
                            newsRvAdapter.submitList(newsList)

                            binding.newsRecyclerView.visibility = View.VISIBLE
                            binding.emptyLayout.visibility = View.GONE

                        } else {
                            binding.newsRecyclerView.visibility = View.GONE
                            binding.emptyLayout.visibility = View.VISIBLE

                        }
                    }
                }
            }
        }
    }


}