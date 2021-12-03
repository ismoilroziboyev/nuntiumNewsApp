package uz.ismoilroziboyev.nuntium.adapters

import android.view.LayoutInflater
import android.view.View
import androidx.viewpager.widget.PagerAdapter
import android.view.ViewGroup
import uz.ismoilroziboyev.nuntium.databinding.ItemViewPagerBinding

class UltraPagerAdapter() : PagerAdapter() {

    override fun getCount(): Int = 5


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemPopularViewPagerBinding =
            ItemViewPagerBinding.inflate(LayoutInflater.from(container.context),
                container,
                false)


        itemPopularViewPagerBinding.apply {

        }

        container.addView(itemPopularViewPagerBinding.root)


        return itemPopularViewPagerBinding.root
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` == view
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}