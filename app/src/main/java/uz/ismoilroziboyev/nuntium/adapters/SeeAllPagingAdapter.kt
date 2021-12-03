package uz.ismoilroziboyev.nuntium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.ismoilroziboyev.nuntium.databinding.ItemRecommendedRvBinding
import uz.ismoilroziboyev.nuntium.models.News
import uz.ismoilroziboyev.nuntium.utils.setImageWithUrl
import java.lang.StringBuilder

class SeeAllPagingAdapter(val list: ArrayList<News>, val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<SeeAllPagingAdapter.MyViewHolder>() {

    interface OnItemClickListener {
        fun onItemClickListener(item: News, position: Int)
        fun bottomReachedListener(nextPage: Int)
    }

    inner class MyViewHolder(val itemRecommendedRvBinding: ItemRecommendedRvBinding) :
        RecyclerView.ViewHolder(itemRecommendedRvBinding.root) {
        fun onBind(item: News, position: Int) {
            itemRecommendedRvBinding.apply {
                imageView.setImageWithUrl(item.image)

                root.setOnClickListener { onItemClickListener.onItemClickListener(item, position) }

                title.text = item.title

                val stringBuilder = StringBuilder()
                item.category.forEachIndexed { index, it ->

                    if (index == item.category.size - 1) {
                        stringBuilder.append(it[0].uppercase() + it.substring(1))
                    } else {
                        stringBuilder.append("${it[0].uppercase() + it.substring(1)}, ")
                    }

                }
                val categoryString = stringBuilder.toString()
                topicTv.text = categoryString
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemRecommendedRvBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(list[position], position)
        if (position == list.size - 1) onItemClickListener.bottomReachedListener(position / 10 + 1)
    }

    fun addItems(otherList: List<News>) {
        val position = list.size
        list.addAll(otherList)
        notifyItemRangeInserted(position, otherList.size)
    }

    override fun getItemCount(): Int = list.size
}