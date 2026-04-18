package com.portfolio.walkdiary.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DiaryEntity::class], version = 5)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diaryDao(): DiaryDao
}