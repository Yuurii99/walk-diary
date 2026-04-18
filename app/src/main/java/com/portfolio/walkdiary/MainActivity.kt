package com.portfolio.walkdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.portfolio.walkdiary.ui.diary.DiaryDetailScreen
import com.portfolio.walkdiary.ui.diary.DiaryFormScreen
import com.portfolio.walkdiary.ui.list.DiaryListScreen
import com.portfolio.walkdiary.ui.theme.WalkDiaryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalkDiaryTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "diary_list" // 最初に表示する画面ID
                ) {
                    // 一覧画面
                    composable("diary_list") {
                        DiaryListScreen(navController = navController)
                    }
                    // 作成画面
                    composable("diary_form") {
                        DiaryFormScreen(navController)
                    }
                    // 詳細画面
                    composable("diary_detail/{diaryId}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("diaryId")?.toInt() ?: 0
                        DiaryDetailScreen(navController, id)
                    }
                    // 編集画面
                    composable("diary_detail/{diaryId}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("diaryId")?.toInt() ?: 0
                        DiaryFormScreen(navController, id)
                    }
                }
            }
        }
    }
}

