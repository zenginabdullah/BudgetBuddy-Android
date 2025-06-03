// Harcama ekleme ekranı
package com.budgetbuddy.app.ui.screens
import com.budgetbuddy.app.ui.components.DropdownMenuBox

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.budgetbuddy.app.viewmodel.ExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddExpenseScreen(viewModel: ExpenseViewModel) {
    val context = LocalContext.current

    // Kullanıcıdan alınan veriler
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())) }

    val categoryOptions = listOf("Gıda", "Ulaşım", "Eğlence", "Eğitim", "Fatura")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Harcama Ekle", style = MaterialTheme.typography.headlineSmall)

        // Tutar alanı
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Tutar (₺)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        // Kategori seçimi
        DropdownMenuBox(
            options = categoryOptions,
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

        // Tarih alanı
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Tarih") },
            modifier = Modifier.fillMaxWidth()
        )

        // Kaydet butonu
        Button(
            onClick = {
                if (amount.isNotBlank() && category.isNotBlank()) {
                    viewModel.insertExpense(
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        category = category,
                        description = description,
                        date = date
                    )
                    Toast.makeText(context, "Harcama kaydedildi!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kaydet")
        }
    }
}
