package uz.ismoilroziboyev.nuntium

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import uz.ismoilroziboyev.nuntium.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.my_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        binding.apply {
            bottomNavbar.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { controller, destination, arguments ->
                when (destination.id) {
                    R.id.home, R.id.categories, R.id.profile, R.id.saved -> {
                        bottomNavbar.visibility = View.VISIBLE
                    }

                    else -> {
                        bottomNavbar.visibility = View.GONE
                    }
                }
            }
        }
    }


    override fun onBackPressed() {

        val currentDestination = navController.currentDestination

        when (currentDestination?.id) {
            R.id.categories, R.id.saved, R.id.profile -> {
                navController.navigate(R.id.home)
            }

            R.id.home -> finish()

            else -> navController.popBackStack()
        }
    }
}