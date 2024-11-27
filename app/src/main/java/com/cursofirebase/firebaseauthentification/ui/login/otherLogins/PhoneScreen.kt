package com.cursofirebase.firebaseauthentification.ui.login.otherLogins

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cursofirebase.firebaseauthentification.ui.login.LoginViewModel
import com.ozcanalasalvar.otp_view.compose.OtpView


@Composable
fun PhoneScreen(modifier: Modifier = Modifier, navigateToDetail: () -> Unit, activity: Activity) {
    val loginViewModel: LoginViewModel = hiltViewModel()
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        Body(
            modifier = modifier,
            loginViewModel = loginViewModel,
            navigateToDetail,
            activity = activity
        )
    }
}

@Composable
private fun Body(
    modifier: Modifier,
    loginViewModel: LoginViewModel,
    navigateToDetail: () -> Unit,
    activity: Activity
) {

    var phoneNumber by remember {
        mutableStateOf("")
    }
    var enabledPin by remember {
        mutableStateOf(false)
    }
    var enabledAfterCode by remember {
        mutableStateOf(true)
    }

    val context = LocalContext.current

    val isLoading: Boolean by loginViewModel.isLoading.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = phoneNumber, onValueChange = { phoneNumber = it },
            modifier = modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            label = { Text(text = "input your phone") },
            singleLine = true,
            maxLines = 1,
            enabled = enabledAfterCode
        )

        PinViewComposable(
            modifier,
            loginViewModel = loginViewModel,
            navigateToDetail = navigateToDetail,
            enabled = enabledPin
        )

        Button(
            onClick = {
                loginViewModel.loginPhone(
                    phoneNumber,
                    onCodeSent = {
                        enabledPin = true
                    },
                    onVerificationFailed = {
                        Toast.makeText(context, "Excepción : ", Toast.LENGTH_SHORT).show()
                    },
                    onVerificationCompleted = { navigateToDetail() },
                    activity = activity
                )
                enabledAfterCode = false
            }, modifier = Modifier
                .fillMaxWidth(),
            enabled = enabledAfterCode
        ) {
            Text(text = "LOGIN")
        }

        if (isLoading) {
            CircularProgressIndicator()
        }
        if (phoneNumber.length == 12) {
            enabledPin = true
        }
    }
}

@Composable
private fun PinViewComposable(
    modifier: Modifier,
    loginViewModel: LoginViewModel,
    navigateToDetail: () -> Unit,
    enabled: Boolean
) {

    var otpCode by remember {
        mutableStateOf("")
    }

    OtpView(
        modifier = modifier.padding(bottom = 24.dp),
        enabled = enabled,
        value = otpCode,
        onTextChange = { text, _ ->
            otpCode = text
            if (text.length == 6) {
                loginViewModel.verifyCode(text) { navigateToDetail() }
            }
        }
    )
}