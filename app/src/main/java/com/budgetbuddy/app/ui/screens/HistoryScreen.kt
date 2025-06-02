package com.budgetbuddy.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.viewmodel.ExpenseViewModel
import androidx.compose.foundation.layout.FlowRow
import com.budgetbuddy.app.viewmodel.IncomeViewModel
import com.budgetbuddy.app.data.local.entity.IncomeEntity

@Composable
fun HistoryScreen(viewModel: ExpenseViewModel,
                  incomeViewModel: IncomeViewModel) {
    val expenses by viewModel.filteredExpenses.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val total by viewModel.totalExpense.collectAsState()

    val incomes by incomeViewModel.incomeList.collectAsState()
    val filteredIncomes by incomeViewModel.filteredIncomes.collectAsState()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SummaryCard(
            totalIncome = viewModel.totalIncome.collectAsState().value,
            totalExpense = viewModel.totalExpense.collectAsState().value
        )

        CategoryFilterRow(
            selectedCategory = selectedCategory,
            onCategorySelected = {
                viewModel.setCategoryFilter(it)
                incomeViewModel.setCategoryFilter(it)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(expenses) { expense ->
                ExpenseCard(
                    expense = expense,
                    onDeleteClick = { viewModel.deleteExpense(expense) }
                )
            }
            items(filteredIncomes) { income ->
                IncomeCard(
                    income = income,
                    onDeleteClick = { incomeViewModel.deleteIncome(income) }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryFilterRow(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit
) {
    val categories = listOf("Tümü", "Gıda", "Ulaşım", "Eğlence", "Eğitim", "Fatura")

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.forEach { category ->
            val isSelected = selectedCategory == category || (category == "Tümü" && selectedCategory == null)

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category.takeIf { it != "Tümü" }) },
                label = { Text(category) }
            )
        }
    }
}
@Composable
fun SummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Özet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Gelir", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "₺%.2f".format(totalIncome),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF388E3C) // Yeşil
                    )
                }

                Column {
                    Text("Gider", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "₺%.2f".format(totalExpense),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFD32F2F) // Kırmızı
                    )
                }

                Column {
                    Text("Net", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "₺%.2f".format(totalIncome - totalExpense),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseCard(expense: ExpenseEntity, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.category, style = MaterialTheme.typography.titleMedium)
                Text("₺%.2f".format(expense.amount), style = MaterialTheme.typography.bodyLarge)
                Text(expense.date, style = MaterialTheme.typography.bodySmall)
                if (expense.description.isNotBlank()) {
                    Text(expense.description, style = MaterialTheme.typography.bodySmall)
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = Color.Red
                )
            }
        }
    }
}
@Composable
fun IncomeCard(income: IncomeEntity, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(income.category, style = MaterialTheme.typography.titleMedium)
                Text("₺%.2f".format(income.amount), style = MaterialTheme.typography.bodyLarge)
                Text(income.date, style = MaterialTheme.typography.bodySmall)
                if (income.description.isNotBlank()) {
                    Text(income.description, style = MaterialTheme.typography.bodySmall)
                }
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = Color.Red
                )
            }
        }
    }
}