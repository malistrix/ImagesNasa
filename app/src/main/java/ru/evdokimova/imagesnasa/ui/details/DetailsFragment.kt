package ru.evdokimova.imagesnasa.ui.details

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import ru.evdokimova.imagesnasa.R
import ru.evdokimova.imagesnasa.databinding.FragmentDetailsBinding
import ru.evdokimova.imagesnasa.utils.DateFormatter

const val TAG = "DetailsFragment "

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val mBinding get() = requireNotNull(_binding)
    private val bundleAgrs: DetailsFragmentArgs by navArgs()
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)
        mBinding.toolbar.title = ""
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getHrefLargeImage(bundleAgrs.image.nasaId)
        initViews(view)
    }

    private fun initViews(view: View){

        val navController = findNavController()
        mBinding.toolbar.setupWithNavController(navController)
        mBinding.toolbar.title = ""

        val img = bundleAgrs.image
        Glide.with(this)
            .load(img.href)
            .placeholder(R.drawable.loading_animation)
            .error(R.drawable.ic_baseline_error_outline_24)
            .into(mBinding.iImage)

        mBinding.titleImage.text = img.title
        mBinding.descriptionImage.text = img.description
        mBinding.dateImage.text = DateFormatter.convertDateString(img.dateCreated)

        val linkImageAssets = getString(R.string.hyperlink, img.imageAssets).parseAsHtml()
        mBinding.assetsImage.text = linkImageAssets
        mBinding.assetsImage.movementMethod = LinkMovementMethod.getInstance()

        if (img.location.isNullOrEmpty()) {
            mBinding.locationImage.visibility = View.GONE
            mBinding.locationHeader.visibility = View.GONE
        } else {
            mBinding.locationImage.text = img.location
            mBinding.locationImage.visibility = View.VISIBLE
        }

        mBinding.iImage.setOnClickListener {
            val href = viewModel.href.ifEmpty { img.href }
            Log.d(TAG, "viewModel.href: ${viewModel.href}")
            Log.d(TAG, "href arg: $href")
            val bundle = bundleOf("href" to href)
            view.findNavController().navigate(
                R.id.action_detailsFragment_to_imageZoomFragment, bundle
            )
        }
    }
}