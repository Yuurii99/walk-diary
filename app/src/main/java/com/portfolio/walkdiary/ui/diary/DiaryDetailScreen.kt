package com.portfolio.walkdiary.ui.diary

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.portfolio.walkdiary.R
import com.portfolio.walkdiary.data.DiaryEntity
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryDetailScreen(
    navController: NavController,
    diaryId: Int,
    viewModel: DiaryViewModel = viewModel()
) {
    var diary by remember { mutableStateOf<DiaryEntity?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    LaunchedEffect(diaryId) {
        diary = viewModel.getDiary(diaryId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.diary_delete)) },
            text = { Text(stringResource(R.string.diary_delete_alert)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        diary?.let {
                            viewModel.deleteDiary(it) {
                                showDeleteDialog = false
                                navController.popBackStack() // 一覧へ戻る
                            }
                        }
                    }
                ) { Text("削除", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日記詳細") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(painterResource(R.drawable.arrow_back), stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("diary_edit/$diaryId") }) {
                        Icon(painterResource(R.drawable.edit), contentDescription = stringResource(R.string.edit))
                    }
                    // ★ 削除ボタン（ゴミ箱アイコン）
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            painterResource(R.drawable.delete),
                            contentDescription = stringResource(R.string.delete),
                            tint = Color.Red
                        )
                    }
                }
            )
        }
    ) { padding ->
        diary?.let { item ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (!item.filePath.isNullOrEmpty()) {
                    AsyncImage(
                        model = item.filePath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(16.dp))
                }
                Text(text = item.title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = stringResource(R.string.timestamp) + "${
                        SimpleDateFormat(
                            stringResource(R.string.yyyy_mm_dd),
                            Locale.JAPANESE
                        ).format(item.timestamp)
                    }", style = MaterialTheme.typography.labelMedium
                )
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Text(text = item.content ?: "", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.padding(all = 16.dp))
                MapSection(diary)
            }
        }
    }
}

@Composable
fun MapSection(diary: DiaryEntity?) {
    if (diary != null) {
        if (diary.latitude != null && diary.longitude != null) {
            val savedPos = LatLng(diary.latitude, diary.longitude)
            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(savedPos, 15f)
            }

            LaunchedEffect(savedPos) {
                cameraPositionState.move(CameraUpdateFactory.newLatLng(savedPos))
            }

            Text(stringResource(R.string.memory_place), style = MaterialTheme.typography.titleMedium)
            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                cameraPositionState = cameraPositionState,
                // 詳細画面では操作を制限する
                uiSettings = MapUiSettings(
                    scrollGesturesEnabled = false,
                    zoomGesturesEnabled = false,
                    myLocationButtonEnabled = false
                )
            ) {
                Marker(state = remember { MarkerState(position = savedPos) } )
            }
        }
    }
}