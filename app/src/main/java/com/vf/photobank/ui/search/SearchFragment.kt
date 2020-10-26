package com.vf.photobank.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vf.photobank.R
import com.vf.photobank.domain.entity.Photo
import com.vf.photobank.ui.ScrollableFragment
import com.vf.photobank.ui.home.PhotosAdapter
import com.vf.photobank.util.hide
import com.vf.photobank.util.hideKeyboard
import com.vf.photobank.util.show
import com.vf.photobank.util.showKeyboard
import com.vf.photobank.util.showSnackBar
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_footer.*
import kotlinx.android.synthetic.main.layout_search_bar.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Search fragment of the application.
 *
 * The search fragment allows the user to
 * retrieve a selection of images from a query.
 */
class SearchFragment(
    onPhotoClick: (Photo) -> Unit
) : Fragment(), ScrollableFragment {
    private val viewModel: SearchViewModel by viewModel()
    private val photosAdapter = PhotosAdapter(
        headerType = PhotosAdapter.HeaderType.SEARCH,
        onPhotoClick = onPhotoClick
    )
    private var nextPageLoading = false
    private lateinit var searchButton: SearchButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(
            view,
            savedInstanceState
        )
        setRecyclerView()
        setSwipeRefreshLayout()
        setSearchBar()
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
        layout_swipe_refresh.setOnRefreshListener {
            if (recycler_view_photos.isNotEmpty()) {
                refreshPhotos()
            } else {
                layout_swipe_refresh.isRefreshing = false
            }
        }
    }

    private fun setSearchBar() {
        searchButton = search_button
        searchButton.setOnClickListener { validateSearchInput() }
        layout_search_bar.setOnClickListener { edit_text_search.requestFocus() }
        image_view_clear.setOnClickListener { clearSearchText() }

        edit_text_search.apply {
            addTextChangedListener { onSearchTextChanged() }
            setOnEditorActionListener { _, actionId, _ ->
                return@setOnEditorActionListener when (actionId) {
                    IME_ACTION_DONE -> {
                        if (!searchButton.isLoading) {
                            onSearchInputValidated()
                            false
                        } else false
                    }
                    else -> false
                }
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    onSearchBarFocused()
                } else {
                    onSearchBarUnfocused()
                }
            }
        }
    }

    /**
     * Clears any text written in the search bar.
     */
    private fun clearSearchText() {
        edit_text_search.text.clear()
        edit_text_search.requestFocus()
    }

    /**
     * Fired when the text of the search bar is modified.
     */
    private fun onSearchTextChanged() {
        if (edit_text_search.text.toString().isNotBlank()) {
            image_view_clear.show()
            searchButton.isVisible = true
        } else {
            image_view_clear.hide()
            searchButton.isVisible = false
        }
    }

    /**
     * Fired when the search bar is focused.
     */
    private fun onSearchBarFocused() {
        edit_text_search.setSelection(edit_text_search.text.length)
        edit_text_search.showKeyboard()
        if (edit_text_search.text.isNotBlank()) {
            searchButton.isVisible = true
        }
    }

    /**
     * Fired when the search bar is not focused anymore.
     */
    private fun onSearchBarUnfocused() {
        edit_text_search.hideKeyboard()
        searchButton.isVisible = false
    }

    /**
     * Triggers the validation of search bar current entry.
     */
    private fun validateSearchInput() {
        onSearchInputValidated()
    }

    /**
     * Fired when the search bar entry is validated.
     */
    private fun onSearchInputValidated() {
        if (edit_text_search.text.isNotBlank()) {
            loadPhotosFromQuery(edit_text_search.text.toString())
        } else edit_text_search.text.clear()
        edit_text_search.clearFocus()
        edit_text_search.hideKeyboard()
        searchButton.isVisible = false
    }

    /**
     * Loads the first page of photos matching the search.
     *
     * @param query the searched term.
     */
    private fun loadPhotosFromQuery(query: String) {
        searchButton.isLoading = true
        viewModel.getSearchedPhotosFirstPage(query, ::onPhotosLoaded, ::onPhotoLoadingError)
    }

    private fun onPhotosLoaded(numberOfResults: Int, photos: List<Photo>) {
        if (layout_search_content.isVisible) layout_search_content.hide()
        displayResults(numberOfResults, photos)
        searchButton.isLoading = false
    }

    private fun onPhotoLoadingError() {
        searchButton.isLoading = false
        showSnackBar(
            constraint_layout_search,
            R.string.home_error_snack_bar,
            R.string.photo_snack_bar_hide
        ) {
            it.hide()
        }
    }

    /**
     * Loads the next page of photos.
     */
    private fun loadNextPage() {
        progress_bar_footer?.show()
        viewModel.getSearchedPhotosNextPage(::onNextPageLoaded, ::onNextPageLoadingError)
    }

    private fun onNextPageLoaded(photos: List<Photo>) {
        if (photos.isEmpty()) progress_bar_footer.hide()
        else {
            photosAdapter.addPhotos(photos)
            progress_bar_footer?.hide()
        }
        nextPageLoading = false
    }

    private fun onNextPageLoadingError() {
        showSnackBar(
            constraint_layout_search,
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
        viewModel.getSearchedPhotosFirstPage(
            onSuccess = ::onPhotosUpdated,
            onError = ::onPhotoUpdateError
        )
    }

    private fun onPhotosUpdated(numberOfResults: Int, photos: List<Photo>) {
        displayResults(numberOfResults, photos)
        layout_swipe_refresh.isRefreshing = false
    }

    private fun onPhotoUpdateError() {
        layout_swipe_refresh.isRefreshing = false
        showSnackBar(
            constraint_layout_search,
            R.string.home_error_snack_bar,
            R.string.home_error_snack_bar_button
        ) {
            refreshPhotos()
        }
    }

    private fun displayResults(numberOfResults: Int, photos: List<Photo>) {
        if (numberOfResults > 0) {
            if (layout_no_results.isVisible) layout_no_results.hide()
            if (!recycler_view_photos.isVisible) recycler_view_photos.show()
            recycler_view_photos.scrollToPosition(0)
            photosAdapter.numberOfResults = numberOfResults
            photosAdapter.setPhotos(photos)
        } else {
            layout_no_results.show(animated = true)
            recycler_view_photos.hide()
        }
    }

    override fun onPause() {
        edit_text_search.hideKeyboard()
        super.onPause()
    }

    override fun scrollToTop() {
        recycler_view_photos.smoothScrollToPosition(0)
    }
}
