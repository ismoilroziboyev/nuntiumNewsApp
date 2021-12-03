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
import uz.ismoilroziboyev.nuntium.databinding.FragmentCategoriesBinding
import uz.ismoilroziboyev.nuntium.databinding.FragmentSelectTopicBinding
import uz.ismoilroziboyev.nuntium.models.ResCategoriesModel
import uz.ismoilroziboyev.nuntium.utils.MySharedPreferences
import uz.ismoilroziboyev.nuntium.utils.RequestResult
import uz.ismoilroziboyev.nuntium.viewmodels.ViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class CategoriesFragment : Fragment(), CoroutineScope {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  /* forward= */true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
        App.appComponent.injectCategoriesFragment(this)
    }


    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var selectTopicRvAdapter: SelectTopicRvAdapter
    private val job = Job()
    private lateinit var sharedPreferences: MySharedPreferences

    @Inject
    lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        sharedPreferences = MySharedPreferences.getInstence(requireContext())
        setUpClicks()
        observe()

        return binding.root
    }

    private fun setUpClicks() {
        binding.apply {

            editButton.setOnClickListener {
                findNavController().navigate(R.id.selectTopicFragment)
            }

        }
    }

    private fun observe() {
        lifecycleScope.launch {

            viewModel.getFavouritesList().collect {
                if (it is RequestResult.Success) {
                    setUpData(it.data)
                }
            }
        }
    }


    private fun setUpData(data: FavouriteTopicsList) {
        binding.apply {
            selectTopicRvAdapter = SelectTopicRvAdapter(
                data.allTopics,
                object : SelectTopicRvAdapter.OnItemClickListener {
                    override fun onItemClickListener(item: String, position: Int) {
                        val bundle = Bundle()
                        bundle.putString("topic", item)
                        findNavController().navigate(R.id.seeAllFragment, bundle)
                    }
                }, ArrayList(data.selectedTopics)
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