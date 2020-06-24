package com.francescoalessi.hacky.di

import androidx.lifecycle.ViewModel
import com.alexfacciorusso.daggerviewmodel.ViewModelKey
import com.francescoalessi.hacky.ui.viewcomment.CommentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class CommentViewModelModule
{
    @Binds
    @IntoMap
    @ViewModelKey(CommentViewModel::class)
    abstract fun bindsSearchViewModel(searchViewModel: CommentViewModel): ViewModel
}