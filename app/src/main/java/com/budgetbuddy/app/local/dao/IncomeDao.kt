package com.budgetbuddy.app.data.local.dao

import androidx.room.*
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncome(income: IncomeEntity)

    @Delete
    suspend fun deleteIncome(income: IncomeEntity)

    @Query("SELECT * FROM incomes ORDER BY date DESC")
    fun getAllIncomes(): Flow<List<IncomeEntity>>

    @Query("DELETE FROM incomes")
    suspend fun clearAllIncomes()

    @Query("SELECT SUM(amount) FROM incomes")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT * FROM expenses WHERE userId = :uid")
    fun getAllExpenses(uid: String): Flow<List<IncomeEntity>>

    @Query("SELECT * FROM incomes WHERE userId = :userId ORDER BY date DESC")
    fun getIncomesByUserId(userId: String): Flow<List<IncomeEntity>>
}
