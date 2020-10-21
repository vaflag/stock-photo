package com.vf.photobank.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vf.photobank.R
import com.vf.photobank.domain.entity.Photo
import com.vf.photobank.util.hide
import com.vf.photobank.util.show
import com.vf.photobank.util.showSnackBar
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.item_footer.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Home fragment of the application.
 *
 * The home fragment displays a list of photo suggestions.
 */
class HomeFragment(
    onPhotoClick: (Photo) -> Unit
) : Fragment() {
    private val viewModel: HomeViewModel by viewModel()
    private val photosAdapter = PhotosAdapter(hasHeader = true, onPhotoClick = onPhotoClick)
    private var nextPageLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        setRecyclerView()
        setSwipeRefreshLayout()
        setErrorView()
        loadPhotos()
    }

    private fun setRecyclerView() {
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(
            2,
            StaggeredGridLayoutManager.VERTICAL
        )
        staggeredGridLayoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        staggeredGridLayoutManager.orientation = StaggeredGridLayoutManager.VERTICAL
        recycler_view_photos.layoutManager = staggeredGridLayoutManager
        recycler_view_photos.adapter = photosAdapter
        recycler_view_photos.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                // Triggers the loading of next page when bottom is reached.
                if (
                    !nextPageLoading &&
                    photosAdapter.hasItems() &&
                    !recyclerView.canScrollVertically(1)
                ) {
                    nextPageLoading = true
                    loadNextPage()
                }
            }
        })
    }

    private fun setSwipeRefreshLayout() {
        layout_swipe_refresh.setOnRefreshListener { refreshPhotos() }
    }

    private fun setErrorView() {
        layout_error.setOnClickListener { loadPhotos() }
    }

    /**
     * Loads the first page of suggested photos.
     */
    private fun loadPhotos() {
        layout_error.hide()
        progress_bar.show()
        viewModel.getSuggestedPhotosFirstPage(::onPhotosLoaded, ::onPhotoLoadingError)
    }

    private fun onPhotosLoaded(photos: List<Photo>) {
        layout_error.hide()
        photosAdapter.addPhotos(photos)
        progress_bar.hide()
    }

    private fun onPhotoLoadingError() {
        progress_bar.hide()
        layout_error.show()
    }

    /**
     * Loads the next page of suggested photos.
     */
    private fun loadNextPage() {
        progress_bar_footer?.show()
        viewModel.getSuggestedPhotosNextPage(::onNextPageLoaded, ::onNextPageLoadingError)
    }

    private fun onNextPageLoaded(photos: List<Photo>) {
        if (photos.isEmpty()) progress_bar_footer.hide()
        else {
            photosAdapter.addPhotos(photos)
            progress_bar_footer.hide()
        }
        nextPageLoading = false
    }

    private fun onNextPageLoadingError() {
        showSnackBar(
            constraint_layout_home,
            R.string.home_error_snack_bar,
            R.string.home_error_snack_bar_button
        ) {
            loadNextPage()
        }
        progress_bar_footer.hide()
        nextPageLoading = false
    }

    /**
     * Refreshes displayed photos.
     */
    private fun refreshPhotos() {
        viewModel.getSuggestedPhotosFirstPage(::onPhotosUpdated, ::onPhotoUpdateError)
    }

    private fun onPhotosUpdated(photos: List<Photo>) {
        photosAdapter.updatePhotos(photos)
        layout_error.hide()
        progress_bar.hide()
        layout_swipe_refresh.isRefreshing = false
    }

    private fun onPhotoUpdateError() {
        layout_swipe_refresh.isRefreshing = false
        showSnackBar(
            constraint_layout_home,
            R.string.home_error_snack_bar,
            R.string.home_error_snack_bar_button
        ) {
            refreshPhotos()
        }
    }
}
