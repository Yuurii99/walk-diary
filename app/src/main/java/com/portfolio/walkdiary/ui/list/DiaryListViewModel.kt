package com.portfolio.walkdiary.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.portfolio.walkdiary.data.DatabaseProvider
import com.portfolio.walkdiary.data.DiaryEntity
import kotlinx.coroutines.flow.Flow

class DiaryListViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryDao = DatabaseProvider.provideDiaryDao(application)

    // 全ての日記を取得するFlow
    val allDiaries: Flow<List<DiaryEntity>> = diaryDao.getAllDiaries()
}