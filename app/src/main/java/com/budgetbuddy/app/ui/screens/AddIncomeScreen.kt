// Gelir ekleme ekranı
package com.budgetbuddy.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.budgetbuddy.app.ui.CategoryPicker
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddIncomeScreen() {
    val context = LocalContext.current

    // Kullanıcıdan alınan veriler
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val date = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()) }

    val incomeCategories = listOf("Maaş", "Prim", "Ek İş", "Yatırım", "Diğer")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Gelir Ekle", style = MaterialTheme.typography.headlineSmall)

        // Tutar girişi
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Tutar (₺)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Kategori seçici bileşeni
        CategoryPicker(
            options = incomeCategories,
            selectedOption = category,
            onOptionSelected = { category = it }
        )

        // Açıklama
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Açıklama") },
            modifier = Modifier.fillMaxWidth()
        )

        // Tarih alanı şimdilik değiştirilmiyor
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text("Tarih") },
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )

        // Kaydet butonu
        Button(
            onClick = {
                Toast.makeText(context, "Gelir kaydedildi!", Toast.LENGTH_SHORT).show()
                // Buraya veri kaydetme işlemi eklenecek (Room + Firebase)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kaydet")
        }
    }
}
