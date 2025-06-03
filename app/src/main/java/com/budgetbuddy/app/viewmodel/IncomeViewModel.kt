package com.budgetbuddy.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.repository.IncomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.*


@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository
) : ViewModel() {

    private val _incomeList = MutableStateFlow<List<IncomeEntity>>(emptyList())
    val incomeList: StateFlow<List<IncomeEntity>> = _incomeList.asStateFlow()
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    init {
        loadIncomes()
    }

    private fun loadIncomes() {
        viewModelScope.launch {
            repository.getAllIncomes().collect {
                _incomeList.value = it
            }
        }
    }

    fun insertIncome(amount: Double, category: String, date: String, description: String) {
        viewModelScope.launch {
            val income = IncomeEntity(
                amount = amount,
                category = category,
                description = description,
                date = date
            )
            repository.insertIncome(income)
        }
    }
    fun deleteIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }
    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    val filteredIncomes: StateFlow<List<IncomeEntity>> = combine(
        _incomeList,
        _selectedCategory
    ) { incomes, category ->
        if (category == null) incomes else incomes.filter { it.category == category }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // ✅ Toplam gelir hesaplaması
    val totalIncome: StateFlow<Double> = incomeList
        .map { incomes -> incomes.sumOf { it.amount } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )
}


