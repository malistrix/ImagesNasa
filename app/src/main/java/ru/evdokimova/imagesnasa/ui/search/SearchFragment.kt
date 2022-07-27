package ru.evdokimova.imagesnasa.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import ru.evdokimova.imagesnasa.R
import ru.evdokimova.imagesnasa.databinding.FragmentSearchBinding
import ru.evdokimova.imagesnasa.ui.adapters.ImagesAdapter
import ru.evdokimova.imagesnasa.ui.adapters.PageAdapter
import ru.evdokimova.imagesnasa.ui.adapters.PageChangeListener
import ru.evdokimova.imagesnasa.utils.Resource

private const val TAG = "SearchFragment "

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val mBinding get() = requireNotNull(_binding)

    private val viewModel by viewModels<SearchViewModel>()

    private lateinit var concatAdapter: ConcatAdapter

    private var isNeedScrollPage = true

    private var isNewSearch = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isNewSearch = false
        initAdapter(view)
        initScroll()
        initSearching(mBinding.searchView)
        initObserveImagesLiveData()
    }

    private fun initObserveImagesLiveData() {
        viewModel.imagesLiveData.observe(viewLifecycleOwner) { imageList ->
            when (imageList) {
                is Resource.Success -> {
                    mBinding.pagProgressBar.visibility = View.INVISIBLE

                    //Восстановление поискового запроса после запуска приложения
                    if (mBinding.searchView.query.isEmpty()) {
                        mBinding.searchView.setQuery(viewModel.page?.query ?: "", false)
                        mBinding.searchView.clearFocus()
                    }

                    imageList.data?.let {
                        (concatAdapter.adapters[0] as ImagesAdapter).differ.submitList(it)
                        val pageAdapter = concatAdapter.adapters[1] as PageAdapter
                        pageAdapter.submitData(viewModel.page)
                        pageAdapter.notifyDataSetChanged()
                    }
                }
                is Resource.Error -> {
                    mBinding.pagProgressBar.visibility = View.INVISIBLE
                    isNeedScrollPage = false
                    if (isNewSearch) {
                        Toast.makeText(requireContext(), imageList.message, Toast.LENGTH_LONG)
                            .show()
                        Log.e(TAG, "resourceError: ${imageList.message}")
                    } else {
                        //восстановление последних полученных данных
                        imageList.data?.let {
                            (concatAdapter.adapters[0] as ImagesAdapter).differ.submitList(it)
                            val pageAdapter = concatAdapter.adapters[1] as PageAdapter
                            pageAdapter.submitData(viewModel.page)
                            pageAdapter.notifyDataSetChanged()
                            Log.e(TAG, "load last data after error")
                        }
                    }
                }
                is Resource.Loading -> {
                    mBinding.pagProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun initAdapter(view: View) {
        val imagesAdapter = ImagesAdapter()
        imagesAdapter.setOnItemClickListener {
            val bundle = bundleOf("image" to it)
            view.findNavController().navigate(
                R.id.action_searchFragment_to_detailsFragment, bundle
            )
        }
        imagesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (isNeedScrollPage) {
                    isNeedScrollPage = false
                    images_rv.scrollToPosition(0)
                }
            }
        })

        val pageAdapter = PageAdapter(object : PageChangeListener {
            override fun onClickNext() {
                isNeedScrollPage = true
                viewModel.getImages(viewModel.page!!.query, viewModel.page!!.nextPage!!)
            }

            override fun onClickPrev() {
                isNeedScrollPage = true
                viewModel.getImages(viewModel.page!!.query, viewModel.page!!.prevPage!!)
            }
        })

        concatAdapter = ConcatAdapter(imagesAdapter, pageAdapter)
        mBinding.imagesRv.apply {
            adapter = concatAdapter
        }
    }

    private fun initScroll() {
        mBinding.imagesRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == RecyclerView.SCROLL_STATE_IDLE)
                    mBinding.pageScroll.visibility = View.INVISIBLE
                else
                    mBinding.pageScroll.visibility = View.VISIBLE
            }
        })
        mBinding.scrollDown.setOnClickListener {
            mBinding.imagesRv.scrollToPosition(concatAdapter.itemCount - 1)
        }
        mBinding.scrollUp.setOnClickListener {
            mBinding.imagesRv.scrollToPosition(0)
        }
    }

    private fun initSearching(search: SearchView) {
        search.apply {
            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        viewModel.getImages(query)
                        isNeedScrollPage = true
                        isNewSearch = true
                    }
                    //чтобы сворачивалась клава возвращаем false
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
            //Для клика по всему searchView, а не только по иконке
            setOnClickListener { isIconified = false }
            //Кнопка поиска внутри  searchView
            isSubmitButtonEnabled = true
        }
    }

}