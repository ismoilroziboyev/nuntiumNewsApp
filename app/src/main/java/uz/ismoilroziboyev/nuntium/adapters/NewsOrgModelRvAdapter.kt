package uz.ismoilroziboyev.nuntium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.ismoilroziboyev.nuntium.databinding.ItemRecommendedRvBinding
import uz.ismoilroziboyev.nuntium.databinding.ItemSearchResultRvBinding
import uz.ismoilroziboyev.nuntium.models.Article
import uz.ismoilroziboyev.nuntium.models.News
import uz.ismoilroziboyev.nuntium.utils.setImageWithUrl
import java.lang.StringBuilder

class NewsOrgModelRvAdapter(val onItemClickListener: OnItemClickListener) :
    ListAdapter<Article, NewsOrgModelRvAdapter.MyViewHolder>(MyDiffUtil()) {


    interface OnItemClickListener {
        fun onItemClickListener(item: Article, position: Int)
    }

    class MyDiffUtil : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url

        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem

        }

    }


    inner class MyViewHolder(private val itemRecommendedRvBinding: ItemSearchResultRvBinding) :
        RecyclerView.ViewHolder(itemRecommendedRvBinding.root) {

        fun onBind(item: Article, position: Int) {
            itemRecommendedRvBinding.apply {
                root.setOnClickListener { onItemClickListener.onItemClickListener(item, position) }
                imageView.setImageWithUrl(item.urlToImage ?: "")
                title.text = item.title
                descriptionTv.text = item.description
                topicTv.text = item.source.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemSearchResultRvBinding.inflate(
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