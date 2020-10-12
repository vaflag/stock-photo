package com.vf.photobank.repository

import com.vf.photobank.data.api.PexelsApi
import com.vf.photobank.data.api.entity.CuratedPhotosResultPage
import com.vf.photobank.data.api.entity.SearchPhotosResultPage
import com.vf.photobank.domain.services.PhotoService
import com.vf.photobank.util.PHOTOS_PER_PAGE
import io.reactivex.Single
import retrofit2.Retrofit

class PhotoRepository(
    retrofit: Retrofit
) : PhotoService {
    private val api = retrofit.create(PexelsApi::class.java)

    override fun getCuratedPhotos(page: Int): Single<CuratedPhotosResultPage> {
        return api.getCuratedPhotos(PHOTOS_PER_PAGE, page)
    }

    override fun getPhotosBySearch(query: String, page: Int): Single<SearchPhotosResultPage> {
        return api.getPhotosBySearch(query, PHOTOS_PER_PAGE, page)
    }
}
