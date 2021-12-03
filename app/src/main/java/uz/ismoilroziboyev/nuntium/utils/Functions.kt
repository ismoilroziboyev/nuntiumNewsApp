package uz.ismoilroziboyev.nuntium.utils

import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.bumptech.glide.Glide


val apiKey by lazy {
    "ylG7hJ-1nVaXwV2Ot_Vyls6mpiYWiN-Z0H3nUl0EQbUgH8u6"
}

fun ImageView.setImageWithUrl(url: String) {
    Glide.with(this).load(url).into(this)
}

fun dpToFloat(dp: Int, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 12f,
        context.resources.displayMetrics
    )
}

//fun backpressedHandler(fragment: Fragment): OnBackPressedCallback {
//    return object : OnBackPressedCallback(true) {
//        override fun handleOnBackPressed() {
//            findNavController(fragment).popBackStack()
//        }
//    }
//}