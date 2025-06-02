package com.budgetbuddy.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.budgetbuddy.app.viewmodel.ExpenseViewModel
import com.budgetbuddy.app.viewmodel.IncomeViewModel
import com.budgetbuddy.app.data.PreferencesManager

@Composable
fun HomeScreen(
    expenseViewModel: ExpenseViewModel,
    incomeViewModel: IncomeViewModel,
    currencySymbol: String,
    onAddExpenseClick: () -> Unit,
    onAddIncomeClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val totalExpense by expenseViewModel.totalExpense.collectAsState()
    val totalIncome by incomeViewModel.totalIncome.collectAsState()
    val balance = totalIncome - totalExpense

    val context = LocalContext.current
    val prefs = PreferencesManager(context)

    // 🧪 SharedPreferences test logları (isteğe bağlı kaldırılabilir)
    LaunchedEffect(Unit) {
        android.util.Log.d("PrefsTest", "Currency: ${prefs.getCurrency()}")
        android.util.Log.d("PrefsTest", "DarkMode: ${prefs.isDarkModeEnabled()}")
        android.util.Log.d("PrefsTest", "Notifications: ${prefs.areNotificationsEnabled()}")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "📊 Bütçe Özeti",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryRow("Gelir", "$currencySymbol%.2f".format(totalIncome), MaterialTheme.colorScheme.primary)
                SummaryRow("Gider", "$currencySymbol%.2f".format(totalExpense), MaterialTheme.colorScheme.error)
                SummaryRow("Kalan", "$currencySymbol%.2f".format(balance), MaterialTheme.colorScheme.secondary)
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onAddIncomeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.AttachMoney, contentDescription = "Gelir Ekle")
                Spacer(Modifier.width(6.dp))
                Text("Gelir Ekle")
            }

            Button(
                onClick = onAddExpenseClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.MoneyOff, contentDescription = "Gider Ekle")
                Spacer(Modifier.width(6.dp))
                Text("Gider Ekle")
            }

            Button(
                onClick = onHistoryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("📜 Geçmiş Kayıtlar")
            }

            Button(
                onClick = onSettingsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⚙️ Ayarlar")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "🔔 Harcama limiti yaklaşmakta!",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SummaryRow(title: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = title, fontWeight = FontWeight.Medium)
        Text(text = value, color = color, fontWeight = FontWeight.Bold)
    }
}
