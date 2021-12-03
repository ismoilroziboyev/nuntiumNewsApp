package uz.ismoilroziboyev.nuntium.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import uz.ismoilroziboyev.nuntium.R
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.ocnyang.pagetransformerhelp.cardtransformer.AlphaAndScalePageTransformer
import uz.ismoilroziboyev.nuntium.adapters.UltraPagerAdapter
import uz.ismoilroziboyev.nuntium.databinding.FragmentOnBoardingBinding


class OnBoardingFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,  /* forward= */true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
    }

    private lateinit var binding: FragmentOnBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        setUpViewPager()
        setUpClicks()
        return binding.root
    }

    private fun setUpClicks() {
        binding.apply {
            button.setOnClickListener {
                if (ultraViewpager.currentItem == 4) {
                    findNavController().navigate(R.id.welcomeFragment)
                } else {
                    ultraViewpager.setCurrentItem(ultraViewpager.currentItem + 1, true)
                }
            }
        }
    }

    private fun setUpViewPager() {
        binding.apply {
            val ultraViewPager = binding.ultraViewpager
            ultraViewpager.adapter = UltraPagerAdapter()
            ultraViewPager.offscreenPageLimit = 3
            ultraViewPager.pageMargin = 40
            ultraViewPager.setPageTransformer(true, AlphaAndScalePageTransformer())
            dotsIndicator.setViewPager(ultraViewPager)
        }
    }


}