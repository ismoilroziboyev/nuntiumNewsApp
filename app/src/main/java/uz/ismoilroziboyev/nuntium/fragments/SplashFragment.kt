package uz.ismoilroziboyev.nuntium.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import uz.ismoilroziboyev.nuntium.App
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.databinding.FragmentSplashBinding
import uz.ismoilroziboyev.nuntium.utils.MySharedPreferences
import uz.ismoilroziboyev.nuntium.utils.RequestResult
import uz.ismoilroziboyev.nuntium.viewmodels.ViewModel
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class SplashFragment : Fragment(), CoroutineScope {


    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.injectSplashFragment(this)
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
    }

    private lateinit var binding: FragmentSplashBinding
    private lateinit var mySharedPreferences: MySharedPreferences
    private lateinit var mutableResult: MutableLiveData<Int>

    private val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    @Inject
    lateinit var viewModel: ViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)

        mySharedPreferences = MySharedPreferences.getInstence(requireContext())

        mutableResult = MutableLiveData(0)

        observeResult()

        val isfirstTime = mySharedPreferences.isFirstTime()

        if (isfirstTime) {
            navigateWelcomePage()
        } else {
            prepareNewsAndNavigateToHome()
        }

        return binding.root
    }

    private fun prepareNewsAndNavigateToHome() {
        setUpBannerNews()
        setUpRecommendedNews()
    }

    private fun setUpRecommendedNews() {
        viewModel.fetchRecommendedNews()

        launch {
            viewModel.recommendedNewsFlow.collect {
                when (it) {
                    is RequestResult.Error -> mutableResult.value = mutableResult.value!!.minus(1)
                    is RequestResult.Success -> {
                        viewModel.insertOrUpdateRecommendedNews(it.data)
                        mutableResult.value = mutableResult.value!!.plus(1)
                    }
                }
            }
        }
    }

    private fun setUpBannerNews() {
        viewModel.fetchBannerNews()
        launch {
            viewModel.bannerNewsFlow.collect {
                when (it) {
                    is RequestResult.Error -> mutableResult.value = mutableResult.value!!.minus(1)
                    is RequestResult.Success -> {
                        viewModel.insertOrUpdateBannerNews(it.data)
                        mutableResult.value = mutableResult.value!!.plus(1)
                    }
                }
            }
        }
    }


    private fun observeResult() {
        mutableResult.observe(viewLifecycleOwner, Observer {

            when (it!!) {
                -1, -2 -> {
                    binding.logoImg.visibility = View.GONE
                    binding.titleTv.text = "Something wnet wrong!"
                }

                2 -> {
                    findNavController().navigate(R.id.home)
                }
            }


        })
    }

    private fun navigateWelcomePage() {
        lifecycleScope.launch {
            delay(1500)
            findNavController().popBackStack()
            findNavController().navigate(R.id.onBoardingFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}