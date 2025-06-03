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
import com.budgetbuddy.app.ui.components.DropdownMenuBox
import com.budgetbuddy.app.viewmodel.IncomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddIncomeScreen(viewModel: IncomeViewModel) {
    val context = LocalContext.current

    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val date = remember { SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date()) }


    val categoryOptions = listOf("Maaş", "Serbest Çalışma", "Yatırım", "Burs", "Diğer")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Gelir Ekle",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Tutar (₺)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenuBox(
            options = categoryOptions,
            selectedOption = category,
            onOptionSelected = { category = it }
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Açıklama") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text("Tarih") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                val parsedAmount = amount.toDoubleOrNull()
                if (parsedAmount != null && category.isNotBlank() && date.isNotBlank()) {
                    viewModel.insertIncome(
                        amount = parsedAmount,
                        category = category,
                        date = date,
                        description = description
                    )
                    Toast.makeText(context, "Gelir kaydedildi!", Toast.LENGTH_SHORT).show()

                    // Alanları sıfırla
                    amount = ""
                    category = ""
                    description = ""
                } else {
                    Toast.makeText(context, "Lütfen tüm alanları doğru şekilde doldurun.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kaydet")
        }

    }
}
