package com.francescoalessi.hacky.di

import android.content.Context
import androidx.room.Room
import com.francescoalessi.hacky.data.HackyDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {
    /*
    *   Provides Room database instance for injection
    */
    @Singleton
    @Provides
    fun providesRoomDatabase(context: Context) : HackyDatabase
    {
        return Room
            .databaseBuilder(
                context,
                HackyDatabase::class.java,
                "hacky-database")
            .fallbackToDestructiveMigration()
            .build()
    }
}