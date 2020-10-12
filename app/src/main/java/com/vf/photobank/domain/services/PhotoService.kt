package com.vf.photobank.domain.services

import com.vf.photobank.data.api.entity.CuratedPhotosResultPage
import com.vf.photobank.data.api.entity.SearchPhotosResultPage
import io.reactivex.Single

interface PhotoService {
    /**
     * Get a page of curated photos from its number.
     *
     * @param page the page number.
     */
    fun getCuratedPhotos(page: Int): Single<CuratedPhotosResultPage>

    /**
     * Get a page of photos related to a query from its number.
     *
     * @param query the query.
     * @param page the page number.
     */
    fun getPhotosBySearch(query: String, page: Int): Single<SearchPhotosResultPage>
}
