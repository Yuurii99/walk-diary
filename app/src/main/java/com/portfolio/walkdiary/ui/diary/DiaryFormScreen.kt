package com.portfolio.walkdiary.ui.diary

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.portfolio.walkdiary.R
import com.portfolio.walkdiary.utils.location.LocationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryFormScreen(
    navController: NavController,
    diaryId: Int? = null,
    viewModel: DiaryViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    var isMapGesturing by remember { mutableStateOf(false) }

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
                markerPosition = LatLng(
                    // 初期値は東京駅
                    it.latitude ?: 35.68110358247781, it.longitude ?: 139.76707791348522)
            }
        }
    }


// ギャラリー選択用のランチャー
    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> selectedImageUri = uri }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (diaryId) {
                        null -> Text(stringResource(R.string.write_diary))
                        else -> Text(stringResource(R.string.update_diary))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.back)
                        )
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
                                R.string.please_title,
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                        when (diaryId) {
                            null -> { // 新規作成
                                viewModel.saveDiary(
                                    title,
                                    content ?: "",
                                    markerPosition,
                                    selectedImageUri,
                                ) {
                                    Toast.makeText(
                                        context,
                                        R.string.diary_saved,
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
                                    markerPosition,
                                    imageUri = selectedImageUri,
                                    timestamp = existingTimestamp,
                                    existingFilePath = existingFilePath,
                                ) {
                                    Toast.makeText(
                                        context,
                                        R.string.diary_updated,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack() // 一覧へ戻る
                                }
                            }
                        }
                    }
                ) {
                    when (diaryId) {
                        null -> Text(stringResource(R.string.save))
                        else -> Text(stringResource(R.string.update))
                    }
                }
            }
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(
                    scrollState,
                    enabled = !isMapGesturing
                ) // マップを触ってる間は!trueでスクロール無効
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // タイトル入力
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {keyboardController?.hide()}
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            // 本文入力
            OutlinedTextField(
                value = content ?: "",
                onValueChange = { content = it },
                label = { Text(stringResource(R.string.today_content)) },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {keyboardController?.hide()}
                ),
                modifier = Modifier.fillMaxWidth(),
                minLines = 5
            )

            // 写真プレビュー領域
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                // 写真が選択されていても、クリックで「選び直し」ができるように設定
                onClick = {
                    pickMedia.launch(
                        PickVisualMediaRequest
                            (ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
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
                            contentDescription = stringResource(R.string.selected_photo),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    // 編集画面で写真が既に登録されている
                    existingFilePath != null -> {
                        AsyncImage(
                            model = existingFilePath,
                            contentDescription = stringResource(R.string.selected_photo),
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
                                stringResource(R.string.select_photo),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                }
            }
            MapSection(
                selectedPosition = markerPosition,
                onPositionSelected = { markerPosition = it },
                modifier = Modifier.padding(vertical = 8.dp),
                onMapTouch = { isMapGesturing = it },
                context = context,
            )

        }
    }
}

@Composable
fun MapSection(
    selectedPosition: LatLng?,
    onPositionSelected: (LatLng) -> Unit,
    modifier: Modifier = Modifier,
    onMapTouch: (Boolean) -> Unit,
    context: Context,
) {
    // mapピン用ステート
    val markerState = rememberUpdatedMarkerState(
        position = selectedPosition ?: LatLng(35.6812, 139.7671)
    )
    // マップカメラ用ステート
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 12f)
    }

    // 位置情報取得
    val fetchAndSetCurrentLocation = {
        LocationHelper.getCurrentLocation(context) { loc ->
            if (loc != null) {
                onPositionSelected(loc)
            } else {
                Toast.makeText(
                    context,
                    context.getString(R.string.location_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
            fetchAndSetCurrentLocation()
        }
    }
    val settingLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fetchAndSetCurrentLocation()
        }
    }

    LaunchedEffect(selectedPosition) {
        selectedPosition?.let {
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
            markerState.position = it
        }
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        if (event.type == PointerEventType.Move) {
                            onMapTouch(true)
                        } else {
                            onMapTouch(false)
                        }
                    }
                }
            }
    ) {
        GoogleMap(
            modifier = modifier
                .fillMaxWidth(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                onPositionSelected(latLng)
            }
        ) {
            if (selectedPosition != null) {
                Marker(
                    state = markerState,
                    title = stringResource(R.string.save_current_location),
                    draggable = true,
                    onInfoWindowClose = { marker ->
                        onPositionSelected(marker.position)
                    }
                )
            }
        }
    }
    TextButton(
        onClick = {
            val hasPermission = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (hasPermission) {
                LocationHelper.checkLocationSettings(
                    context,
                    onEnabled = { // 権限OK、位置情報設定もON
                        fetchAndSetCurrentLocation()
                    }, onDisabled = { intentSenderRequest ->  // 権限OK、位置情報設定がOFF
                        settingLauncher.launch(intentSenderRequest)
                    })
            } else {
                permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
        }
    )
    {
        Icon(
            painterResource(R.drawable.location_on),
            contentDescription = stringResource(R.string.show_current_location)
        )
        Text(stringResource(R.string.use_current_location))
    }
}
