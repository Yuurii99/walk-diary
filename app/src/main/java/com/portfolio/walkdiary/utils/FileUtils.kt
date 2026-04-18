package com.portfolio.walkdiary.utils

import android.content.Context
import android.net.Uri
import java.io.File

object FileExec {
    // アプリ内部ストレージに写真をコピー
    fun saveUriToFile(context: Context, uri: Uri): String? {
        return try {
            // 1. ファイル名の生成
            val fileName = "DIARY_${System.currentTimeMillis()}.jpg"
            val destFile = File(context.filesDir, fileName)

            // 2. ストリームを開いてコピー
            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            destFile.absolutePath // 保存したパスを返す
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

