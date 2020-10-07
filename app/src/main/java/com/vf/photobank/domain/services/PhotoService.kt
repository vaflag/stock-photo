package com.vf.photobank.domain.services

import com.vf.photobank.data.api.entity.CuratedPhotosResultPage
import com.vf.photobank.data.api.entity.SearchPhotosResultPage
import io.reactivex.Single

interface PhotoService {
    fun getCuratedPhotos(): Single<CuratedPhotosResultPage>
    fun getPhotosBySearch(query: String): Single<SearchPhotosResultPage>
}
