package com.budgetbuddy.app.data.local.dao

import androidx.room.*
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Query("DELETE FROM expenses")
    suspend fun clearAllExpenses()

    @Query("SELECT SUM(amount) FROM expenses")
    fun getTotalExpense(): Flow<Double?>


}
