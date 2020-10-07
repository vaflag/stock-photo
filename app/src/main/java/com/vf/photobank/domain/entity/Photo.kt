package com.vf.photobank.domain.entity

data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val photographer: String,
    val src: PhotoSources
)
