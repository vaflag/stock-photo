package com.vf.photobank.di

import com.google.gson.GsonBuilder
import com.vf.photobank.BuildConfig.API_KEY
import com.vf.photobank.util.API_BASE_URL
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val DI_RETROFIT_PEXELS = "retrofitPexels"
private const val DI_OKHTTP_PEXELS = "okHttpPexels"
private const val DEFAULT_TIMEOUT_SECONDS = 30L

val networkModule = module {
    single<Retrofit>(named(DI_RETROFIT_PEXELS)) {
        Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(get<OkHttpClient>(named(DI_OKHTTP_PEXELS)))
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    single<OkHttpClient>(named(DI_OKHTTP_PEXELS)) {
        OkHttpClient.Builder()
            .writeTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .connectTimeout(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request = chain.request()
                    val newRequest = request.newBuilder().header(
                        "Authorization",
                        API_KEY
                    )
                    return chain.proceed(newRequest.build())
                }
            }
            )
            .build()
    }
}
