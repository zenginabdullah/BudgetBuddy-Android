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
import com.budgetbuddy.app.data.repository.IncomeRepository
import com.budgetbuddy.app.data.local.dao.IncomeDao
import com.budgetbuddy.app.data.remote.FirebaseDataSource
import com.budgetbuddy.app.data.remote.FirebaseDataSourceImpl

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
    )
    .fallbackToDestructiveMigration()
    .build()
}

@Provides
@Singleton
fun provideFirebaseDataSource(): FirebaseDataSource {
    return FirebaseDataSourceImpl()
}

    @Provides
    fun provideExpenseDao(db: AppDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideIncomeDao(db: AppDatabase): IncomeDao = db.incomeDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()

    @Provides
    @Singleton
    fun provideExpenseRepository(
        expenseDao: ExpenseDao,
        incomeDao: IncomeDao,
        firebaseDataSource: FirebaseDataSource
    ): ExpenseRepository {
        return ExpenseRepository(expenseDao, incomeDao, firebaseDataSource)
    }

    @Provides
    @Singleton
    fun provideIncomeRepository(
        incomeDao: IncomeDao,
        firebaseDataSource: FirebaseDataSource
    ): IncomeRepository {
        return IncomeRepository(incomeDao, firebaseDataSource)
    }
}
