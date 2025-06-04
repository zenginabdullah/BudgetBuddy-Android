package com.budgetbuddy.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.data.repository.IncomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository
) : ViewModel() {

    private val _allIncomes = MutableStateFlow<List<IncomeEntity>>(emptyList())
    val allIncomes: StateFlow<List<IncomeEntity>> = _allIncomes.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _incomeList = MutableStateFlow<List<IncomeEntity>>(emptyList())
    val incomeList: StateFlow<List<IncomeEntity>> = _incomeList.asStateFlow()

    init {
        // Firestore'dan verileri senkronize et (isteğe bağlı ama faydalı)
        viewModelScope.launch {
            repository.syncAllIncomesFromFirebase()
        }

        observeIncome()
    }

    private fun observeIncome() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid != null) {
            viewModelScope.launch {
                repository.getIncomesByUserId(uid).collect { list ->
                    _allIncomes.value = list
                    _incomeList.value = list
                }
            }
        }
    }

    fun insertIncome(amount: Double, category: String, date: String, description: String) {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            val income = IncomeEntity(
                amount = amount,
                category = category,
                description = description,
                date = date,
                userId = uid
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
        _allIncomes, _selectedCategory
    ) { incomes, category ->
        category?.let { cat ->
            incomes.filter { it.category == cat }
        } ?: incomes
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val totalIncome: StateFlow<Double> = _allIncomes
        .map { it.sumOf { income -> income.amount } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )
}
