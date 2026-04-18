package com.portfolio.walkdiary.ui.diary

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.portfolio.walkdiary.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryFormScreen(
    navController: NavController,
    diaryId: Int? = null,
    viewModel: DiaryViewModel = viewModel()
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var content: String? by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var existingTimestamp by remember { mutableLongStateOf(0L) }
    var existingFilePath by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(diaryId) {
        if (diaryId != null) {
            viewModel.getDiary(diaryId)?.let {
                title = it.title
                content = it.content
                existingTimestamp = it.timestamp
                existingFilePath = it.filePath
            }
        }
    }

    // ギャラリー選択用のランチャー
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> selectedImageUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日記を書く") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(R.drawable.arrow_back), "戻る")
                    }
                }
            )
        },
        bottomBar = {


            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = {
                        if (title.isBlank()) {
                            Toast.makeText(
                                context,
                                "タイトルを入力してください",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        when (diaryId) {
                            null -> { // 新規作成
                                viewModel.saveDiary(title, content ?: "", selectedImageUri) {
                                    Toast.makeText(
                                        context,
                                        "日記を保存しました",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack() // 一覧へ戻る
                                }
                            }

                            else -> { // 編集
                                viewModel.updateDiary(
                                    diaryId,
                                    title,
                                    content ?: "",
                                    imageUri = selectedImageUri,
                                    timestamp = existingTimestamp,
                                    existingFilePath = existingFilePath
                                ) {
                                    Toast.makeText(
                                        context,
                                        "日記を編集しました！",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    navController.popBackStack() // 一覧へ戻る
                                }
                            }
                        }
                    }
                )
                {
                    Text("日記を保存する")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // タイトル入力
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("タイトル") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 本文入力
                OutlinedTextField(
                    value = content ?: "",
                    onValueChange = { content = it },
                    label = { Text("今日のできごと") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 5
                )

                // 写真プレビュー領域
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    // 写真が選択されていても、クリックで「選び直し」ができるように設定
                    onClick = { galleryLauncher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    when {
                        // 写真を選択したなら
                        selectedImageUri != null -> {
                            AsyncImage(
                                model = selectedImageUri ?: existingFilePath,
                                contentDescription = "選択された写真",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // 編集画面で写真が既に登録されている
                        existingFilePath != null -> {
                            AsyncImage(
                                model = existingFilePath,
                                contentDescription = "選択された写真",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        // 写真が何もないなら
                        else -> {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add_photo_alternate),
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "写真をえらぶ",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                    }
                }
            }
        }
    }
}