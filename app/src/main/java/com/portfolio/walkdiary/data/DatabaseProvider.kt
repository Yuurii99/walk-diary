package com.portfolio.walkdiary.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    private var instance: AppDatabase? = null

    fun provideDatabase(context: Context): AppDatabase {
        // インスタンスがない場合は作成
        return instance ?: synchronized(this) {
            return instance ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "diary_db")
//            ).fallbackToDestructiveMigration(true)
                .build()
                .also { instance = it }
        }
    }
    fun provideDiaryDao(context: Context): DiaryDao {
        return provideDatabase(context).diaryDao()
    }

}