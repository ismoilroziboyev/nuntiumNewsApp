package uz.ismoilroziboyev.nuntium.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.ismoilroziboyev.nuntium.R
import uz.ismoilroziboyev.nuntium.databinding.ItemTopicRvBinding

class SelectTopicRvAdapter(
    val list: List<String>,
    private val onItemClickListener: OnItemClickListener,
    val selectedItems: ArrayList<String>
) :
    RecyclerView.Adapter<SelectTopicRvAdapter.MyVIewHolder>() {


    interface OnItemClickListener {
        fun onItemClickListener(item: String, position: Int)
    }

    inner class MyVIewHolder(val itemTopicRvBinding: ItemTopicRvBinding) :
        RecyclerView.ViewHolder(itemTopicRvBinding.root) {

        fun onBind(item: String, position: Int) {
            itemTopicRvBinding.apply {
                titleTv.text = item[0].uppercase() + item.substring(1)
                root.setOnClickListener { onItemClickListener.onItemClickListener(item, position) }
                if (selectedItems.contains(item)) {
                    root.background = root.context.getDrawable(R.drawable.button_back)
                    titleTv.setTextColor(Color.WHITE)
                } else {
                    root.background = root.context.getDrawable(R.drawable.item_topic_back)
                    titleTv.setTextColor(root.context.getColor(R.color.secondary_txt_color))
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyVIewHolder {
        return MyVIewHolder(
            ItemTopicRvBinding.inflate(
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