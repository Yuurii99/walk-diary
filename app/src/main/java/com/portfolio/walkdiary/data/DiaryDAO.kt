package com.portfolio.walkdiary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Insert
    suspend fun insertDiary(diary: DiaryEntity)

    @Query("SELECT * FROM diaries WHERE id = :id")
    suspend fun getDiaryById(id: Int): DiaryEntity?

    @Query("SELECT * FROM diaries ORDER BY timestamp DESC")
    fun getAllDiaries(): Flow<List<DiaryEntity>>

    @Update
    suspend fun updateDiary(diary: DiaryEntity)
    @Delete
    suspend fun deleteDiary(diary: DiaryEntity)
}