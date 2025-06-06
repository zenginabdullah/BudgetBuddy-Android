package com.budgetbuddy.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.budgetbuddy.app.data.local.entity.ExpenseEntity
import com.budgetbuddy.app.data.local.entity.IncomeEntity
import com.budgetbuddy.app.ui.screens.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class ChatBotViewModel @Inject constructor() : ViewModel() {

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun addMessage(message: ChatMessage) {
        _chatMessages.value = _chatMessages.value + message
    }

    fun removeLastMessage() {
        if (_chatMessages.value.isNotEmpty()) {
            _chatMessages.value = _chatMessages.value.dropLast(1)
        }
    }

    suspend fun generateResponse(
        userMessage: String,
        expenses: List<ExpenseEntity>,
        incomes: List<IncomeEntity>
    ): String = withContext(Dispatchers.Default) {
        try {
            // Basit bir AI yanıtı oluştur
            val lowercaseMessage = userMessage.lowercase()
            val totalIncome = incomes.sumOf { it.amount }
            val totalExpense = expenses.sumOf { it.amount }
            val balance = totalIncome - totalExpense

            // Kategori bazlı harcama özeti
            val categoryExpenses = expenses.groupBy { it.category }
                .mapValues { it.value.sumOf { expense -> expense.amount } }

            // En yüksek harcama kategorisi
            val highestExpenseCategory = categoryExpenses.maxByOrNull { it.value }

            // En düşük harcama kategorisi
            val lowestExpenseCategory = categoryExpenses.minByOrNull { it.value }

            // Kullanıcı sorusuna göre yanıt oluştur
            when {
                // Genel durum sorguları
                lowercaseMessage.contains("durum") || lowercaseMessage.contains("özet") ||
                        lowercaseMessage.contains("nasıl") && lowercaseMessage.contains("gidiyor") -> {
                    buildString {
                        append("Finansal durumunuzun özeti şu şekilde:\n\n")
                        append("Toplam Gelir: %.2f TL\n".format(totalIncome))
                        append("Toplam Gider: %.2f TL\n".format(totalExpense))
                        append("Bakiye: %.2f TL\n\n".format(balance))

                        if (balance > 0) {
                            append("Tebrikler! Pozitif bir bakiyeye sahipsiniz. ")
                            if (balance < totalIncome * 0.2) {
                                append("Ancak bakiyeniz toplam gelirinizin %20'sinden az. Biraz daha tasarruf yapmayı düşünebilirsiniz.")
                            } else {
                                append("Bakiyeniz gayet iyi durumda. Bu parayı yatırıma yönlendirebilirsiniz.")
                            }
                        } else if (balance < 0) {
                            append("Dikkat! Negatif bir bakiyeye sahipsiniz. Harcamalarınızı azaltmanız veya ek gelir bulmanız gerekebilir.")
                        } else {
                            append("Geliriniz ve gideriniz eşit. Tasarruf yapabilmek için harcamalarınızı azaltmayı düşünebilirsiniz.")
                        }
                    }
                }

                // Tasarruf önerileri
                lowercaseMessage.contains("tasarruf") || lowercaseMessage.contains("biriktirebilir") ||
                        lowercaseMessage.contains("nasıl") && (lowercaseMessage.contains("biriktirebilirim") || lowercaseMessage.contains("tasarruf")) -> {
                    buildString {
                        append("İşte size özel tasarruf önerileri:\n\n")

                        // En yüksek harcama kategorisine göre öneri
                        if (highestExpenseCategory != null) {
                            append("En çok harcama yaptığınız kategori: ${highestExpenseCategory.key} (%.2f TL)\n".format(highestExpenseCategory.value))

                            when (highestExpenseCategory.key.lowercase()) {
                                "gıda" -> append("Gıda harcamalarınızı azaltmak için evde yemek yapmayı, toplu alışveriş yapmayı ve indirimli ürünleri tercih etmeyi deneyebilirsiniz.\n")
                                "market" -> append("Market harcamalarınızı azaltmak için alışveriş listesi hazırlayabilir, indirim günlerini takip edebilir ve marka bağımlılığınızı azaltabilirsiniz.\n")
                                "ulaşım" -> append("Ulaşım masraflarınızı azaltmak için toplu taşıma, bisiklet kullanımı veya araç paylaşımı gibi alternatifleri değerlendirebilirsiniz.\n")
                                "eğlence" -> append("Eğlence harcamalarınızı azaltmak için ücretsiz etkinlikler bulabilir veya abonelik hizmetlerinizi gözden geçirebilirsiniz.\n")
                                "fatura" -> append("Fatura giderlerinizi azaltmak için enerji tasarrufu yapabilir, kullanmadığınız cihazları fişten çekebilir veya daha uygun tarife planları araştırabilirsiniz.\n")
                                "kira" -> append("Kira gideriniz bütçenizde önemli bir yer kaplıyor. Daha uygun bir eve taşınmayı veya ev arkadaşı bulmayı düşünebilirsiniz.\n")
                                "giyim" -> append("Giyim harcamalarınızı azaltmak için sezon sonu indirimlerini bekleyebilir, ikinci el alışveriş yapabilir veya kapsül gardırop oluşturabilirsiniz.\n")
                                "sağlık" -> append("Sağlık harcamalarınızı azaltmak için koruyucu sağlık hizmetlerine önem verebilir ve düzenli check-up yaptırabilirsiniz.\n")
                                "eğitim" -> append("Eğitim harcamalarınızı azaltmak için ücretsiz online kurslar, kütüphane kaynakları ve burs imkanlarını araştırabilirsiniz.\n")
                                else -> append("${highestExpenseCategory.key} kategorisindeki harcamalarınızı gözden geçirip, gereksiz olanları azaltmayı deneyebilirsiniz.\n")
                            }
                        }

                        append("\nGenel tasarruf önerileri:\n")
                        append("1. 50/30/20 kuralını uygulayın: Gelirinizin %50'sini ihtiyaçlara, %30'unu isteklere ve %20'sini tasarrufa ayırın.\n")
                        append("2. Otomatik tasarruf sistemi kurun: Maaşınız yattığında belirli bir miktarı otomatik olarak tasarruf hesabına aktarın.\n")
                        append("3. Gereksiz abonelikleri iptal edin.\n")
                        append("4. Alışverişlerde 24 saat kuralını uygulayın: Büyük alışverişler için 24 saat bekleyin ve gerçekten ihtiyacınız olup olmadığını düşünün.\n")
                        append("5. Fatura ve abonelik giderlerinizi düzenli olarak gözden geçirin ve daha uygun alternatifleri araştırın.")
                    }
                }

                // Bütçe planlaması
                lowercaseMessage.contains("bütçe") || lowercaseMessage.contains("plan") ||
                        lowercaseMessage.contains("nasıl") && lowercaseMessage.contains("planlayabilirim") -> {
                    buildString {
                        append("Bütçe planlaması için önerilerim:\n\n")

                        // Gelir-gider oranına göre bütçe önerisi
                        val expenseRatio = if (totalIncome > 0) totalExpense / totalIncome else 0.0

                        append("Şu anda harcamalarınız gelirinizin %.1f%%'ini oluşturuyor.\n\n".format(expenseRatio * 100))

                        append("İdeal bir bütçe dağılımı şöyle olabilir:\n")
                        append("- Zorunlu harcamalar (kira, faturalar, gıda): Gelirinizin %50-60'ı\n")
                        append("- Kişisel harcamalar (eğlence, alışveriş): Gelirinizin %20-30'u\n")
                        append("- Tasarruf ve yatırım: Gelirinizin %20'si\n\n")

                        // Kategori bazlı bütçe önerisi
                        append("Kategori bazlı harcamalarınıza göre aylık bütçe önerisi:\n")
                        val idealBudget = totalIncome * 0.8 // Gelirin %80'i harcamaya

                        if (categoryExpenses.isNotEmpty()) {
                            val totalExpenseSum = categoryExpenses.values.sum()
                            categoryExpenses.forEach { (category, amount) ->
                                val ratio = if (totalExpenseSum > 0) amount / totalExpenseSum else 0.0
                                val suggestedBudget = idealBudget * ratio
                                append("- $category: %.2f TL\n".format(suggestedBudget))
                            }
                        }

                        append("\nEtkili bütçe planlaması için adımlar:\n")
                        append("1. Tüm gelir ve giderlerinizi kaydedin.\n")
                        append("2. Harcamalarınızı zorunlu ve isteğe bağlı olarak sınıflandırın.\n")
                        append("3. Her kategori için aylık limit belirleyin.\n")
                        append("4. Düzenli olarak bütçenizi gözden geçirin ve gerekirse ayarlayın.\n")
                        append("5. Acil durumlar için bir fon oluşturun (3-6 aylık giderlerinizi karşılayacak miktarda).")
                    }
                }

                // Harcama analizi
                lowercaseMessage.contains("analiz") || lowercaseMessage.contains("harcama") ||
                        lowercaseMessage.contains("nereye") && lowercaseMessage.contains("harcıyorum") -> {
                    buildString {
                        append("Harcama analiziniz:\n\n")

                        if (categoryExpenses.isNotEmpty()) {
                            append("Kategori bazlı harcama dağılımınız:\n")
                            categoryExpenses.forEach { (category, amount) ->
                                val percentage = if (totalExpense > 0) amount * 100 / totalExpense else 0.0
                                append("- $category: %.2f TL (%.1f%%)\n".format(amount, percentage))
                            }

                            append("\nEn yüksek harcama yaptığınız kategori: ")
                            if (highestExpenseCategory != null) {
                                append("${highestExpenseCategory.key} (%.2f TL)\n".format(highestExpenseCategory.value))
                            }

                            append("\nEn düşük harcama yaptığınız kategori: ")
                            if (lowestExpenseCategory != null) {
                                append("${lowestExpenseCategory.key} (%.2f TL)\n".format(lowestExpenseCategory.value))
                            }

                            // Ortalama günlük harcama
                            val dailyAverage = totalExpense / 30
                            append("\nOrtalama günlük harcamanız: %.2f TL\n".format(dailyAverage))

                            // Gelir-gider oranı
                            val expenseToIncomeRatio = if (totalIncome > 0) totalExpense / totalIncome else 0.0
                            append("Harcama/Gelir oranınız: %.2f%%\n".format(expenseToIncomeRatio * 100))

                            if (expenseToIncomeRatio > 0.9) {
                                append("\nUyarı: Harcamalarınız gelirinizin %90'ından fazlasını oluşturuyor. Bu durum finansal güvenliğiniz için risk oluşturabilir.")
                            }
                        } else {
                            append("Henüz harcama kaydınız bulunmuyor. Harcamalarınızı ekledikçe daha detaylı analiz sunabilirim.")
                        }
                    }
                }

                // Yatırım önerileri
                lowercaseMessage.contains("yatırım") || lowercaseMessage.contains("biriktir") ||
                        lowercaseMessage.contains("nasıl") && (lowercaseMessage.contains("değerlendirebilirim") || lowercaseMessage.contains("yatırım")) -> {
                    buildString {
                        append("Yatırım ve birikim önerileri:\n\n")

                        if (balance > 0) {
                            append("Tebrikler! %.2f TL pozitif bakiyeniz var. Bu parayı değerlendirebileceğiniz bazı yollar:\n\n".format(balance))
                        } else {
                            append("Şu anda negatif veya sıfır bakiyeniz var. Öncelikle bir acil durum fonu oluşturmayı hedeflemelisiniz. İşte yatırım yapmaya başlamadan önce izleyebileceğiniz adımlar:\n\n")
                        }

                        append("1. Acil durum fonu: 3-6 aylık giderlerinizi karşılayacak bir fon oluşturun.\n")
                        append("2. Düşük riskli yatırımlar: Vadeli mevduat, devlet tahvili, altın gibi düşük riskli araçlarla başlayabilirsiniz.\n")
                        append("3. Emeklilik fonu: Bireysel emeklilik sistemine katılarak uzun vadeli birikim yapabilirsiniz.\n")
                        append("4. Hisse senetleri ve yatırım fonları: Daha yüksek getiri potansiyeli olan ancak daha riskli yatırım araçlarını düşünebilirsiniz.\n")
                        append("5. Gayrimenkul: Uzun vadeli ve genellikle güvenli bir yatırım aracıdır.\n\n")

                        append("Not: Yatırım kararları vermeden önce bir finans danışmanına başvurmanızı öneririm.")
                    }
                }

                // Kategori önerileri
                lowercaseMessage.contains("kategori") || lowercaseMessage.contains("sınıflandır") -> {
                    buildString {
                        append("Bütçe takibi için kullanabileceğiniz harcama kategorileri:\n\n")
                        append("Temel Kategoriler:\n")
                        append("1. Barınma: Kira, mortgage, site aidatı, ev bakım/onarım\n")
                        append("2. Gıda: Market alışverişleri, dışarıda yemek\n")
                        append("3. Ulaşım: Yakıt, toplu taşıma, taksi, araç bakımı\n")
                        append("4. Faturalar: Elektrik, su, doğalgaz, internet, telefon\n")
                        append("5. Sağlık: İlaçlar, doktor ziyaretleri, sigorta\n")
                        append("6. Eğitim: Kurslar, kitaplar, okul masrafları\n")
                        append("7. Eğlence: Sinema, konser, abonelikler, hobiler\n")
                        append("8. Giyim: Kıyafet, ayakkabı, aksesuar\n")
                        append("9. Kişisel bakım: Kozmetik, kuaför, spor salonu\n")
                        append("10. Tasarruf/Yatırım: Biriktirdiğiniz veya yatırım yaptığınız miktar\n\n")

                        append("Mevcut harcamalarınızda kullandığınız kategoriler:\n")
                        if (categoryExpenses.isNotEmpty()) {
                            categoryExpenses.keys.forEach { category ->
                                append("- $category\n")
                            }
                        } else {
                            append("Henüz harcama kaydınız bulunmuyor.")
                        }
                    }
                }

                // Gelir önerileri
                lowercaseMessage.contains("gelir") || lowercaseMessage.contains("kazanç") ||
                        lowercaseMessage.contains("nasıl") && lowercaseMessage.contains("kazanabilirim") -> {
                    buildString {
                        append("Ek gelir elde etmek için öneriler:\n\n")
                        append("1. Freelance çalışma: Yeteneklerinizi kullanarak proje bazlı işler yapabilirsiniz (yazılım, tasarım, çeviri, içerik üretimi vb.)\n")
                        append("2. Yan iş: Hafta sonları veya akşamları part-time bir işte çalışabilirsiniz.\n")
                        append("3. Özel ders: Bilgi sahibi olduğunuz konularda özel ders verebilirsiniz.\n")
                        append("4. İkinci el satış: Kullanmadığınız eşyaları online platformlarda satabilirsiniz.\n")
                        append("5. Ev paylaşımı: Evinizin bir odasını kiraya verebilirsiniz.\n")
                        append("6. Araç paylaşımı: Arabanızı yolculuk paylaşım uygulamalarında değerlendirebilirsiniz.\n")
                        append("7. El işi ürünler: El yapımı ürünler üretip satabilirsiniz.\n")
                        append("8. Online kurs: Uzmanlık alanınızda online kurslar hazırlayabilirsiniz.\n")
                        append("9. Anket ve uygulama testleri: Boş zamanlarınızda anketlere katılabilir veya uygulama testleri yapabilirsiniz.\n")
                        append("10. Yatırım gelirleri: Birikimlerinizi doğru yatırım araçlarında değerlendirerek pasif gelir elde edebilirsiniz.")
                    }
                }

                // Borç yönetimi
                lowercaseMessage.contains("borç") || lowercaseMessage.contains("kredi") -> {
                    buildString {
                        append("Borç yönetimi için öneriler:\n\n")
                        append("1. Tüm borçlarınızı listeleyin: Miktar, faiz oranı ve son ödeme tarihlerini not edin.\n")
                        append("2. Çığ yöntemi: En küçük borçtan başlayarak ödeyin. Bu motivasyonunuzu artırır.\n")
                        append("3. Çağlayan yöntemi: En yüksek faizli borçtan başlayarak ödeyin. Bu matematiksel olarak en verimli yöntemdir.\n")
                        append("4. Acil durum fonu oluşturun: Beklenmedik durumlar için en az 1 aylık gider tutarında bir fon bulundurun.\n")
                        append("5. Borç konsolidasyonu: Mümkünse, yüksek faizli borçlarınızı daha düşük faizli tek bir kredi altında toplayın.\n")
                        append("6. Otomatik ödeme talimatı verin: Gecikme faizlerinden kaçınmak için otomatik ödeme talimatları oluşturun.\n")
                        append("7. Ek gelir kaynakları bulun: Borçlarınızı daha hızlı ödemek için ek gelir kaynakları araştırın.\n")
                        append("8. Harcamalarınızı kısın: Borç ödemelerine daha fazla bütçe ayırabilmek için gereksiz harcamalarınızı azaltın.")
                    }
                }

                // Genel sorular
                else -> {
                    "Sorunuzu tam olarak anlayamadım. Bütçe planlaması, tasarruf önerileri, harcama analizi, yatırım tavsiyeleri, gelir artırma yöntemleri veya borç yönetimi hakkında sorular sorabilirsiniz. Size finansal durumunuza özel yanıtlar verebilirim."
                }
            }
        } catch (e: Exception) {
            Log.e("ChatBotViewModel", "Error generating response: ${e.message}")
            "Üzgünüm, yanıt oluştururken bir hata oluştu. Lütfen tekrar deneyin."
        }
    }
}