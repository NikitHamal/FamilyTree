package com.famy.tree

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.famy.tree.presentation.FamyApp
import com.famy.tree.presentation.FamyViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: FamyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            FamyApp(viewModel = viewModel)
        }
    }
}
