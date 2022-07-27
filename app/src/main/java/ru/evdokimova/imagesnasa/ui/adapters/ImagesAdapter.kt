package ru.evdokimova.imagesnasa.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_image.view.*
import ru.evdokimova.imagesnasa.R
import ru.evdokimova.imagesnasa.data.entity.ImageEntity


class ImagesAdapter : RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>() {

    inner class ImagesViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val callback = object : DiffUtil.ItemCallback<ImageEntity>() {

        override fun areItemsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean {
            return oldItem.nasaId == newItem.nasaId
        }

        override fun areContentsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        return ImagesViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        )
    }

    //Помощник для вычисления разницы между двумя списками DiffUtil в фоновом потоке.
    val differ = AsyncListDiffer(this, callback)


    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val image = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(image.href)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_baseline_error_outline_24)
                .into(i_image)
            i_image.clipToOutline = true
            title_image.text = image.title
            setOnClickListener {
                onItemClickListener?.let { it(image) }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((ImageEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (ImageEntity) -> Unit) {
        onItemClickListener = listener
    }


}