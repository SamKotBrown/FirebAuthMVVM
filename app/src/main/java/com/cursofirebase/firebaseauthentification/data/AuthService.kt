package com.cursofirebase.firebaseauthentification.data

import android.app.Activity
import android.content.Context
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    private fun getCurrentUser() = firebaseAuth.currentUser

    suspend fun login(mail: String, passw: String): FirebaseUser? {
        return firebaseAuth.signInWithEmailAndPassword(mail, passw)
            .await().user
    }

    suspend fun register(email: String, password: String): FirebaseUser? {
        // Esto es lo mismo, aunque nos permite controlar de mejor manera las posibles situaciones
        // que puedan ocurrir durante la llamada al servicio de autentificación
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val user = it.user
                    cancellableContinuation.resume(user)
                }
                .addOnFailureListener { ex ->
                    cancellableContinuation.resumeWithException(ex)
                }
        }
    }

    fun isUserLogged(): Boolean {
        return getCurrentUser() != null
    }

    fun logout() {
        firebaseAuth.signOut()
    }

    fun loginWithPhone(
        phoneText: String,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
        activity: Activity
    ) {

        //llamamos a phoneAuthProvider, creando el objeto options
        val options = PhoneAuthOptions
            .newBuilder()
            .setPhoneNumber(phoneText)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun verifyCode(verifCode: String, text: String): FirebaseUser? {
        val credentials = PhoneAuthProvider.getCredential(verifCode, text)
        return completeCredentialChecking(credentials)
    }

    suspend fun completeCredentialChecking(credentials: PhoneAuthCredential) =
        suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithCredential(credentials)
                .addOnSuccessListener {
                    cancellableContinuation.resume(it.user)
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
        }

    suspend fun getGoogleClient(resultCredential: GetCredentialResponse) : FirebaseUser? {
        when (val cred = resultCredential.credential) {
            is CustomCredential -> {
                if (cred.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(cred.data)
                    val googleIdToken = googleIdTokenCredential.idToken
                    val credentials = GoogleAuthProvider.getCredential(googleIdToken, null)

                    return suspendCancellableCoroutine { cancellableContinuation ->
                        firebaseAuth.signInWithCredential(credentials)
                            .addOnSuccessListener {
                                cancellableContinuation.resume(it.user)
                            }
                            .addOnFailureListener {
                                cancellableContinuation.resumeWithException(it)
                            }
                    }
                }
            }
        }
        return null
    }
}