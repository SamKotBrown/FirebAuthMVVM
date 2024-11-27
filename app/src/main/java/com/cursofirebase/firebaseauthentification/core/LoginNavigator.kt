package com.cursofirebase.firebaseauthentification.core

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cursofirebase.firebaseauthentification.core.Routes.Detail
import com.cursofirebase.firebaseauthentification.core.Routes.NormalLogin
import com.cursofirebase.firebaseauthentification.core.Routes.PhoneLogin
import com.cursofirebase.firebaseauthentification.core.Routes.Signup
import com.cursofirebase.firebaseauthentification.ui.detail.DetailScreen
import com.cursofirebase.firebaseauthentification.ui.login.LoginScreen
import com.cursofirebase.firebaseauthentification.ui.login.otherLogins.PhoneScreen
import com.cursofirebase.firebaseauthentification.ui.signup.SignupScreen

@Composable
fun LoginNavigator(
    modifier: Modifier = Modifier,
    navigationController: NavHostController,
    defaultDestination: String = NormalLogin.route,
    activity: Activity
) {
    //NOTA IMPORTANTE: SI NO PONES LAS LLAVES (EN EL ATRIBUTO BUILDER O FUERA DEL MÉTODO),
    // TE VA A PONER COMO QUE NO EXISTE EL MÉTODO CON EL StartDestination.
    // Gracias por hacerme caso y no perder 2 horas
    NavHost(
        navController = navigationController,
        startDestination = defaultDestination,
        builder = composableScreens(navigationController, activity),
        modifier = modifier
    )
}

fun composableScreens(
    navigationController: NavHostController,
    activity: Activity
): NavGraphBuilder.() -> Unit {
    return {
        composable(NormalLogin.route) {
            LoginScreen(
                activity = activity,
                navigateToDetail = {
                    navigationController.navigate(Detail.route)
                }, navigateToRegister = {
                    navigationController.navigate(Signup.route)
                },
                navigateToPhoneLogin = {
                    navigationController.navigate(PhoneLogin.route)
                }
            )
        }
        composable(PhoneLogin.route) {
            PhoneScreen(activity = activity, navigateToDetail = {
                navigationController.navigate(Detail.route)
            })
        }
        composable(Signup.route) {
            SignupScreen {
                navigationController.navigate(Detail.route)
            }
        }
        composable(Detail.route) {
            DetailScreen {
                navigationController.navigate(NormalLogin.route) {
                    //Esto lo que hace es eliminar las anteriores pantallas de la pila y vuelve
                    // a la primera. Sirve, en este caso, para los logins
                    popUpTo(
                        navigationController.currentBackStackEntry?.destination?.route
                            ?: return@navigate
                    ) {
                        inclusive = true
                    }
                }
            }
        }
    }
}

sealed class Routes(val route: String) {
    data object Signup : Routes("Signup")
    data object NormalLogin : Routes("Login")
    data object Detail : Routes("Detail")
    data object PhoneLogin : Routes("PhoneLogin")
    data object FacebookLogin : Routes("FacebookLogin")
    data object GitHubLogin : Routes("GitHubLogin")
    data object MicrosoftLogin : Routes("MicrosoftLogin")
    data object TwitterLogin : Routes("TwitterLogin")
    data object YahooLogin : Routes("YahooLogin")
}