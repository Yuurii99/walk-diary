package com.portfolio.walkdiary.ui.diary

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.portfolio.walkdiary.data.DatabaseProvider
import com.portfolio.walkdiary.data.DiaryEntity
import com.portfolio.walkdiary.utils.FileExec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DiaryViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryDao = DatabaseProvider.provideDiaryDao(application)

    suspend fun getDiary(id: Int): DiaryEntity? {
        return diaryDao.getDiaryById(id)
    }

    fun updateDiary(
        diaryID: Int,
        title: String,
        content: String?,
        markerPosition: LatLng?,
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
                latitude = markerPosition?.latitude,
                longitude = markerPosition?.longitude,
                // 新しい画像か既存の画像か判定
                filePath = imageUri?.let {
                    FileExec.saveUriToFile(
                        getApplication(),
                        it
                    )
                } ?: existingFilePath
            )

            diaryDao.updateDiary(updatedDiary)
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun saveDiary(
        title: String,
        content: String,
        markerPosition: LatLng?,
        imageUri: Uri?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val filePath = imageUri?.let { FileExec.saveUriToFile(getApplication(), it) }

            val diary = DiaryEntity(
                title = title,
                content = content,
                latitude = markerPosition?.latitude,
                longitude = markerPosition?.longitude,
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

    fun deleteDiary(
        diary: DiaryEntity,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. 画像ファイルがあれば削除
            diary.filePath?.let { path ->
                val file = File(path)
                if (file.exists()) file.delete()
            }

            // 2. DBから削除
            diaryDao.deleteDiary(diary)

            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }
}