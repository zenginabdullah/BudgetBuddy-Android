package com.budgetbuddy.app.ui.components // Bu bileşenin bulunduğu paket

// İkonlar için import
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

// Layout ve UI bileşenleri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Gider ekranında kullanılan özel kategori dropdown menüsü
@Composable
fun DropdownMenuBox(
    options: List<String>, // Seçenek listesi (örn: Gıda, Ulaşım vs.)
    selectedOption: String, // Şu an seçili olan kategori
    onOptionSelected: (String) -> Unit // Kullanıcı yeni seçenek seçtiğinde ne yapılacak
) {
    var expanded by remember { mutableStateOf(false) } // Menü açık mı?

    Box(modifier = Modifier.fillMaxWidth()) {
        // Seçili kategori alanı
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {}, // Değiştirme yok, sadece seçim yapılır
            readOnly = true,
            label = { Text("Kategori Seç") },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }, // Tıklanınca menü açılır
            trailingIcon = { // Aşağı ok ikonu
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Açılır Menü"
                    )
                }
            }
        )

        // Açılır menü
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            // Her bir seçenek için menü item’ı
            options.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onOptionSelected(item) // Seçim geri döndürülür
                        expanded = false
                    }
                )
            }
        }
    }
}
