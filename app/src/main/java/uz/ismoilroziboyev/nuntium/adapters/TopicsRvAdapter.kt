package uz.ismoilroziboyev.nuntium.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.databinding.ItemTopicRvHomeBinding

class TopicsRvAdapter(val list: List<String>, val onItemClickListener: OnItemClickListener) :
    RecyclerView.Adapter<TopicsRvAdapter.MyVIewHolder>() {

    var activeItem: Int = 0


    interface OnItemClickListener {
        fun onItemClickListener(item: String, position: Int)
    }

    inner class MyVIewHolder(val itemTopicRvBinding: ItemTopicRvHomeBinding) :
        RecyclerView.ViewHolder(itemTopicRvBinding.root) {

        fun onBind(item: String, position: Int) {
            itemTopicRvBinding.apply {
                root.setOnClickListener { onItemClickListener.onItemClickListener(item, position) }
                title.text = item[0].uppercase() + item.substring(1)
                if (position == activeItem) {
                    root.background = root.context.getDrawable(R.drawable.button_back)
                    title.setTextColor(Color.WHITE)
                } else {
                    root.background = root.context.getDrawable(R.drawable.item_topic_back)
                    title.setTextColor(root.context.getColor(R.color.secondary_txt_color))
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVIewHolder {
        return MyVIewHolder(
            ItemTopicRvHomeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: MyVIewHolder, position: Int) {

        holder.onBind(list[position], position)

    }


    override fun getItemCount(): Int = list.size
}