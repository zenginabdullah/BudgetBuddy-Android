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
        observeExpenses()
        updateTotalIncome()
    }

    private fun observeExpenses() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                repository.getExpensesByUserId(uid).collect { expenses ->
                    _allExpenses.value = expenses
                    _totalExpense.value = expenses.sumOf { it.amount }
                }
            }
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
            } catch (e: Exception) {
                Log.e("ExpenseViewModel", "Insert failed: ${e.message}")
            }
        }
    }

    fun deleteExpense(expense: ExpenseEntity) = viewModelScope.launch {
        repository.deleteExpense(expense)
    }

    fun clearAllExpenses() = viewModelScope.launch {
        repository.clearAllExpenses()
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }
    private fun updateTotalIncome() {
        viewModelScope.launch {
            repository.getAllIncomes().collect { incomes ->
                val total = incomes.sumOf { it.amount }
                _totalIncome.value = total
            }
        }
    }
    fun generateAISuggestion(incomes: List<IncomeEntity>): String {
        return SpendingAnalyzer.generateSuggestion(
            expenses = _allExpenses.value,
            incomes = incomes
        )
    }
}
