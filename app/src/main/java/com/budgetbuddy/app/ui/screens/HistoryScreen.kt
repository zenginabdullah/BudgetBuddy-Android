// Geçmiş işlemleri listeleyen ekran

package com.budgetbuddy.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.*

data class Transaction(
    val type: String, // "Gelir" ya da "Gider"
    val category: String,
    val amount: Double,
    val date: String
)

@Composable
fun HistoryScreen() {
    // Örnek veri seti
    val allTransactions = remember {
        listOf(
            Transaction("Gelir", "Maaş", 5000.0, "01.05.2025"),
            Transaction("Gider", "Gıda", 150.0, "02.05.2025"),
            Transaction("Gider", "Ulaşım", 60.0, "03.05.2025"),
            Transaction("Gelir", "Yatırım", 800.0, "05.05.2025"),
            Transaction("Gider", "Fatura", 250.0, "06.05.2025")
        )
    }

    // Filtreler
    var selectedType by remember { mutableStateOf("Hepsi") }
    var selectedCategory by remember { mutableStateOf("Tümü") }

    val types = listOf("Hepsi", "Gelir", "Gider")
    val categories = listOf("Tümü") + allTransactions.map { it.category }.distinct()

    // Filtrelenmiş liste
    val filteredTransactions = allTransactions.filter {
        (selectedType == "Hepsi" || it.type == selectedType) &&
                (selectedCategory == "Tümü" || it.category == selectedCategory)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Tür ve Kategori filtre menüleri
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DropdownSelector(
                label = "Tür",
                options = types,
                selected = selectedType,
                onSelected = { selectedType = it },
                modifier = Modifier.weight(1f)
            )

            DropdownSelector(
                label = "Kategori",
                options = categories,
                selected = selectedCategory,
                onSelected = { selectedCategory = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Listeleme
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(filteredTransactions) { txn ->
                TransactionItem(txn)
            }
        }
    }
}

// Dropdown menü bileşeni (filtre için)
@Composable
fun DropdownSelector(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Her işlem için gösterilecek kart
@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (transaction.type == "Gelir")
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "${transaction.type} - ${transaction.category}")
            Text(text = "₺%.2f".format(transaction.amount), style = MaterialTheme.typography.bodyLarge)
            Text(text = transaction.date, style = MaterialTheme.typography.bodySmall)
        }
    }
}
