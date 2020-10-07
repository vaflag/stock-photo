package com.vf.photobank.data.api.entity

import com.google.gson.annotations.SerializedName
import com.vf.photobank.domain.entity.Photo

data class CuratedPhotosResultPage(
    @SerializedName("page")
    val pageNumber: Int,
    @SerializedName("per_page")
    val numberPerPage: Int,
    val photos: List<Photo>,
    @SerializedName("next_page")
    val nextPageUrl: String
)
