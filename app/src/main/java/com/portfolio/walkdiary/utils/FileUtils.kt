package com.portfolio.walkdiary.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object FileExec {
    // アプリ内部ストレージに写真をコピー
    fun saveUriToFile(context: Context, uri: Uri): String? {
        // 1. ファイル名の生成
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "diary_${timeStamp}_${UUID.randomUUID()}.jpg"

        val appStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File(appStorageDir, fileName)

        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

