package com.cursofirebase.firebaseauthentification.ui.login

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.cursofirebase.firebaseauthentification.R
import com.cursofirebase.firebaseauthentification.ui.theme.Splash

@Composable
fun LoginScreen(
    navigateToDetail: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToPhoneLogin: () -> Unit,
    activity: Activity
) {

    val loginViewModel: LoginViewModel = hiltViewModel()

    var mailText by remember {
        mutableStateOf("")
    }
    var pasText by remember {
        mutableStateOf("")
    }

    val isLoading: Boolean by loginViewModel.isLoading.collectAsState()

    val context = LocalContext.current

    val scrollState = rememberScrollState()

    // Imagen, campo usuario, contraseña, boton login, registro

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Splash)
            .padding(horizontal = 12.dp)
    ) {
        val (image, mailField, passwField,
            regText, btColumn, prbLoading) = createRefs()

        createVerticalChain(
            image, mailField, passwField, regText, btColumn,
            chainStyle = ChainStyle.Packed
        )

        ImageComposable(
            Modifier
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(mailField.top)
                }
                .padding(bottom = 64.dp)
        )
        TextField(
            value = mailText, onValueChange = { mailText = it },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(mailField) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(image.bottom)
                    bottom.linkTo(passwField.top)
                },
            label = { Text(text = "input your mail") }
        )
        TextField(
            value = pasText, onValueChange = { pasText = it },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(passwField) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(mailField.bottom)
                    bottom.linkTo(btColumn.top)
                },
            label = { Text(text = "input your password") },
            visualTransformation = PasswordVisualTransformation()
        )
        Text(text = "No tiene cuenta, registrese",
            modifier = Modifier
                .constrainAs(regText) {
                    end.linkTo(parent.end)
                    top.linkTo(passwField.bottom)
                }
                .clickable {
                    navigateToRegister()
                })

        Column(
            Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .constrainAs(btColumn) {
                    top.linkTo(regText.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LoginButton(
                modifier = Modifier,
                onClick = {
                    loginViewModel.login(mailText, pasText) {
                        navigateToDetail()
                    }
                }
            )
            PhoneButton(
                modifier = Modifier,
                onClick = {
                    navigateToPhoneLogin()
                })
            GoogleButton(
                modifier = Modifier,
                onClick = {
                    loginViewModel.doGoogleSignIn(context, navigateToDetail)
                })
            GitHubButton(
                modifier = Modifier,
                onClick = {
                    loginViewModel.gitHubLoginSelected(activity, navigateToDetail)
                })
            TwitterButton(
                modifier = Modifier,
                onClick = {
                    loginViewModel.twitterLoginSelected(activity, navigateToDetail)
                })
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

@Composable
fun TwitterButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF3A95C7)
        )
    ) {
        Icon(
            modifier = modifier
                .size(15.dp)
                .height(15.dp),
            painter = painterResource(id = R.drawable.ic_twitter),
            contentDescription = "Phone",
            tint = Color.Black
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "LOGIN WITH TWITTER", color = Color.Black)
    }
}

@Composable
fun GitHubButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Icon(
            modifier = modifier
                .size(15.dp)
                .height(15.dp),
            painter = painterResource(id = R.drawable.ic_github),
            contentDescription = "Phone"
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "LOGIN WITH GITHUB")
    }
}

@Composable
private fun LoginButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF673B99)
        )
    ) {
        Text(text = "LOGIN WITH MAIL")
    }
}

@Composable
fun PhoneButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Icon(
            modifier = modifier
                .size(15.dp)
                .height(15.dp),
            painter = painterResource(id = R.drawable.ic_phone),
            contentDescription = "Phone"
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "LOGIN WITH PHONE")
    }
}

@Composable
fun GoogleButton(modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp), colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black
        )
    ) {
        Icon(
            modifier = modifier
                .size(15.dp)
                .height(15.dp),
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Phone"
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(text = "LOGIN WITH GOOGLE")
    }
}

@Composable
private fun ImageComposable(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.ic_launcher_background),
        contentDescription = "",
        modifier = modifier
            .width(200.dp)
            .height(200.dp)
    )
}