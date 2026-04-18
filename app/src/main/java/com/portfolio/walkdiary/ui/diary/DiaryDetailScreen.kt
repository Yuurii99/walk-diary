package com.portfolio.walkdiary.ui.diary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.portfolio.walkdiary.R
import com.portfolio.walkdiary.data.DiaryEntity
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(navController: NavController, diaryId: Int, viewModel: DiaryViewModel = viewModel()) {
    var diary by remember { mutableStateOf<DiaryEntity?>(null) }

    LaunchedEffect(diaryId) {
        diary = viewModel.getDiary(diaryId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日記詳細") },
                actions = {
                    IconButton(onClick = { navController.navigate("diary_edit/$diaryId") }) {
                        Icon(painterResource(R.drawable.edit), contentDescription = "編集")
                    }
                }
            )
        }
    ) { padding ->
        diary?.let { item ->
            Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
                if (!item.filePath.isNullOrEmpty()) {
                    AsyncImage(
                        model = item.filePath,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(16.dp))
                }
                Text(text = item.title, style = MaterialTheme.typography.headlineMedium)
                Text(text = "作成日: ${SimpleDateFormat("yyyy/MM/dd", Locale.JAPANESE).format(item.timestamp)}", style = MaterialTheme.typography.labelMedium)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text(text = item.content ?: "", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}