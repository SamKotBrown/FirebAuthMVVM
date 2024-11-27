package com.cursofirebase.firebaseauthentification.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cursofirebase.firebaseauthentification.R
import com.cursofirebase.firebaseauthentification.data.AuthService
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private var _isLoading = MutableStateFlow<Boolean>(false)
    var isLoading: StateFlow<Boolean> = _isLoading

    var verifCode: String = ""

    fun login(mailText: String, pasText: String, navigateToDetail: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = withContext(Dispatchers.IO) {
                authService.login(mailText, pasText)
            }

            if (result != null) {
                navigateToDetail()
            }

            _isLoading.value = false
        }
    }

    fun loginPhone(
        phoneText: String,
        activity: Activity,
        onVerificationFailed: () -> Unit,
        onVerificationCompleted: () -> Unit,
        onCodeSent: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credentials: PhoneAuthCredential) {
                    //Se llama cuando detecta que es un número de telefono correcto
                    viewModelScope.launch {
                        val result = withContext(Dispatchers.IO) {
                            authService.completeCredentialChecking(
                                credentials
                            )
                        }
                        if (result != null) {
                            onVerificationCompleted()
                        }
                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    _isLoading.value = false
                    onVerificationFailed()
                }

                override fun onCodeSent(
                    verificationCode: String,
                    p1: PhoneAuthProvider.ForceResendingToken
                ) {
                    verifCode = verificationCode
                    _isLoading.value = false
                    onCodeSent()
                }
            }

            withContext(Dispatchers.IO) {
                authService.loginWithPhone(phoneText, callback = callbacks, activity = activity)
            }

            _isLoading.value = false
        }
    }

    fun verifyCode(text: String, onSuccessVerification: () -> Unit) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                authService.verifyCode(verifCode, text)
            }
            if (result != null) {
                onSuccessVerification()
            }
        }
    }

    fun doGoogleSignIn(
        context: Context,
        navigateToDetail: () -> Unit
    ) {
        val credentialManager = CredentialManager.create(context)

        val googleSignInRequest: GetCredentialRequest = GetCredentialRequest
            .Builder()
            .addCredentialOption(getGoogleIdOption(context))
            .build()

        viewModelScope.launch {
            val resultCredential = credentialManager.getCredential(context, googleSignInRequest)
            val loginResult = withContext(Dispatchers.IO) {
                authService.getGoogleClient(resultCredential)
            }
            if (loginResult != null) {
                navigateToDetail()
            }
        }
    }

    private fun getAddGoogleAccountIntent(): Intent {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT)
        intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
        return intent
    }

    private fun getGoogleIdOption(context: Context): GetGoogleIdOption {
        val bytes = UUID.randomUUID().toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedOnce = digest.fold("") { str, it ->
            str + "%02x".format(it)
        }

        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(true)
            .setNonce(hashedOnce)
            .build()
    }

}