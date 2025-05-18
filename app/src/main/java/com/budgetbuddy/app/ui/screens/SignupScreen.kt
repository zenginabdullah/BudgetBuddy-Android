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
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SignupScreen(
    onSignupClick: (email: String, password: String) -> Unit, // Kayıt fonksiyonu
    onNavigateToLogin: () -> Unit // Giriş ekranına yönlendirme fonksiyonu
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
        // Ekranda başlık olarak "Hesabını Oluştur" yazısını ekleriz
        Text("Hesabını Oluştur", fontSize = 24.sp, fontWeight = FontWeight.Bold)

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

        // Kayıt olma butonu
        Button(
            onClick = { onSignupClick(email, password) }, // Kayıt işlemi
            modifier = Modifier.fillMaxWidth() // Ekranın genişliğine yayılır
        ) {
            Text("Kayıt Ol") // Buton üzerindeki metin
        }

        Spacer(modifier = Modifier.height(8.dp)) // Buton ile altındaki metin arasına boşluk ekler

        // Giriş yapma sayfasına yönlendiren metin
        TextButton(onClick = onNavigateToLogin) {
            Text("Zaten hesabın var mı? Giriş Yap") // Giriş yapma metni
        }
    }
}
