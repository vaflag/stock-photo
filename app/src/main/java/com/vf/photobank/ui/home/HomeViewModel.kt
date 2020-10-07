package com.vf.photobank.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vf.photobank.data.api.entity.CuratedPhotosResultPage
import com.vf.photobank.domain.services.PhotoService
import com.vf.photobank.util.Resource
import com.vf.photobank.util.applySchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

class HomeViewModel(
    private val photoService: PhotoService
) : ViewModel() {
    private var disposables = CompositeDisposable()
    private val _photoPage = MutableLiveData<Resource<CuratedPhotosResultPage>>()
    val photoPage: MutableLiveData<Resource<CuratedPhotosResultPage>>
        get() = _photoPage

    fun getCuratedPhotos() {
        photoService.getCuratedPhotos()
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
