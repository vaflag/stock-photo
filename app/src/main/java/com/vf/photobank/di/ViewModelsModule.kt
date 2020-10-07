package com.vf.photobank.di

import com.vf.photobank.ui.home.HomeViewModel
import com.vf.photobank.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelsModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { SearchViewModel(get()) }
}
