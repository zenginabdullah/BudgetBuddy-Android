package com.budgetbuddy.app.data.remote

import android.util.Log
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseDataSourceImpl : FirebaseDataSource {
    // Firebase Auth ve Firestore örnekleri
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Oturum açmış kullanıcının UID’si
    private fun currentUserId(): String =
        auth.currentUser?.uid ?: throw IllegalStateException("Kullanıcı giriş yapmamış")

    /**
     * Firestore’a yeni bir expense ekler.
     * Belge ID’si olarak Room’un atadığı Int ID’yi String’e çeviririz.
     */
    override suspend fun insertExpense(expense: ExpenseEntity) {
        val uid = currentUserId()
        Log.d("FirebaseDS", "Firestore’a expense yazılıyor: $expense, uid=$uid")

        // Room ID’sini String’e çevirip Firestore path olarak kullanıyoruz:
        val docRef = firestore
            .collection("users")
            .document(uid)
            .collection("expenses")
            .document(expense.id.toString())

        val data = mapOf(
            "id" to expense.id,       // opsiyonel: Firestore’da id alanı olarak saklıyoruz
            "amount" to expense.amount,
            "category" to expense.category,
            "description" to expense.description,
            "date" to expense.date
        )

        // Her set çağrısı, eğer doc yoksa oluşturur; varsa üzerine yazar
        docRef.set(data).await()
    }

    /**
     * Firestore’daki tüm giderleri getirir.
     * Belge ID’sinin “id” alanından Int olarak döndürürüz.
     */
    override suspend fun getAllExpenses(): List<ExpenseEntity> {
        val uid = currentUserId()
        val snapshot = firestore
            .collection("users")
            .document(uid)
            .collection("expenses")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            // Firestore’da sakladığımız “id” alanını Int olarak alıyoruz:
            val idLong = doc.getLong("id") ?: return@mapNotNull null
            val amount = doc.getDouble("amount") ?: return@mapNotNull null
            val category = doc.getString("category") ?: return@mapNotNull null
            val description = doc.getString("description") ?: return@mapNotNull null
            val date = doc.getString("date") ?: return@mapNotNull null

            ExpenseEntity(
                id = idLong.toInt(),      // Int ID
                amount = amount,
                category = category,
                description = description,
                date = date,
                userId = uid
            )
        }
    }

    /**
     * Firestore’dan girilen ID’ye göre siler.
     */
    override suspend fun deleteExpense(expenseId: Int) {
        val uid = currentUserId()
        firestore
            .collection("users")
            .document(uid)
            .collection("expenses")
            .document(expenseId.toString())
            .delete()
            .await()
    }

    // --- Income Metotları (yeni eklenenler) ---
    override suspend fun insertIncome(income: IncomeEntity) {
        val uid = currentUserId()
        Log.d("FirebaseDS", "Firestore’a income yazılıyor: $income, uid=$uid")
        val docRef = firestore
            .collection("users")
            .document(uid)
            .collection("incomes")
            .document(income.id.toString())
        val data = mapOf(
            "id" to income.id,
            "amount" to income.amount,
            "category" to income.category,
            "description" to income.description,
            "date" to income.date
        )
        docRef.set(data).await()
    }

    override suspend fun getAllIncomes(): List<IncomeEntity> {
        val uid = currentUserId()
        val snapshot = firestore
            .collection("users")
            .document(uid)
            .collection("incomes")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val idLong = doc.getLong("id") ?: return@mapNotNull null
            val amount = doc.getDouble("amount") ?: return@mapNotNull null
            val category = doc.getString("category") ?: return@mapNotNull null
            val description = doc.getString("description") ?: return@mapNotNull null
            val date = doc.getString("date") ?: return@mapNotNull null

            IncomeEntity(
                id = idLong.toInt(),
                amount = amount,
                category = category,
                description = description,
                date = date,
                userId = uid
            )
        }
    }

    override suspend fun deleteIncome(incomeId: Int) {
        val uid = currentUserId()
        firestore
            .collection("users")
            .document(uid)
            .collection("incomes")
            .document(incomeId.toString())
            .delete()
            .await()
    }
}