package uz.ismoilroziboyev.nuntium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.databinding.ItemBannerRvBinding
import uz.ismoilroziboyev.nuntium.models.News
import uz.ismoilroziboyev.nuntium.repositories.Repository
import uz.ismoilroziboyev.nuntium.utils.setImageWithUrl
import java.lang.StringBuilder

class BannerRvAdapter(val onItemClickListener: OnItemClickListener, val repository: Repository) :
    ListAdapter<News, BannerRvAdapter.MyViewHolder>(MyDiffUtil()) {


    interface OnItemClickListener {
        fun onSaveButtonClickListener(item: News, position: Int)
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

    inner class MyViewHolder(val itemBannerRvBinding: ItemBannerRvBinding) :
        RecyclerView.ViewHolder(itemBannerRvBinding.root) {

        fun onBind(item: News, position: Int) {
            itemBannerRvBinding.apply {
                root.setOnClickListener { onItemClickListener.onItemClickListener(item, position) }
                saveButton.setOnClickListener {
                    onItemClickListener.onSaveButtonClickListener(
                        item,
                        position
                    )
                }

                if (repository.getCountThisSavedNewsEntity(item.title, item.description) == 0) {
                    saveButton.setImageResource(R.drawable.ic_save_button_white)
                } else {
                    saveButton.setImageResource(R.drawable.ic_bookmark_active_white)
                }

                titleTv.text = item.title
                imageView.setImageWithUrl(item.image)
                val stringBuilder = StringBuilder()
                item.category.forEachIndexed { index, it ->

                    if (index == item.category.size - 1) {
                        stringBuilder.append(it)
                    } else {
                        stringBuilder.append("$it, ")
                    }

                }
                val categoryString = stringBuilder.toString()
                topicTv.text = categoryString

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemBannerRvBinding.inflate(
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