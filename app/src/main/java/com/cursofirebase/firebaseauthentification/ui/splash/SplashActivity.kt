package com.cursofirebase.firebaseauthentification.ui.splash

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.cursofirebase.firebaseauthentification.core.LoginNavigator
import com.cursofirebase.firebaseauthentification.core.Routes
import com.cursofirebase.firebaseauthentification.ui.theme.FirebaseAuthentificationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FirebaseAuthentificationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    navController = rememberNavController()
                    Text(text = "FIRE LOGIN")
                    NavigatorStart()
                }
            }
        }
    }

    @Composable
    private fun NavigatorStart() {
        if (splashViewModel.isUserLogged()) {
            LoginNavigator(
                navigationController = navController,
                modifier = Modifier,
                defaultDestination = Routes.Detail.route,
                activity = this
            )
        } else {
            LoginNavigator(
                navigationController = navController,
                modifier = Modifier, activity = this
            )
        }
    }
}