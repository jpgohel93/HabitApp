package com.tjcg.habitapp.hilt_modules

import android.content.Context
import androidx.room.Room
import com.tjcg.habitapp.data.HabitCalendarDao
import com.tjcg.habitapp.data.HabitDao
import com.tjcg.habitapp.data.HabitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideHabitDao(database : HabitDatabase) : HabitDao {
        return database.habitDao()
    }

    @Provides
    fun provideHabitCalendarDao(database: HabitDatabase) : HabitCalendarDao {
        return database.habitCalendarDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) : HabitDatabase {
        return Room
            .databaseBuilder(appContext, HabitDatabase::class.java, "HabitDatabase")
            .build()
    }
}