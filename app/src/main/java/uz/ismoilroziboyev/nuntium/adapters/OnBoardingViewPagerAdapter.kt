package uz.ismoilroziboyev.nuntium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.ismoilroziboyev.nuntium.databinding.ItemViewPagerBinding

class OnBoardingViewPagerAdapter : RecyclerView.Adapter<OnBoardingViewPagerAdapter.MyViewHolder>() {


    inner class MyViewHolder(val itemViewPagerBinding: ItemViewPagerBinding) :
        RecyclerView.ViewHolder(itemViewPagerBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemViewPagerBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = 5
}