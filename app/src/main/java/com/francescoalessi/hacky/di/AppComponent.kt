package com.francescoalessi.hacky.di

import com.alexfacciorusso.daggerviewmodel.DaggerViewModelInjectionModule
import com.francescoalessi.hacky.MainActivity
import com.francescoalessi.hacky.ui.FrontpageFragment
import com.francescoalessi.hacky.ui.viewcomment.ViewCommentsFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules =
    [
        DaggerViewModelInjectionModule::class,
        NetworkModule::class,
        AndroidModule::class,
        StorageModule::class,
        FrontpageViewModelModule::class,
        CommentViewModelModule::class
    ]
)
/*
 *  Dagger Dependency Injection main module
 */
interface AppComponent
{
    // Injection entry points
    fun inject(mainActivity: MainActivity)
    fun inject(frontpageFragment: FrontpageFragment)
    fun inject(commentsFragment: ViewCommentsFragment)
}