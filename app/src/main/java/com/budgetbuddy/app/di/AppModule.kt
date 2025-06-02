package com.budgetbuddy.app.di

import android.content.Context
import androidx.room.Room
import com.budgetbuddy.app.data.local.AppDatabase
import com.budgetbuddy.app.data.local.dao.*
import com.budgetbuddy.app.data.repository.ExpenseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton
import com.budgetbuddy.app.repository.IncomeRepository
import com.budgetbuddy.app.data.local.dao.IncomeDao

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "budget_buddy_db"
        ).build()
    }

    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideIncomeDao(db: AppDatabase): IncomeDao = db.incomeDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideExpenseRepository(
        expenseDao: ExpenseDao,
        incomeDao: IncomeDao
    ): ExpenseRepository {
        return ExpenseRepository(expenseDao, incomeDao)
    }

    @Provides
    fun provideIncomeRepository(incomeDao: IncomeDao): IncomeRepository {
        return IncomeRepository(incomeDao)
    }
}
