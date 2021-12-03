package uz.ismoilroziboyev.nuntium.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.databinding.FragmentProfileBinding
import uz.ismoilroziboyev.nuntium.utils.MySharedPreferences
import vn.luongvo.widget.iosswitchview.SwitchView

class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    private lateinit var binding: FragmentProfileBinding
    private lateinit var mySharedPreferences: MySharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        mySharedPreferences = MySharedPreferences.getInstence(requireContext())

        binding.apply {
            switchview.isChecked = mySharedPreferences.isLightMode()

            languageButton.setOnClickListener {
                findNavController().navigate(R.id.languageFragment)
            }

            lightDarkModeButton.setOnClickListener {
                val boolean = !switchview.isChecked
                switchview.toggle(boolean)
                mySharedPreferences.setLightMode(boolean)
                if (boolean) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                }

            }
        }

        return binding.root
    }

}