package uz.ismoilroziboyev.nuntium.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.databinding.ItemTopicNewsRvBinding
import uz.ismoilroziboyev.nuntium.models.News
import uz.ismoilroziboyev.nuntium.repositories.Repository
import uz.ismoilroziboyev.nuntium.utils.setImageWithUrl

class TopicNewsRvAdapter(val onItemClickListener: OnItemClickListener, val repository: Repository) :
    ListAdapter<News, TopicNewsRvAdapter.MyViewHolder>(MyDiffUtil()) {


    interface OnItemClickListener {
        fun onItemClickListener(item: News, position: Int)
        fun onSaveButtonClickListener(item: News, position: Int)
    }

    class MyDiffUtil : DiffUtil.ItemCallback<News>() {

        override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem.id == newItem.id

        }

        override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
            return oldItem == newItem

        }

    }


    inner class MyViewHolder(val itemTopicNewsRvAdapter: ItemTopicNewsRvBinding) :
        RecyclerView.ViewHolder(itemTopicNewsRvAdapter.root) {

        fun onBind(item: News, position: Int) {
            itemTopicNewsRvAdapter.apply {
                root.setOnClickListener { onItemClickListener.onItemClickListener(item, position) }

                if (repository.getCountThisSavedNewsEntity(item.title, item.description) == 0) {
                    saveButton.setImageResource(R.drawable.ic_saved)
                } else {
                    saveButton.setImageResource(R.drawable.ic_bookmark_active)
                }

                saveButton.setOnClickListener {
                    onItemClickListener.onSaveButtonClickListener(
                        item,
                        position
                    )
                }



                imageView.setImageWithUrl(item.image)
                title.text = item.title


            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            ItemTopicNewsRvBinding.inflate(
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