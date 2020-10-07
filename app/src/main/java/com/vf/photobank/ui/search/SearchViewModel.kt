package com.vf.photobank.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vf.photobank.data.api.entity.SearchPhotosResultPage
import com.vf.photobank.domain.services.PhotoService
import com.vf.photobank.util.Resource
import com.vf.photobank.util.applySchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class SearchViewModel(
    val photoService: PhotoService
) : ViewModel() {
    private var disposables = CompositeDisposable()
    private val _photoPage = MutableLiveData<Resource<SearchPhotosResultPage>>()
    val photoPage: MutableLiveData<Resource<SearchPhotosResultPage>>
        get() = _photoPage

    fun getPhotosBySearch(query: String) {
        photoService.getPhotosBySearch(query)
            .applySchedulers()
            .doOnSubscribe { _photoPage.value = Resource.Loading() }
            .subscribe(
                {
                    _photoPage.postValue(Resource.Success(it))
                },
                {
                    _photoPage.postValue(Resource.Fail(it))
                }
            )
            .addTo(disposables)
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
