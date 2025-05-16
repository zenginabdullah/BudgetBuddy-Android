package com.budgetbuddy.app.ui // Bu bileşenin bulunduğu paket

// UI bileşenleri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Gelir ekranında kategori seçimi için kullanılan menü
@Composable
fun CategoryPicker(
    options: List<String>, // Seçenekler
    selectedOption: String, // Seçili olan
    onOptionSelected: (String) -> Unit // Seçim sonucu ne yapılacak
) {
    var expanded by remember { mutableStateOf(false) } // Menü açık mı?

    Column {
        Text(text = "Kategori", style = MaterialTheme.typography.labelMedium)

        // Menü kutusu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true } // Tıklayınca aç
                .padding(vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                label = { Text("Kategori Seç") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option) // Yeni seçim bildir
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
