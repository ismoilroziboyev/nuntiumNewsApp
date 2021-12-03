package uz.ismoilroziboyev.nuntium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.ismoilroziboyev.nuntium.databinding.ItemRecommendedRvBinding
import uz.ismoilroziboyev.nuntium.models.News
import uz.ismoilroziboyev.nuntium.utils.setImageWithUrl
import java.lang.StringBuilder

class NewsRvAdapter(val onItemClickListener: OnItemClickListener) :
    ListAdapter<News, NewsRvAdapter.MyViewHolder>(MyDiffUtil()) {


    interface OnItemClickListener {
        fun onItemClickListener(item: News, position: Int)
    }

    class MyDiffUtil : DiffUtil.ItemCallback<News>() {

        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem

        }

    }


    inner class MyViewHolder(val itemRecommendedRvBinding: ItemRecommendedRvBinding) :
        RecyclerView.ViewHolder(itemRecommendedRvBinding.root) {

        fun onBind(item: News, position: Int) {
            itemRecommendedRvBinding.apply {
                root.setOnClickListener { onItemClickListener.onItemClickListener(item, position) }

                imageView.setImageWithUrl(item.image)
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
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.onBind(getItem(position), position)
    }
}