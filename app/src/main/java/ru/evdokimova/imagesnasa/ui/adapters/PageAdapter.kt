package ru.evdokimova.imagesnasa.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.evdokimova.imagesnasa.R
import ru.evdokimova.imagesnasa.data.entity.PageEntity
import ru.evdokimova.imagesnasa.databinding.ItemPageBinding


class PageAdapter(private val pageChangeListener: PageChangeListener) :
    RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    private var pages: List<PageEntity> = listOf()

    fun submitData(pageEntity: PageEntity?) {
        pages = if(pageEntity == null) listOf() else listOf(pageEntity)
    }

    inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val binding = ItemPageBinding.bind(view)

        fun bind(page: PageEntity) = with(binding) {
            nextPage.visibility = if (page.nextPage == null) View.GONE else View.VISIBLE
            prevPage.visibility = if (page.prevPage == null) View.GONE else View.VISIBLE
            nextPage.setOnClickListener { pageChangeListener.onClickNext() }
            prevPage.setOnClickListener { pageChangeListener.onClickPrev() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        return PageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_page, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount(): Int {
        return pages.size
    }
}

interface PageChangeListener {
    fun onClickNext()
    fun onClickPrev()
}