package com.budgetbuddy.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.data.repository.IncomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository
) : ViewModel() {

    // 1. Tüm gelir kayıtları için StateFlow
    private val _allIncomes = MutableStateFlow<List<IncomeEntity>>(emptyList())
    val allIncomes: StateFlow<List<IncomeEntity>> = _allIncomes.asStateFlow()

    // 2. Kullanıcının seçtiği kategori filtresi (null = tüm kategoriler)
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    init {
        // Uygulama başladığında Firestore senkronizasyonunu tetikle
        viewModelScope.launch {
            repository.syncAllIncomesFromFirebase()
        }

        // Room’dan gelen gelir listesini _allIncomes StateFlow’una aktar
        viewModelScope.launch {
            repository.getAllIncomes().collect { list ->
                _allIncomes.value = list
            }
        }
    }

    // 3. Yeni gelir ekleme (Room + Firestore)
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

    // 4. Gelir silme (Room + Firestore)
    fun deleteIncome(income: IncomeEntity) {
        viewModelScope.launch {
            repository.deleteIncome(income)
        }
    }

    // 5. Kategori filtresini güncelle
    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    // 6. Filtrelenmiş gelir listesi: kategoriye göre süzülen veya tüm liste
    val filteredIncomes: StateFlow<List<IncomeEntity>> = combine(
        _allIncomes,
        _selectedCategory
    ) { incomes, category ->
        if (category.isNullOrEmpty()) {
            incomes
        } else {
            incomes.filter { it.category == category }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = emptyList()
    )

    // 7. Toplam gelir hesaplaması (filtrelenmiş liste değil, tüm liste üzerinden)
    val totalIncome: StateFlow<Double> = allIncomes
        .map { list -> list.sumOf { it.amount } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = 0.0
        )
}
