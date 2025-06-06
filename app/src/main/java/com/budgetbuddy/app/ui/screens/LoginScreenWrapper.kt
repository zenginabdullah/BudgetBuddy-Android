package com.budgetbuddy.app.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun LoginScreenWrapper(
    onLoginSuccess: () -> Unit,
    onNavigateToSignup: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()

    LoginScreen(
        onLoginClick = { email, password ->
            coroutineScope.launch {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Login", "✅ Giriş başarılı: ${auth.currentUser?.email}")
                            onLoginSuccess()
                        } else {
                            Log.e("Login", "❌ Giriş başarısız: ${task.exception?.message}")
                        }
                    }
            }
        },
        onNavigateToSignup = onNavigateToSignup
    )
}
