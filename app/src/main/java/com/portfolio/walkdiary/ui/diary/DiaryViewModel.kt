package com.portfolio.walkdiary.ui.diary

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.portfolio.walkdiary.data.DatabaseProvider
import com.portfolio.walkdiary.data.DiaryEntity
import com.portfolio.walkdiary.utils.FileExec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryDao = DatabaseProvider.provideDiaryDao(application)

    suspend fun getDiary(id: Int): DiaryEntity? {
        return diaryDao.getDiaryById(id)
    }

    fun updateDiary(
        diaryID: Int,
        title: String,
        content: String?,
        imageUri: Uri?,
        timestamp: Long,
        existingFilePath: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // 新しい画像が選ばれた場合のみ保存、そうでなければ既存のパスを使用
            val updatedDiary = DiaryEntity(
                id = diaryID,
                title = title,
                content = content,
                timestamp = timestamp,
                latitude = null,
                longitude = null,
                // 新しい画像か既存の画像か判定
                filePath = imageUri?.let {
                    FileExec.saveUriToFile(getApplication(),
                        it) } ?: existingFilePath
            )

            diaryDao.updateDiary(updatedDiary)
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun saveDiary(title: String, content: String, imageUri: Uri?, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = imageUri?.let { FileExec.saveUriToFile(getApplication(), it) }

            val diary = DiaryEntity(
                title = title,
                content = content,
                filePath = filePath,
                timestamp = System.currentTimeMillis()
                // ここに LocationHelper から取得した緯度経度を渡す TODO
            )
            diaryDao.insertDiary(diary)

            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}