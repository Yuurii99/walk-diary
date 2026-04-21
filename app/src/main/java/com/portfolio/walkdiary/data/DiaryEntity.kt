package com.portfolio.walkdiary.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diaries")
data class DiaryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String?,
    val filePath: String?,
    val timestamp: Long,        // 作成日時
    val latitude: Double?,
    val longitude: Double?
)