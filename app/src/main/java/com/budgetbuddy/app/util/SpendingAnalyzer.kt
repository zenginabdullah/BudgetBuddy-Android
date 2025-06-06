package com.budgetbuddy.app.util

import android.util.Log
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.min

object SpendingAnalyzer {

    fun generateSuggestion(expenses: List<ExpenseEntity>, incomes: List<IncomeEntity>): String {
        if (expenses.isEmpty() || incomes.isEmpty()) {
            return "Yeterli veri yok 😔. Birkaç harcama ve gelir girdikten sonra sana özel öneriler verebilirim!"
        }

        val totalExpense = expenses.sumOf { it.amount }
        val totalIncome = incomes.sumOf { it.amount }
        val ratio = totalExpense / totalIncome

        val mostSpentCategory = expenses
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .maxByOrNull { it.value }?.key ?: "Bilinmeyen"

        return when {
            ratio > 0.9 -> "Bu ay gelirin %90'ından fazlasını harcadın 😱. Özellikle \"$mostSpentCategory\" kategorisine dikkat etmeni öneririm!"
            ratio > 0.7 -> "Harcamaların gelirin %70'ini geçti. \"$mostSpentCategory\" kategorisinde tasarruf yaparak kontrolü ele alabilirsin 💪"
            else -> "Harcamaların oldukça dengeli 👏. Böyle devam! Belki \"$mostSpentCategory\" harcamalarını biraz kısarak daha fazla tasarruf edebilirsin 💰"
        }
    }

    fun analyzeSpendingTrends(expenses: List<ExpenseEntity>): List<String> {
        Log.d("SpendingAnalyzer", "Analyzing spending trends for ${expenses.size} expenses")

        if (expenses.isEmpty()) {
            Log.d("SpendingAnalyzer", "No expenses to analyze")
            return emptyList()
        }

        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH) + 1
        Log.d("SpendingAnalyzer", "Current month: $currentMonth")

        val categoryMonthlyMap = mutableMapOf<String, MutableMap<Int, Double>>()
        val warnings = mutableListOf<String>()

        for (expense in expenses) {
            val month = try {
                SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(expense.date)?.let {
                    Calendar.getInstance().apply { time = it }.get(Calendar.MONTH) + 1
                } ?: continue
            } catch (e: Exception) {
                Log.e("SpendingAnalyzer", "Date parsing error: ${e.message} for date: ${expense.date}")
                continue
            }

            val category = expense.category
            categoryMonthlyMap.getOrPut(category) { mutableMapOf() }
                .merge(month, expense.amount) { old, new -> old + new }

            Log.d("SpendingAnalyzer", "Added expense: $category, month: $month, amount: ${expense.amount}")
        }

        Log.d("SpendingAnalyzer", "Category Monthly Map: $categoryMonthlyMap")

        // Geçmiş ay verisi varsa trend analizi yap
        for ((category, monthlyData) in categoryMonthlyMap) {
            val current = monthlyData[currentMonth] ?: continue
            val past = monthlyData.filterKeys { it != currentMonth }.values

            if (past.isNotEmpty()) {
                val avg = past.average()
                Log.d("SpendingAnalyzer", "Category: $category, Current: $current, Avg: $avg")

                if (current > avg * 1.2) {
                    val increase = ((current - avg) / avg * 100).toInt()
                    val warning = "📈 '$category' harcamalarında %$increase artış var. Dikkatli ol!"
                    Log.d("SpendingAnalyzer", "Adding warning: $warning")
                    warnings.add(warning)
                }
            } else {
                Log.d("SpendingAnalyzer", "No past data for category: $category")
            }
        }

        // Geçmiş ay verisi yoksa, mevcut ay içindeki kategori dağılımına göre öneriler oluştur
        if (warnings.isEmpty()) {
            val currentMonthExpenses = expenses.filter {
                try {
                    val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(it.date)
                    val month = Calendar.getInstance().apply { time = date }.get(Calendar.MONTH) + 1
                    month == currentMonth
                } catch (e: Exception) {
                    false
                }
            }

            if (currentMonthExpenses.isNotEmpty()) {
                // En yüksek harcama yapılan kategoriyi bul
                val categoryTotals = currentMonthExpenses
                    .groupBy { it.category }
                    .mapValues { it.value.sumOf { expense -> expense.amount } }

                val totalSpent = categoryTotals.values.sum()
                val highestCategory = categoryTotals.maxByOrNull { it.value }

                if (highestCategory != null) {
                    val percentage = (highestCategory.value / totalSpent * 100).toInt()
                    if (percentage > 50) {
                        warnings.add("📊 '${highestCategory.key}' kategorisine bütçenin %$percentage'ini harcıyorsun. Harcamalarını daha dengeli dağıtmayı düşünebilirsin.")
                    }
                }

                // Günlük harcama analizi
                val dayTotals = currentMonthExpenses
                    .groupBy { it.date }
                    .mapValues { it.value.sumOf { expense -> expense.amount } }

                val maxDailySpending = dayTotals.maxByOrNull { it.value }
                if (maxDailySpending != null && dayTotals.size > 1) {
                    val avgDailySpending = totalSpent / dayTotals.size
                    if (maxDailySpending.value > avgDailySpending * 2) {
                        warnings.add("📅 ${maxDailySpending.key} tarihinde normalden çok daha fazla harcama yapmışsın. Büyük harcamaları planlamak bütçe kontrolüne yardımcı olabilir.")
                    }
                }

                // Eğer hala öneri yoksa genel bir öneri ekle
                if (warnings.isEmpty() && categoryTotals.size > 1) {
                    val smallestCategory = categoryTotals.minByOrNull { it.value }
                    if (smallestCategory != null) {
                        warnings.add("💡 '${smallestCategory.key}' kategorisinde tasarruflu davranıyorsun. Diğer kategorilerde de benzer alışkanlıklar geliştirmeyi deneyebilirsin.")
                    }
                }
            }
        }

        Log.d("SpendingAnalyzer", "Final warnings: $warnings")
        return warnings
    }

    // Harcama grafiği bileşeni
    @Composable
    fun ExpensePieChart(
        expenses: List<ExpenseEntity>,
        currencySymbol: String,
        modifier: Modifier = Modifier
    ) {
        val categoryExpenses = expenses
            .groupBy { it.category }
            .mapValues { it.value.sumOf { expense -> expense.amount } }
            .toList()
            .sortedByDescending { it.second }

        val totalExpense = expenses.sumOf { it.amount }

        // Kategoriler için renkler
        val colors = listOf(
            Color(0xFF4285F4), // Mavi
            Color(0xFFDB4437), // Kırmızı
            Color(0xFFF4B400), // Sarı
            Color(0xFF0F9D58), // Yeşil
            Color(0xFF9C27B0), // Mor
            Color(0xFF00ACC1), // Açık Mavi
            Color(0xFFFF7043), // Turuncu
            Color(0xFF795548), // Kahverengi
            Color(0xFF607D8B)  // Gri
        )

        val surfaceColor = MaterialTheme.colorScheme.surface

        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = surfaceColor
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Pasta grafik
                Box(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp)
                    ) {
                        val radius = min(size.width, size.height) / 2
                        var startAngle = 0f

                        categoryExpenses.forEachIndexed { index, (_, amount) ->
                            val sweepAngle = (amount / totalExpense * 360).toFloat()
                            val color = colors[index % colors.size]

                            drawArc(
                                color = color,
                                startAngle = startAngle,
                                sweepAngle = sweepAngle,
                                useCenter = true,
                                size = Size(radius * 2, radius * 2),
                                topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
                            )

                            startAngle += sweepAngle
                        }

                        // İç daire (boşluk için)
                        drawCircle(
                            color = surfaceColor,
                            radius = radius * 0.6f,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }

                    // Toplam harcama ortada gösterilir
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Toplam",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(
                            text = "$currencySymbol%.0f".format(totalExpense),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Kategori listesi
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categoryExpenses.take(5).forEachIndexed { index, (category, amount) ->
                        val percentage = (amount / totalExpense * 100).toInt()
                        val color = colors[index % colors.size]

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(color, RoundedCornerShape(2.dp))
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "$currencySymbol%.0f".format(amount),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Eğer 5'ten fazla kategori varsa, diğerlerini "Diğer" olarak göster
                    if (categoryExpenses.size > 5) {
                        val otherAmount = categoryExpenses.drop(5).sumOf { it.second }
                        val percentage = (otherAmount / totalExpense * 100).toInt()

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(Color.Gray, RoundedCornerShape(2.dp))
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Diğer",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )

                            Text(
                                text = "$percentage%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "$currencySymbol%.0f".format(otherAmount),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }

    // Harcama verisi yoksa gösterilecek bileşen
    @Composable
    fun NoExpenseDataCard() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Henüz harcama verisi yok",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Harcama ekleyerek detaylı analiz görüntüleyebilirsin",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}