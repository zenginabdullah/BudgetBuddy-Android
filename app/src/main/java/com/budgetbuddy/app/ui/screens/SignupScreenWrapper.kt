package com.budgetbuddy.app.ui.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun SignupScreenWrapper(
    onSignupSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val coroutineScope = rememberCoroutineScope()

    SignupScreen(
        onSignupClick = { email, password ->
            coroutineScope.launch {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("Signup", "✅ Kayıt başarılı: ${auth.currentUser?.email}")
                            onSignupSuccess()
                        } else {
                            Log.e("Signup", "❌ Kayıt başarısız: ${task.exception?.message}")
                        }
                    }
            }
        },
        onNavigateToLogin = onNavigateToLogin
    )
}
