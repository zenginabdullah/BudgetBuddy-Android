package com.budgetbuddy.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    currency: String,
    isDark: Boolean,
    notificationsEnabled: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onCurrencyChange: (String) -> Unit,
    onNotificationsToggle: (Boolean) -> Unit
) {
    val currencyOptions = listOf("₺", "$", "€")
    var currencyMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Ayarlar", style = MaterialTheme.typography.headlineSmall)

        // PARA BİRİMİ
        Text("Para Birimi")
        Box {
            OutlinedButton(onClick = { currencyMenuExpanded = true }) {
                Text(currency)
            }
            DropdownMenu(
                expanded = currencyMenuExpanded,
                onDismissRequest = { currencyMenuExpanded = false }
            ) {
                currencyOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onCurrencyChange(option)
                            currencyMenuExpanded = false
                        }
                    )
                }
            }
        }

        // KOYU TEMA
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Koyu Tema")
            Switch(
                checked = isDark,
                onCheckedChange = { onThemeToggle(it) }
            )
        }

        // BİLDİRİMLER
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bildirimler")
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { onNotificationsToggle(it) }
            )
        }
    }
}
