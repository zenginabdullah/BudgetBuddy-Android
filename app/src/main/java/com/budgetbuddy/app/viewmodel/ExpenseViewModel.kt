package com.budgetbuddy.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgetbuddy.app.data.repository.ExpenseRepository
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.util.SpendingAnalyzer


@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _allExpenses = MutableStateFlow<List<ExpenseEntity>>(emptyList())
    val allExpenses: StateFlow<List<ExpenseEntity>> = _allExpenses.asStateFlow()
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()
    private val _totalIncome = MutableStateFlow(0.0)
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()

    val filteredExpenses: StateFlow<List<ExpenseEntity>> = combine(_allExpenses, _selectedCategory) { expenses, category ->
        category?.let {
            expenses.filter { it.category == category }
        } ?: expenses
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Firebase'den verileri senkronize et
        viewModelScope.launch {
            repository.syncAllExpensesFromFirebase()
            Log.d("ExpenseViewModel", "Synced expenses from Firebase")
        }

        observeExpenses()
        updateTotalIncome()
    }

    private fun observeExpenses() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                repository.getExpensesByUserId(uid).collect { expenses ->
                    _allExpenses.value = expenses
                    val total = expenses.sumOf { it.amount }
                    _totalExpense.value = total
                    Log.d("ExpenseViewModel", "Total expense updated: $total, expenses size: ${expenses.size}")

                    // Harcama detaylarını logla
                    expenses.forEach { expense ->
                        Log.d("ExpenseViewModel", "Expense: ${expense.category}, amount: ${expense.amount}")
                    }
                }
            }
        } else {
            Log.e("ExpenseViewModel", "User ID is null, cannot observe expenses")
        }
    }

    fun insertExpense(amount: Double, category: String, description: String, date: String) {
        viewModelScope.launch {
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch

                val expense = ExpenseEntity(
                    amount = amount,
                    category = category,
                    description = description,
                    date = date,
                    userId = uid
                )
                repository.insertExpense(expense)
                Log.d("ExpenseViewModel", "Expense inserted: $expense")

                // Ekleme sonrası güncel toplam gideri logla
                val currentExpenses = _allExpenses.value
                val newTotal = currentExpenses.sumOf { it.amount } + amount
                Log.d("ExpenseViewModel", "After insert - expected total expense: $newTotal")
            } catch (e: Exception) {
                Log.e("ExpenseViewModel", "Insert failed: ${e.message}")
            }
        }
    }

    fun deleteExpense(expense: ExpenseEntity) = viewModelScope.launch {
        repository.deleteExpense(expense)
        Log.d("ExpenseViewModel", "Expense deleted: $expense")
    }

    fun clearAllExpenses() = viewModelScope.launch {
        repository.clearAllExpenses()
        Log.d("ExpenseViewModel", "All expenses cleared")
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    private fun updateTotalIncome() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                repository.getAllIncomes().collect { incomes ->
                    val total = incomes.sumOf { it.amount }
                    _totalIncome.value = total
                    Log.d("ExpenseViewModel", "Total income updated: $total")
                }
            }
        } else {
            Log.e("ExpenseViewModel", "User ID is null, cannot update total income")
        }
    }

    fun generateAISuggestion(incomes: List<IncomeEntity>): String {
        return SpendingAnalyzer.generateSuggestion(
            expenses = _allExpenses.value,
            incomes = incomes
        )
    }

    // Gider toplamını manuel olarak güncelle
    fun updateTotalExpense() {
        viewModelScope.launch {
            val expenses = _allExpenses.value
            val total = expenses.sumOf { it.amount }
            _totalExpense.value = total
            Log.d("ExpenseViewModel", "Manually updated total expense: $total")
        }
    }
}