package com.vf.photobank.ui.search

import androidx.lifecycle.ViewModel
import com.vf.photobank.data.api.entity.SearchPhotosResultPage
import com.vf.photobank.domain.entity.Photo
import com.vf.photobank.domain.services.PhotoService
import com.vf.photobank.util.applySchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class SearchViewModel(
    private val photoService: PhotoService
) : ViewModel() {
    private var disposables = CompositeDisposable()
    private var currentPhotoPage: SearchPhotosResultPage? = null
    private var currentQueryTerm: String = ""

    /**
     * Retrieves the first page of searched photos from the API.
     *
     * @param query the search query term.
     * @param onSuccess the action to be performed in case of success.
     * @param onError the action to be performed in case of error.
     */
    fun getSearchedPhotosFirstPage(
        query: String = currentQueryTerm,
        onSuccess: (totalResults: Int, results: List<Photo>) -> Unit,
        onError: () -> Unit
    ) {
        currentQueryTerm = query

        photoService.getPhotosBySearch(query, page = 1)
            .applySchedulers()
            .subscribe(
                {
                    currentPhotoPage = it
                    onSuccess.invoke(it.numberOfResults, it.photos)
                },
                {
                    onError.invoke()
                }
            )
            .addTo(disposables)
    }

    /**
     * Retrieves the next page of searched photos from the API.
     *
     * Invokes [onSuccess] with an empty list as parameter
     * if current page does not have a next page.
     *
     * @param onSuccess the action to be performed in case of success.
     * @param onError the action to be performed in case of error.
     */
    fun getSearchedPhotosNextPage(
        onSuccess: (List<Photo>) -> Unit,
        onError: () -> Unit
    ) {
        if (!disposables.isDisposed) {
            currentPhotoPage?.let {
                if (it.nextPageUrl != null) {
                    photoService.getPhotosBySearch(
                        currentQueryTerm,
                        page = it.pageNumber + 1
                    )
                        .applySchedulers()
                        .subscribe(
                            { result ->
                                currentPhotoPage = result
                                onSuccess.invoke(result.photos)
                            },
                            {
                                onError.invoke()
                            }
                        )
                        .addTo(disposables)
                } else onSuccess.invoke(emptyList())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
