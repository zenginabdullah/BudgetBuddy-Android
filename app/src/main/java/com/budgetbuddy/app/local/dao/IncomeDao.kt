package com.budgetbuddy.app.data.local.dao

import androidx.room.*
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity) : Long

    @Query("DELETE FROM incomes WHERE id = :incomeId")
    suspend fun deleteIncomeById(incomeId: Int)

    @Query("SELECT * FROM incomes ORDER BY date DESC")
    fun getAllIncomes(): Flow<List<IncomeEntity>>

    @Query("DELETE FROM incomes")
    suspend fun clearAllIncomes()

    @Query("SELECT SUM(amount) FROM incomes")
    fun getTotalIncome(): Flow<Double?>
}
