package com.cursofirebase.firebaseauthentification.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.cursofirebase.firebaseauthentification.ui.theme.Splash

@Composable
fun SignupScreen(
    navigateToDetail: () -> Unit
) {
    // Solución a error de "by viewModels() no disponible":
    // https://stackoverflow.com/questions/78388748/viewmodels-not-found-in-jetpack-compose

    val signupViewModel: SignupViewModel = hiltViewModel()
    val isLoading: Boolean by signupViewModel.isLoading.collectAsState()

    var mailText by remember {
        mutableStateOf("")
    }
    var pasText by remember {
        mutableStateOf("")
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Splash)
            .padding(horizontal = 12.dp)
    ) {
        val (mailField, passwField, btnRegister, prbLoading) = createRefs()
        createVerticalChain(mailField, passwField, chainStyle = ChainStyle.Packed)
        TextField(
            value = mailText, onValueChange = { mailText = it },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(mailField) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(passwField.top)
                },
            label = { Text(text = "Input your mail") }
        )
        TextField(
            value = pasText, onValueChange = { pasText = it },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(passwField) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(mailField.bottom)
                },
            label = { Text(text = "input your password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Button(onClick = {
            signupViewModel.register(mailText, pasText) {
                navigateToDetail()
            }
        }, modifier = Modifier
            .fillMaxWidth()
            .constrainAs(btnRegister) {
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        ) {
            Text(text = "REGISTER")
        }
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.constrainAs(prbLoading) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            })
        }
    }
}