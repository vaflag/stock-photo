package com.vf.photobank.ui.home

import androidx.lifecycle.ViewModel
import com.vf.photobank.data.api.entity.CuratedPhotosResultPage
import com.vf.photobank.domain.entity.Photo
import com.vf.photobank.domain.services.PhotoService
import com.vf.photobank.util.applySchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class HomeViewModel(
    private val photoService: PhotoService
) : ViewModel() {
    private var disposables = CompositeDisposable()
    private var currentPhotoPage: CuratedPhotosResultPage? = null

    /**
     * Retrieves the first page of suggested photos from the API.
     *
     * @param onSuccess the action to be performed in case of success.
     * @param onError the action to be performed in case of error.
     */
    fun getSuggestedPhotosFirstPage(
        onSuccess: (List<Photo>) -> Unit,
        onError: () -> Unit
    ) {
        getSuggestedPhotosPage(
            1,
            onSuccess,
            onError
        )
    }

    /**
     * Retrieves the next page of suggested photos from the API.
     *
     * Invokes [onSuccess] with an empty list as parameter
     * if current page does not have a next page.
     *
     * @param onSuccess the action to be performed in case of success.
     * @param onError the action to be performed in case of error.
     */
    fun getSuggestedPhotosNextPage(
        onSuccess: (List<Photo>) -> Unit,
        onError: () -> Unit
    ) {
        if (!disposables.isDisposed) {
            currentPhotoPage?.let {
                if (it.nextPageUrl != null) {
                    getSuggestedPhotosPage(
                        it.pageNumber + 1,
                        onSuccess,
                        onError
                    )
                } else onSuccess.invoke(emptyList())
            }
        }
    }

    /**
     * Retrieves a given page of suggested photos from the API.
     *
     * @param page the page number.
     * @param onSuccess the action to be performed in case of success.
     * @param onError the action to be performed in case of error.
     */
    private fun getSuggestedPhotosPage(
        page: Int,
        onSuccess: (List<Photo>) -> Unit,
        onError: () -> Unit
    ) {
        photoService.getCuratedPhotos(page)
            .applySchedulers()
            .subscribe(
                {
                    currentPhotoPage = it
                    onSuccess.invoke(it.photos)
                },
                {
                    onError.invoke()
                }
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
