package com.cursofirebase.firebaseauthentification.ui.splash

import androidx.lifecycle.ViewModel
import com.cursofirebase.firebaseauthentification.data.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    fun isUserLogged(): Boolean {
        return authService.isUserLogged()
    }

}