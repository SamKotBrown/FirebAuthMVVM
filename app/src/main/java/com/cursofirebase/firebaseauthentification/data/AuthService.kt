package com.cursofirebase.firebaseauthentification.data

import android.app.Activity
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
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

    suspend fun getGoogleClient(resultCredential: GetCredentialResponse): FirebaseUser? {
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

    suspend fun gitHubLogin(activity: Activity): FirebaseUser? {
        val builtProvider = OAuthProvider.newBuilder("github.com").apply {
            // Aquí le ponemos lo que le vamos a pedir
            scopes = listOf("user:email")
        }.build()

        return initRegisterWithProvider(activity, builtProvider)
    }

    private suspend fun initRegisterWithProvider(
        activity: Activity,
        builtProvider: OAuthProvider
    ) = suspendCancellableCoroutine<FirebaseUser?> { cancCont ->
        //Si ya hay un resultado pendiente
        firebaseAuth.pendingAuthResult?.addOnSuccessListener {
            cancCont.resume(it.user)
        }?.addOnFailureListener { ex ->
            cancCont.resumeWithException(ex)
            // Si no hay nada pendiente, se hace lo de la siguiente línea
        } ?: completeRegisterWithProvider(activity, builtProvider, cancCont)
    }

    private fun completeRegisterWithProvider(
        activity: Activity,
        build: OAuthProvider,
        cancCont: CancellableContinuation<FirebaseUser?>
    ) {
        //No se hace return porque, debido a que se usa el mismo cancellableCoroutine, esta función
        // forma parte de la corrutina que lo llama
        firebaseAuth.startActivityForSignInWithProvider(activity, build)
            .addOnSuccessListener { cancCont.resume(it.user) }
            .addOnFailureListener { cancCont.resumeWithException(it) }
    }

    suspend fun twitterLogin(activity: Activity): FirebaseUser? {
        val provider = OAuthProvider.newBuilder("twitter.com").build()
        return initRegisterWithProvider(activity, provider)
    }
}