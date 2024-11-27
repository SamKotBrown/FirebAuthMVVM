package com.cursofirebase.firebaseauthentification.ui.detail

import androidx.lifecycle.ViewModel
import com.cursofirebase.firebaseauthentification.data.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    fun logout(navigateToLogin: () -> Unit) {
        // Al hacer el logout hay que borrar todos los datos que tuviese el usuario que ha hecho
        // logout. Luego cuando se vuelva a logear otro usuario, habría que cargar los datos
        // pertinents a ese usuario
        authService.logout()
        navigateToLogin()
    }

}