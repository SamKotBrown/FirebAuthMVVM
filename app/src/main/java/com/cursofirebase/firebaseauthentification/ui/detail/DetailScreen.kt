package com.cursofirebase.firebaseauthentification.ui.detail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DetailScreen(navigateToLogin: () -> Unit) {
    val detailViewModel: DetailViewModel = hiltViewModel()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {
        val (btnLogout) = createRefs()
        LogoutButton(
            modifier = Modifier.constrainAs(btnLogout) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            },
            onClick = {
                detailViewModel.logout {
                    navigateToLogin()
                }
            })

    }
}

@Composable
fun LogoutButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    ) {
        Text(text = "LOGOUT")
    }
}