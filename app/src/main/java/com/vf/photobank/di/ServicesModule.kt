package com.vf.photobank.di

import com.vf.photobank.domain.services.PhotoService
import com.vf.photobank.repository.PhotoRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val servicesModule = module {
    single<PhotoService> {
        PhotoRepository(
            get(named(DI_RETROFIT_PEXELS))
        )
    }
}
