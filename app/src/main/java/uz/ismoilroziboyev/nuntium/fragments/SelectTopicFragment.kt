package uz.ismoilroziboyev.nuntium.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.ismoilroziboyev.nuntium.App
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.adapters.SelectTopicRvAdapter
import uz.ismoilroziboyev.nuntium.database.entities.FavouriteTopicsList
import uz.ismoilroziboyev.nuntium.databinding.FragmentSelectTopicBinding
import uz.ismoilroziboyev.nuntium.models.ResCategoriesModel
import uz.ismoilroziboyev.nuntium.utils.MySharedPreferences
import uz.ismoilroziboyev.nuntium.utils.RequestResult
import uz.ismoilroziboyev.nuntium.viewmodels.ViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SelectTopicFragment : Fragment(), CoroutineScope {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  /* forward= */true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        App.appComponent.injectSelectFragment(this)
    }


    private lateinit var binding: FragmentSelectTopicBinding
    private lateinit var selectTopicRvAdapter: SelectTopicRvAdapter
    private lateinit var sharedPreferences: MySharedPreferences

    @Inject
    lateinit var viewModel: ViewModel

    private val TAG = "SelectTopicFragment"
    private val job = Job()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSelectTopicBinding.inflate(inflater, container, false)
        viewModel.fetchCategoriesList()
        sharedPreferences = MySharedPreferences.getInstence(requireContext())
        observeData()
        setUpClicks()

        return binding.root
    }

    private fun setUpClicks() {
        binding.apply {
            button.setOnClickListener {
                val selectedList = selectTopicRvAdapter.selectedItems

                if (selectedList.size < 3) {
                    Toast.makeText(
                        requireContext(),
                        "Please, select 3 topics at least!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val favouriteTopicsListModel = FavouriteTopicsList(
                        selectedTopics = selectedList,
                        allTopics = selectTopicRvAdapter.list
                    )
                    viewModel.updateOrInsertFavouriteTopics(favouriteTopicsListModel)
                    sharedPreferences.setIsFirstTime(false)
                    findNavController().navigate(R.id.splashFragment)
                }
            }
        }
    }

    private fun observeData() {
        launch {
            viewModel.categoriesStateflow.collect {
                when (it) {
                    is RequestResult.Error -> {
                        binding.progressLayout.visibility = View.GONE
                    }
                    is RequestResult.Loading -> {
                        Log.d(TAG, "observeData: Loading")
                    }
                    is RequestResult.Success -> {
                        setUpData(it.data)
                    }
                }
            }
        }
    }

    private fun getDbFavouriteTopicInDb(): ArrayList<String> {
        var list = ArrayList<String>()

        lifecycleScope.launch {

            viewModel.getFavouritesList().collect {
                when (it) {
                    is RequestResult.Error -> {
                        list = ArrayList()
                    }

                    is RequestResult.Success -> {
                        list = ArrayList(it.data.selectedTopics)
                    }
                }
            }

        }

        return list
    }

    private fun setUpData(data: ResCategoriesModel) {
        binding.apply {
            selectTopicRvAdapter = SelectTopicRvAdapter(
                data.categories,
                object : SelectTopicRvAdapter.OnItemClickListener {
                    override fun onItemClickListener(item: String, position: Int) {
                        val list = selectTopicRvAdapter.selectedItems

                        if (list.contains(item)) {
                            list.remove(item)
                        } else {
                            list.add(item)
                        }
                        selectTopicRvAdapter.notifyItemChanged(position)
                    }
                }, getDbFavouriteTopicInDb()
            )

            topicsRv.adapter = selectTopicRvAdapter

            progressLayout.visibility = View.GONE
            rvLayout.visibility = View.VISIBLE
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}