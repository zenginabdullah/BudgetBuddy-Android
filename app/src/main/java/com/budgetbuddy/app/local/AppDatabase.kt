package com.budgetbuddy.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.budgetbuddy.app.data.local.dao.CategoryDao
import com.budgetbuddy.app.data.local.dao.ExpenseDao
import com.budgetbuddy.app.data.local.dao.IncomeDao
import com.budgetbuddy.app.data.local.entity.CategoryEntity
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity

@Database(
    entities = [ExpenseEntity::class, IncomeEntity::class, CategoryEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "budget_buddy_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
