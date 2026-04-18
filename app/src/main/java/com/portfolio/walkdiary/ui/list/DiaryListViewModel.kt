package com.portfolio.walkdiary.ui.list

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.portfolio.walkdiary.data.DatabaseProvider
import com.portfolio.walkdiary.data.DiaryEntity
import com.portfolio.walkdiary.utils.FileExec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryListViewModel(application: Application) : AndroidViewModel(application) {
    private val diaryDao = DatabaseProvider.provideDiaryDao(application)

    // 全ての日記を取得するFlow
    val allDiaries: Flow<List<DiaryEntity>> = diaryDao.getAllDiaries()

    // 保存ロジック（先ほど作成したもの）
    fun saveDiary(
        title: String,
        content: String,
        imageUri: Uri?,
        latitude: Double? = null,
        longitude: Double? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. 画像が選択されている場合、アプリ内専用フォルダにコピーして保存
                val savedFilePath = imageUri?.let { uri ->
                    FileExec.saveUriToFile(getApplication(), uri)
                }

                // 2. 新しい日記エンティティを作成
                val newDiary = DiaryEntity(
                    title = title,
                    content = content,
                    filePath = savedFilePath, // 保存に失敗した場合は null になる
                    timestamp = System.currentTimeMillis(),
                    latitude = latitude,
                    longitude = longitude
                )

                // 3. Room DB へインサート
                diaryDao.insertDiary(newDiary)

                // 4. UIスレッドに戻って成功後の処理（画面遷移など）を実行
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                val context = application.applicationContext
                Toast.makeText(context, "日記の保存に失敗しました", Toast.LENGTH_SHORT).show()
            }
        }
    }
}