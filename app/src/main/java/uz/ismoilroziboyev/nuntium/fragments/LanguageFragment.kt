package uz.ismoilroziboyev.nuntium.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.yariksoffice.lingver.Lingver
import uz.ismoilroziboyev.nuntium.App
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.databinding.FragmentLanguageBinding
import uz.ismoilroziboyev.nuntium.utils.MySharedPreferences
import uz.ismoilroziboyev.nuntium.viewmodels.ViewModel
import javax.inject.Inject

class LanguageFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }


    private lateinit var binding: FragmentLanguageBinding
    private lateinit var mySharedPreferences: MySharedPreferences
    private lateinit var language: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLanguageBinding.inflate(inflater, container, false)
        mySharedPreferences = MySharedPreferences.getInstence(requireContext())


        binding.apply {
            backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            setUi()

            englishButton.setOnClickListener {
                mySharedPreferences.setLanguageNews("English")
                Lingver.getInstance().setLocale(requireContext(), "en")
                setUi()
            }

            russianButton.setOnClickListener {
                mySharedPreferences.setLanguageNews("Russian")
                Lingver.getInstance().setLocale(requireContext(), "ru")
                setUi()
            }
        }

        return binding.root
    }

    private fun setUi() {
        language = mySharedPreferences.getLanguageNews()

        binding.apply {

            title.text = context?.getString(R.string.language)
            englishTxt.text = context?.getString(R.string.english)
            russianTxt.text = context?.getString(R.string.russian)


            if (language == "English") {
                englishButton.background = context?.getDrawable(R.drawable.button_back)
                englishTxt.setTextColor(context?.getColor(R.color.white)!!)
                englishTick.visibility = View.VISIBLE
            } else {
                englishButton.background = context?.getDrawable(R.drawable.item_topic_back)
                englishTxt.setTextColor(context?.getColor(R.color.secondary_txt_color)!!)
                englishTick.visibility = View.GONE
            }


            if (language == "Russian") {
                russianButton.background = context?.getDrawable(R.drawable.button_back)
                russianTxt.setTextColor(context?.getColor(R.color.white)!!)
                tickButton.visibility = View.VISIBLE
            } else {
                russianButton.background = context?.getDrawable(R.drawable.item_topic_back)
                russianTxt.setTextColor(context?.getColor(R.color.secondary_txt_color)!!)
                tickButton.visibility = View.GONE
            }
        }
    }

}