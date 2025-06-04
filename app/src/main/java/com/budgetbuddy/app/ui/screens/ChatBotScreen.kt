package com.budgetbuddy.app.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.viewmodel.ChatBotViewModel
import com.budgetbuddy.app.viewmodel.ExpenseViewModel
import com.budgetbuddy.app.viewmodel.IncomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


data class ChatMessage(
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    expenseViewModel: ExpenseViewModel,
    incomeViewModel: IncomeViewModel,
    chatBotViewModel: ChatBotViewModel = viewModel()
) {
    val expenses by expenseViewModel.allExpenses.collectAsState()
    val incomes by incomeViewModel.allIncomes.collectAsState()
    val chatMessages by chatBotViewModel.chatMessages.collectAsState()
    var userInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Kullanıcı verilerine göre ilk mesajı hazırla
    LaunchedEffect(expenses, incomes) {
        if (chatMessages.isEmpty()) {
            val totalIncome = incomes.sumOf { it.amount }
            val totalExpense = expenses.sumOf { it.amount }
            val balance = totalIncome - totalExpense

            // Kategori bazlı harcama özeti oluştur
            val categoryExpenses = expenses.groupBy { it.category }
                .mapValues { it.value.sumOf { expense -> expense.amount } }

            val initialMessage = buildString {
                append("Merhaba! Ben BütçeBuddy'nin finansal asistanıyım. ")
                append("Finansal durumunuza göre size özel tavsiyelerde bulunabilirim.\n\n")
                append("Mevcut finansal durumunuz:\n")
                append("Toplam Gelir: %.2f TL\n".format(totalIncome))
                append("Toplam Gider: %.2f TL\n".format(totalExpense))
                append("Bakiye: %.2f TL\n\n".format(balance))

                if (categoryExpenses.isNotEmpty()) {
                    append("Harcama kategorileriniz:\n")
                    categoryExpenses.forEach { (category, amount) ->
                        append("- $category: %.2f TL (%.1f%%)\n".format(amount, amount * 100 / totalExpense))
                    }
                }

                append("\nSize nasıl yardımcı olabilirim? Bütçe planlaması, tasarruf önerileri veya harcama analizi hakkında sorular sorabilirsiniz.")
            }

            chatBotViewModel.addMessage(ChatMessage(initialMessage, false))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Başlık
        Text(
            text = "Finansal Asistan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Sohbet mesajları
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = true
        ) {
            items(chatMessages.reversed()) { message ->
                ChatBubble(message = message)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mesaj girişi
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                placeholder = { Text("Mesajınızı yazın...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (userInput.isNotBlank()) {
                            sendMessage(userInput, chatBotViewModel, expenses, incomes, coroutineScope)
                            userInput = ""
                            focusManager.clearFocus()
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            IconButton(
                onClick = {
                    if (userInput.isNotBlank()) {
                        sendMessage(userInput, chatBotViewModel, expenses, incomes, coroutineScope)
                        userInput = ""
                        focusManager.clearFocus()
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Gönder",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

private fun sendMessage(
    message: String,
    chatBotViewModel: ChatBotViewModel,
    expenses: List<ExpenseEntity>,
    incomes: List<IncomeEntity>,
    coroutineScope: CoroutineScope
) {
    // Kullanıcı mesajını ekle
    val userMessage = ChatMessage(message, true)
    chatBotViewModel.addMessage(userMessage)

    // AI yanıtını oluştur ve ekle
    coroutineScope.launch {
        try {
            // Yükleniyor mesajı
            chatBotViewModel.addMessage(ChatMessage("Yanıt hazırlanıyor...", false))

            // AI yanıtını al
            val response = chatBotViewModel.generateResponse(message, expenses, incomes)

            // Yükleniyor mesajını kaldır
            chatBotViewModel.removeLastMessage()

            // Gerçek yanıtı ekle
            chatBotViewModel.addMessage(ChatMessage(response, false))
        } catch (e: Exception) {
            // Hata durumunda yükleniyor mesajını kaldır
            chatBotViewModel.removeLastMessage()

            // Hata mesajı ekle
            chatBotViewModel.addMessage(ChatMessage("Üzgünüm, bir hata oluştu. Lütfen tekrar deneyin.", false))
            Log.e("ChatBotScreen", "Error generating response", e)
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val backgroundColor = if (message.isFromUser)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.secondaryContainer

    val textColor = if (message.isFromUser)
        MaterialTheme.colorScheme.onPrimary
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    // Alignment değişkenini kaldırıp doğrudan Box içinde kullanıyorum
    val isUserMessage = message.isFromUser

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = if (isUserMessage) Alignment.TopEnd else Alignment.TopStart
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUserMessage) 16.dp else 0.dp,
                bottomEnd = if (isUserMessage) 0.dp else 16.dp
            ),
            color = backgroundColor,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.content,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

