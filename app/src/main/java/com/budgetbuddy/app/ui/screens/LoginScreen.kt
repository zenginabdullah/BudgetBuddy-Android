package com.budgetbuddy.app.ui.screens

import androidx.compose.foundation.layout.* // Düzenleme için gerekli bileşenler
import androidx.compose.material3.* // Material 3 UI bileşenleri
import androidx.compose.runtime.* // State yönetimi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation // Şifreyi gizleme
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit, // Giriş fonksiyonu
    onNavigateToSignup: () -> Unit // Kayıt ol ekranına yönlendirme fonksiyonu
) {
    var email by remember { mutableStateOf("") } // E-posta girişi için state
    var password by remember { mutableStateOf("") } // Şifre girişi için state

    Column(
        modifier = Modifier
            .fillMaxSize() // Ekranı tamamen kaplaması için
            .padding(24.dp), // Ekranın kenarlarına boşluk ekler
        verticalArrangement = Arrangement.Center, // İçeriği dikeyde ortalar
        horizontalAlignment = Alignment.CenterHorizontally // İçeriği yatayda ortalar
    ) {
        // Ekranda başlık olarak "Hoşgeldin!" yazısını ekleriz
        Text("BudgetBuddy'ye Hoşgeldin!", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(24.dp)) // Başlık ile form arasına boşluk ekler

        // E-posta girişi için text field
        OutlinedTextField(
            value = email, // E-posta input değerini tutar
            onValueChange = { email = it }, // E-posta değeri her değiştiğinde güncellenir
            label = { Text("E-posta") }, // TextField'ın üstünde görünen etiket
            modifier = Modifier.fillMaxWidth() // Ekranın genişliğine yayılır
        )

        Spacer(modifier = Modifier.height(8.dp)) // E-posta ve şifre arasına boşluk ekler

        // Şifre girişi için text field
        OutlinedTextField(
            value = password, // Şifre input değerini tutar
            onValueChange = { password = it }, // Şifre değeri her değiştiğinde güncellenir
            label = { Text("Şifre") }, // TextField'ın üstünde görünen etiket
            visualTransformation = PasswordVisualTransformation(), // Şifreyi gizler
            modifier = Modifier.fillMaxWidth() // Ekranın genişliğine yayılır
        )

        Spacer(modifier = Modifier.height(16.dp)) // Şifre ve buton arasında boşluk ekler

        // Giriş yapma butonu
        Button(
            onClick = { onLoginClick(email, password) }, // Giriş yapma işlemi
            modifier = Modifier.fillMaxWidth() // Ekranın genişliğine yayılır
        ) {
            Text("Giriş Yap") // Buton üzerindeki metin
        }

        Spacer(modifier = Modifier.height(8.dp)) // Buton ile altındaki metin arasına boşluk ekler

        // Kayıt ol sayfasına yönlendiren metin
        TextButton(onClick = onNavigateToSignup) {
            Text("Hesabın yok mu? Kayıt Ol") // Kayıt olma metni
        }
    }
}
