package com.resulbey.nofaptakip

import android.app.AlertDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var welcomeText: TextView
    private lateinit var streakText: TextView
    private lateinit var nextBadgeText: TextView
    private lateinit var badgesCard: MaterialCardView
    private lateinit var badgesList: TextView
    private lateinit var darkModeButton: MaterialButton
    private lateinit var historyButton: MaterialButton
    private lateinit var allBadgesButton: MaterialButton
    private lateinit var relapseButton: MaterialButton
    private lateinit var emergencyButton: MaterialButton
    
    private val badges = mapOf(
        1 to Pair("Yeni Başlangıç", "İlk adımı attın! Her yolculuk tek bir adımla başlar."),
        3 to Pair("Kararlı", "3 gün dayanarak kararlılığını gösterdin!"),
        7 to Pair("Haftalık Başarı", "Bir haftayı tamamladın! Beynin iyileşmeye başlıyor."),
        14 to Pair("İki Haftalık Savaşçı", "İki hafta! Artık yeni alışkanlıklar oluşturmaya başladın."),
        30 to Pair("Aylık Şampiyon", "Bir ay! İnanılmaz bir başarı, dopamin seviyen dengeleniyor."),
        90 to Pair("Üç Aylık Efsane", "90 gün! Beynin tamamen yenilendi, yeni sen ortaya çıktı."),
        180 to Pair("Altı Aylık Usta", "180 gün! Artık tam bir kontrol ustasısın."),
        365 to Pair("Yıllık Efsane", "Bir yıl! Sen artık bir ilham kaynağısın.")
    )
    
    private val motivationalTips = arrayOf(
        "Bugün zor olabilir, ama yarın daha güçlü olacaksın!",
        "Her 'Hayır' dediğinde, iradenin gücü artıyor.",
        "Geçmiş seni tanımlamaz, bugünkü seçimlerin geleceğini belirler.",
        "Kendini geliştirmek için attığın her adım değerlidir.",
        "Zorluklar seni yıldırmasın, her zorluk yeni bir fırsat!",
        "Başarı bir süreçtir, bir anda olacak bir şey değil.",
        "Her gün yeni bir başlangıç, her an yeni bir fırsat!",
        "Güçlü ol, kararlı ol, başaracaksın!",
        "Pornografi beynindeki ödül sistemini bozar ve doğal motivasyonunu azaltır.",
        "Aşırı dopamin salınımı, beyninin normal aktivitelere karşı duyarsızlaşmasına neden olur.",
        "Mastürbasyon bağımlılığı, sosyal ilişkilerini ve özgüvenini olumsuz etkiler.",
        "Porno izlemek, gerçek ilişkilere olan ilgini ve tatmin duygunu azaltır.",
        "Her geçen gün beynin iyileşiyor, dopamin reseptörlerin normale dönüyor.",
        "Beynin artık daha az uyarıcıya ihtiyaç duyuyor, bu harika bir gelişme!",
        "Frontal lobun güçleniyor, karar verme ve dürtü kontrolün gelişiyor.",
        "Nöral yolların yenileniyor, bağımlılık döngüsünden çıkıyorsun!",
        "Beynindeki gri madde yoğunluğu artıyor, bu daha iyi odaklanma demek!",
        "Testosteron seviyen dengeleniyor, enerjin ve motivasyonun artıyor.",
        "Dopamin detoksu yapıyorsun, yakında hayattan daha çok zevk alacaksın.",
        "Prefrontal korteksin güçleniyor, irade gücün her gün artıyor.",
        "Bağımlılık beynini ele geçirmiş olabilir, ama sen beynindan daha güçlüsün!",
        "Her 'Hayır' dediğinde, yeni ve sağlıklı nöral bağlantılar oluşturuyorsun.",
        "Porno, beyninin gerçeklik algısını bozar. Sen gerçek hayatı seç!",
        "Bağımlılık döngüsünü kırmak zor olabilir, ama imkansız değil. Sen yapabilirsin!",
        "Beynin plastisite özelliğine sahip, yani kendini onarabilir. Ona bu şansı ver!",
        "Her gün temiz kaldığında, beynin biraz daha normale dönüyor.",
        "Gerçek mutluluk, dopamin bağımlılığından kurtulmakla başlar.",
        "Porno, beyninin ödül sistemini hackler. Sen kontrolü geri al!"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        prefs = getSharedPreferences("NoFapPrefs", MODE_PRIVATE)
        
        welcomeText = findViewById(R.id.welcomeText)
        streakText = findViewById(R.id.streakText)
        nextBadgeText = findViewById(R.id.nextBadgeText)
        badgesCard = findViewById(R.id.badgesCard)
        badgesList = findViewById(R.id.badgesList)
        darkModeButton = findViewById(R.id.darkModeButton)
        historyButton = findViewById(R.id.historyButton)
        allBadgesButton = findViewById(R.id.allBadgesButton)
        relapseButton = findViewById(R.id.relapseButton)
        emergencyButton = findViewById(R.id.emergencyButton)
        
        setupWelcomeMessage()
        updateStreak()
        setupButtons()
        updateBadges()
        updateDarkModeButton()
    }
    
    private fun setupWelcomeMessage() {
        val name = prefs.getString("user_name", "") ?: ""
        welcomeText.text = "Hoş geldin, $name! Yolculuğunda seninle birlikteyiz."
    }
    
    private fun updateStreak() {
        val startDate = prefs.getLong("startDate", 0)
        if (startDate == 0L) return
        
        val currentStreak = calculateStreak(startDate)
        streakText.text = "$currentStreak gündür temizsin!"
        
        // Bir sonraki rozete kalan günleri hesapla
        val nextBadge = badges.entries.find { it.key > currentStreak }
        if (nextBadge != null) {
            val daysLeft = nextBadge.key - currentStreak
            nextBadgeText.text = "${nextBadge.value.first} rozetine $daysLeft gün kaldı!"
        } else {
            nextBadgeText.text = "Tüm rozetleri kazandın! Muhteşemsin!"
        }
    }
    
    private fun calculateStreak(startDate: Long): Int {
        val start = Calendar.getInstance().apply { timeInMillis = startDate }
        val now = Calendar.getInstance()
        
        return abs((now.timeInMillis - start.timeInMillis) / (24 * 60 * 60 * 1000)).toInt()
    }
    
    private fun updateBadges() {
        val currentStreak = calculateStreak(prefs.getLong("startDate", 0))
        val earnedBadges = badges.filter { it.key <= currentStreak }
        
        if (earnedBadges.isEmpty()) {
            badgesCard.visibility = View.GONE
            return
        }
        
        badgesCard.visibility = View.VISIBLE
        val badgesText = StringBuilder()
        earnedBadges.forEach { (days, badge) ->
            badgesText.append("🏆 ${badge.first} (${badge.second})\n")
        }
        badgesList.text = badgesText.toString().trimEnd()
    }
    
    private fun setupButtons() {
        darkModeButton.setOnClickListener {
            toggleDarkMode()
        }
        
        historyButton.setOnClickListener {
            showHistory()
        }
        
        allBadgesButton.setOnClickListener {
            showAllBadges()
        }
        
        relapseButton.setOnClickListener {
            showRelapseDialog()
        }
        
        emergencyButton.setOnClickListener {
            showMotivationalTip()
        }
    }
    
    private fun toggleDarkMode() {
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        val newMode = !isDarkMode
        
        AppCompatDelegate.setDefaultNightMode(
            if (newMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
        
        prefs.edit().putBoolean("dark_mode", newMode).apply()
        updateDarkModeButton()
    }
    
    private fun updateDarkModeButton() {
        val isDarkMode = prefs.getBoolean("dark_mode", false)
        darkModeButton.text = if (isDarkMode) "Açık Tema" else "Koyu Tema"
        darkModeButton.setIconResource(
            if (isDarkMode) R.drawable.ic_light_mode
            else R.drawable.ic_dark_mode
        )
    }
    
    private fun showHistory() {
        val relapseHistory = mutableListOf<Pair<Long, String>>()
        var currentDate = prefs.getLong("startDate", 0)
        var lastReason = prefs.getString("lastRelapseReason", null)
        
        while (currentDate > 0 && lastReason != null) {
            relapseHistory.add(Pair(currentDate, lastReason))
            currentDate = prefs.getLong("startDate_$currentDate", 0)
            lastReason = prefs.getString("lastRelapseReason_$currentDate", null)
        }
        
        if (relapseHistory.isEmpty()) {
            MaterialAlertDialogBuilder(this)
                .setTitle("Geçmiş")
                .setMessage("Henüz hiç relapse olmadın. Harika gidiyorsun!")
                .setPositiveButton("Tamam", null)
                .show()
            return
        }
        
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("tr"))
        val historyText = StringBuilder()
        relapseHistory.forEachIndexed { index, (date, reason) ->
            historyText.append("${index + 1}. ${dateFormat.format(Date(date))}\n")
            historyText.append("Neden: $reason\n\n")
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Geçmiş")
            .setMessage(historyText.toString().trimEnd())
            .setPositiveButton("Tamam", null)
            .show()
    }
    
    private fun showAllBadges() {
        val currentStreak = calculateStreak(prefs.getLong("startDate", 0))
        val badgesText = StringBuilder()
        
        badges.forEach { (days, badge) ->
            val isEarned = days <= currentStreak
            val daysLeft = if (!isEarned) days - currentStreak else 0
            
            badgesText.append(if (isEarned) "🏆" else "🔒")
            badgesText.append(" ${badge.first}\n")
            badgesText.append("${badge.second}\n")
            if (!isEarned) {
                badgesText.append("Bu rozete ulaşmana $daysLeft gün kaldı!\n")
            }
            badgesText.append("\n")
        }
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Tüm Rozetler")
            .setMessage(badgesText.toString().trimEnd())
            .setPositiveButton("Tamam", null)
            .show()
    }
    
    private fun showRelapseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_relapse, null)
        val reasonInput = dialogView.findViewById<TextInputEditText>(R.id.reasonInput)
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Neden Relapse Oldun?")
            .setView(dialogView)
            .setPositiveButton("Kaydet") { dialog, _ ->
                val reason = reasonInput.text.toString()
                if (reason.isBlank()) {
                    reasonInput.error = "Lütfen bir sebep girin"
                    return@setPositiveButton
                }
                
                // Önceki relapse tarihini ve sebebini sakla
                val oldStartDate = prefs.getLong("startDate", 0)
                if (oldStartDate > 0) {
                    val oldReason = prefs.getString("lastRelapseReason", null)
                    if (oldReason != null) {
                        prefs.edit()
                            .putLong("startDate_$oldStartDate", oldStartDate)
                            .putString("lastRelapseReason_$oldStartDate", oldReason)
                            .apply()
                    }
                }
                
                // Yeni relapse tarihini ve sebebini kaydet
                val relapseDate = System.currentTimeMillis()
                prefs.edit()
                    .putLong("startDate", relapseDate)
                    .putString("lastRelapseReason", reason)
                    .apply()
                
                updateStreak()
                updateBadges()
                
                // Motivasyon mesajı göster
                showMotivationalTip()
                dialog.dismiss()
            }
            .setNegativeButton("İptal", null)
            .show()
    }
    
    private fun showMotivationalTip() {
        val randomTip = motivationalTips.random()
        MaterialAlertDialogBuilder(this)
            .setTitle("Motivasyon")
            .setMessage(randomTip)
            .setPositiveButton("Teşekkürler") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
} 