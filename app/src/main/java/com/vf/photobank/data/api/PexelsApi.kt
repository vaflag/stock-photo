package com.vf.photobank.data.api

import com.vf.photobank.data.api.entity.CuratedPhotosResultPage
import com.vf.photobank.data.api.entity.SearchPhotosResultPage
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface PexelsApi {
    @GET("curated")
    fun getCuratedPhotos(
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Single<CuratedPhotosResultPage>

    @GET("search")
    fun getPhotosBySearch(
        @Query("query") query: String,
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Single<SearchPhotosResultPage>
}
