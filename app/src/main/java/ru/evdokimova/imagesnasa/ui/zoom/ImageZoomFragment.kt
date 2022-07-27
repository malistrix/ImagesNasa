package ru.evdokimova.imagesnasa.ui.zoom

import android.Manifest
import android.app.WallpaperManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.EncodeStrategy
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import ru.evdokimova.imagesnasa.R
import ru.evdokimova.imagesnasa.databinding.FragmentImageZoomBinding
import ru.evdokimova.imagesnasa.utils.hasWritePermission


private const val TAG = "ImageZoomFragment "

@AndroidEntryPoint
class ImageZoomFragment : Fragment(), MenuProvider {

    private var _binding: FragmentImageZoomBinding? = null
    private val mBinding get() = requireNotNull(_binding)

    private val bundleAgrs: ImageZoomFragmentArgs by navArgs()

    private val viewModel by viewModels<ZoomViewModel>()
    private lateinit var imgHref: String
    private lateinit var workManager: WorkManager

    private val checkPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.startDownloadImageWorker(workManager, imgHref)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        workManager = WorkManager.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageZoomBinding.inflate(layoutInflater, container, false)
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
        mBinding.toolbar.title = ""
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(mBinding.toolbar)
        }
        val navController = findNavController()
        mBinding.toolbar.setupWithNavController(navController)
        mBinding.toolbar.title = ""
        initPhotoView()
    }

    private fun initPhotoView() {
        imgHref = bundleAgrs.href.filterNot { it.isWhitespace() }.replace("http://", "https://")
        mBinding.photoView.setOnClickListener {
            if (mBinding.toolbar.visibility == View.VISIBLE)
                mBinding.toolbar.visibility = View.GONE
            else
                mBinding.toolbar.visibility = View.VISIBLE
        }

        try {
            val strategyDecodeNoCache = object : DiskCacheStrategy() {
                override fun isDataCacheable(dataSource: DataSource?) = false
                override fun isResourceCacheable(
                    isFromAlternateCacheKey: Boolean,
                    dataSource: DataSource?, encodeStrategy: EncodeStrategy?
                ) = false

                override fun decodeCachedResource() = true
                override fun decodeCachedData() = true
            }
            Glide.with(this)
                .load(imgHref).diskCacheStrategy(strategyDecodeNoCache)
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_baseline_error_outline_24)
                .into(mBinding.photoView)
        } catch (e: Exception) {
            Log.e(TAG, "${e.message} ${Log.getStackTraceString(e)}")
        }
    }

    private fun getSizeScreen(): Pair<Int, Int> {
        Resources.getSystem().displayMetrics
        val windowManager: WindowManager = requireActivity().windowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(metrics)
        Log.i(TAG, "metrics size ${metrics.widthPixels} ${metrics.heightPixels}")
        return Pair(metrics.widthPixels, metrics.heightPixels)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_zoom, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.use_wallpaper -> {
                setWallpaper()
                return true
            }
            R.id.download -> {
                if (requireContext().hasWritePermission()) {
                    viewModel.startDownloadImageWorker(workManager, imgHref)
                } else {
                    checkPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
        }
        return false
    }

    private fun setWallpaper() {
        val size = getSizeScreen()
        Glide.with(this)
            .asBitmap()
            .load(imgHref).centerCrop()
            .into(object : CustomTarget<Bitmap>(size.first, size.second) {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    try {
                        WallpaperManager.getInstance(requireContext()).setBitmap(resource)
                    } catch (e: Exception) {
                        Log.e(TAG, "Wallpaper ${e.message} ${Log.getStackTraceString(e)}")
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
    }
}