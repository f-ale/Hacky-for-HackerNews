package com.francescoalessi.hacky.di

import androidx.lifecycle.ViewModel
import com.alexfacciorusso.daggerviewmodel.ViewModelKey
import com.francescoalessi.hacky.ui.FrontpageViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FrontpageViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(FrontpageViewModel::class)
    abstract fun bindsSearchViewModel(searchViewModel: FrontpageViewModel): ViewModel
}