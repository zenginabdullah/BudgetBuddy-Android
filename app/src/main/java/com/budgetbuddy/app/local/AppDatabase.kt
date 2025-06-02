package com.budgetbuddy.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.budgetbuddy.app.data.local.dao.CategoryDao
import com.budgetbuddy.app.data.local.dao.ExpenseDao
import com.budgetbuddy.app.data.local.dao.IncomeDao
import com.budgetbuddy.app.data.local.entity.CategoryEntity
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity

@Database(
    entities = [ExpenseEntity::class, IncomeEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao
}
