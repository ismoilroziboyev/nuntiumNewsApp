package uz.ismoilroziboyev.nuntium.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.ismoilroziboyev.nuntium.App
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.adapters.NewsOrgModelRvAdapter
import uz.ismoilroziboyev.nuntium.databinding.FragmentSearchBinding
import uz.ismoilroziboyev.nuntium.models.Article
import uz.ismoilroziboyev.nuntium.utils.RequestResult
import uz.ismoilroziboyev.nuntium.viewmodels.ViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


private const val ARG_PARAM1 = "param1"

class SearchFragment : Fragment(), CoroutineScope {
    private var param1: String? = null

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        App.appComponent.injectSearchFragment(this)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    private lateinit var binding: FragmentSearchBinding
    private lateinit var recommmendedRvAdapter: NewsOrgModelRvAdapter


    @Inject
    lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        if (param1 == "mic") {
            displaySpeechRecognizer()
            param1 = ""
        } else {
            binding.searchView.requestFocus()
            binding.searchView.isFocusableInTouchMode = true
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.searchView, 0)
        }

        recommmendedRvAdapter =
            NewsOrgModelRvAdapter(object : NewsOrgModelRvAdapter.OnItemClickListener {
                override fun onItemClickListener(item: Article, position: Int) {
                    val bundle = Bundle()
                    bundle.putSerializable("article", item)
                    findNavController().navigate(R.id.newsViewFragment, bundle)
                }
            })


        setUpSearchViewQuery()
        observeViewModel()




        binding.apply {
            newsRecyclerView.adapter = recommmendedRvAdapter
            backButton.setOnClickListener { findNavController().popBackStack() }


            micSearch.setOnClickListener { displaySpeechRecognizer() }

            root.addTransitionListener(object : MotionLayout.TransitionListener {

                override fun onTransitionStarted(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int
                ) {
                    if (startId == R.id.start) {
                        searchLayout.background = activity?.getDrawable(R.drawable.item_topic_back)
                    }
                }

                override fun onTransitionChange(
                    motionLayout: MotionLayout?,
                    startId: Int,
                    endId: Int,
                    progress: Float
                ) {

                }

                override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                    if (currentId == R.id.end) {
                        searchLayout.background = null
                    }
                }

                override fun onTransitionTrigger(
                    motionLayout: MotionLayout?,
                    triggerId: Int,
                    positive: Boolean,
                    progress: Float
                ) {

                }

            })
        }

        return binding.root
    }

    private fun observeViewModel() {
        launch {
            viewModel.searchedNewsFlow.collect {
                when (it) {
                    is RequestResult.Loading -> {
                        binding.apply {
                            placeHolder.text = "Search millions news"
                            progressBar.visibility = View.VISIBLE
                            placeHolder.visibility = View.GONE
                            newsRecyclerView.visibility = View.GONE
                        }
                    }

                    is RequestResult.Error -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            placeHolder.visibility = View.VISIBLE
                            placeHolder.text = it.message
                            newsRecyclerView.visibility = View.GONE
                        }
                    }

                    is RequestResult.Success -> {
                        binding.apply {
                            placeHolder.text = "Search millions news"
                            progressBar.visibility = View.GONE
                            placeHolder.visibility = View.GONE
                            recommmendedRvAdapter.submitList(it.data.articles)
                            newsRecyclerView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun setUpSearchViewQuery() {
        binding.apply {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (!(query == null || query == "")) {
                        progressBar.visibility = View.VISIBLE
                        placeHolder.visibility = View.GONE
                        newsRecyclerView.visibility = View.GONE
                        viewModel.fetchNewSearchedNews(query)
                    }
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    if (!(newText == null || newText == "")) {
                        progressBar.visibility = View.VISIBLE
                        placeHolder.visibility = View.GONE
                        newsRecyclerView.visibility = View.GONE
                        viewModel.fetchNewSearchedNews(newText)
                    }

                    return false
                }

            })
        }
    }

    private fun displaySpeechRecognizer() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
        }
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)

                var string = ""

                spokenText?.forEach { string = "$string $it" }

                binding.apply {
                    searchView.setQuery(string, true)
                }
            }
        }
    }
}