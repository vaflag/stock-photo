package com.vf.photobank.util

open class Resource<out T : Any> {
    data class Success<out T : Any>(val data: T) : Resource<T>()
    data class Fail(val error: Throwable) : Resource<Nothing>()
    class Loading : Resource<Nothing>()
}
