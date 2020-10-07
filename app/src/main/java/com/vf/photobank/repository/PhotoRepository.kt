package com.vf.photobank.repository

import com.vf.photobank.data.api.PexelsApi
import com.vf.photobank.data.api.entity.CuratedPhotosResultPage
import com.vf.photobank.data.api.entity.SearchPhotosResultPage
import com.vf.photobank.domain.services.PhotoService
import io.reactivex.Single
import retrofit2.Retrofit

class PhotoRepository(
    retrofit: Retrofit
) : PhotoService {
    private val api = retrofit.create(PexelsApi::class.java)

    override fun getCuratedPhotos(): Single<CuratedPhotosResultPage> {
        return api.getCuratedPhotos()
    }

    override fun getPhotosBySearch(query: String): Single<SearchPhotosResultPage> {
        return api.getPhotosBySearch(query)
    }
}
