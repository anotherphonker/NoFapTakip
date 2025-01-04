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
import kotlinx.coroutines.launch
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.resulbey.nofaptakip.updater.UpdateManager
import android.content.Context
import android.content.Intent
import android.content.PendingIntent
import android.app.AlarmManager
import android.content.pm.PackageManager
import android.os.Build

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
    private lateinit var updateManager: UpdateManager
    private lateinit var checkUpdateButton: MaterialButton
    
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
        // Genel Motivasyon
        "Bugün zor olabilir, ama yarın daha güçlü olacaksın!",
        "Her 'Hayır' dediğinde, iradenin gücü artıyor.",
        "Geçmiş seni tanımlamaz, bugünkü seçimlerin geleceğini belirler.",
        "Kendini geliştirmek için attığın her adım değerlidir.",
        "Zorluklar seni yıldırmasın, her zorluk yeni bir fırsat!",
        "Başarı bir süreçtir, bir anda olacak bir şey değil.",
        "Her gün yeni bir başlangıç, her an yeni bir fırsat!",
        "Güçlü ol, kararlı ol, başaracaksın!",

        // Bilimsel Gerçekler
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

        // Bağımlılık ve İyileşme
        "Bağımlılık beynini ele geçirmiş olabilir, ama sen beyninden daha güçlüsün!",
        "Her 'Hayır' dediğinde, yeni ve sağlıklı nöral bağlantılar oluşturuyorsun.",
        "Porno, beyninin gerçeklik algısını bozar. Sen gerçek hayatı seç!",
        "Bağımlılık döngüsünü kırmak zor olabilir, ama imkansız değil. Sen yapabilirsin!",
        "Beynin plastisite özelliğine sahip, yani kendini onarabilir. Ona bu şansı ver!",
        "Her gün temiz kaldığında, beynin biraz daha normale dönüyor.",
        "Gerçek mutluluk, dopamin bağımlılığından kurtulmakla başlar.",
        "Porno, beyninin ödül sistemini hackler. Sen kontrolü geri al!",

        // Yeni Eklenen Mesajlar
        "Her zorluk, daha güçlü bir karakterin tohumudur.",
        "Bugün vazgeçmemek, yarın için gurur kaynağın olacak.",
        "Gerçek özgürlük, dürtülerini kontrol edebilmektir.",
        "Kendine yatırım yap, geleceğin sana minnettar olacak.",
        "Zor zamanlar güçlü insanlar yaratır.",
        "Her başarısızlık, başarıya giden yolda bir derstir.",
        "Değişim zordur, ama pişmanlık daha zordur.",
        "Küçük adımlar, büyük değişimlerin başlangıcıdır.",
        "Beynin senin düşmanın değil, sadece yeniden eğitilmesi gerekiyor.",
        "Bağımlılık bir hastalıktır, ama iyileşmek senin elinde.",
        "Her gün yeni bir zafer şansıdır.",
        "Geçmişini değiştiremezsin, ama geleceğini şekillendirebilirsin.",
        "Başarı bir seçimdir, her gün bu seçimi yapıyorsun.",
        "İrade kası gibidir, kullandıkça güçlenir.",
        "Zorluklarla yüzleşmek, karakterini güçlendirir.",
        "Her 'Hayır' bir zaferdir, her zafer bir adımdır.",
        "Bağımlılık zincirlerini kır, özgürlüğünü geri al!",
        "Sen düşündüğünden çok daha güçlüsün.",
        "Değişim içeriden başlar, dışarıdan görünür.",
        "Bugünün fedakarlığı, yarının zaferidir."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        prefs = getSharedPreferences("NoFapPrefs", MODE_PRIVATE)
        updateManager = UpdateManager(this)
        
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
        checkUpdateButton = findViewById(R.id.checkUpdateButton)
        
        setupWelcomeMessage()
        updateStreak()
        setupButtons()
        updateBadges()
        updateDarkModeButton()
        requestNotificationPermission()
        setupDailyNotification()
        checkForUpdates()
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
        
        checkUpdateButton.setOnClickListener {
            checkForUpdates(showLoading = true)
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
        
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Neden Relapse Oldun?")
            .setView(dialogView)
            .setPositiveButton("Kaydet", null) // Butonu şimdilik null olarak ayarlıyoruz
            .setNegativeButton("İptal", null)
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val reason = reasonInput.text.toString()
                if (reason.isBlank()) {
                    reasonInput.error = "Lütfen bir sebep girin"
                    return@setOnClickListener
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
        }
        
        dialog.show()
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

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                MaterialAlertDialogBuilder(this)
                    .setTitle("Bildirim İzni")
                    .setMessage("Günlük motivasyon mesajları ve rozet kazanımlarını alabilmek için bildirim iznine ihtiyacımız var.")
                    .setPositiveButton("İzin Ver") { _, _ ->
                        requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }
    
    private fun setupDailyNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, DailyNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.DAY_OF_YEAR, 1)
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    private fun checkForUpdates(showLoading: Boolean = false) {
        var progressDialog: AlertDialog? = null
        
        if (showLoading) {
            progressDialog = MaterialAlertDialogBuilder(this)
                .setTitle("Güncelleme Kontrolü")
                .setMessage("Güncellemeler kontrol ediliyor...")
                .setView(R.layout.dialog_progress)
                .setCancelable(false)
                .create()
            progressDialog.show()
        }
        
        lifecycleScope.launch {
            try {
                val updateInfo = updateManager.checkForUpdates()
                progressDialog?.dismiss()
                
                if (updateInfo.hasUpdate) {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle("Güncelleme Mevcut!")
                        .setMessage("Yeni sürüm (${updateInfo.latestVersion}) mevcut. Güncellemek ister misiniz?")
                        .setPositiveButton("Güncelle") { _, _ ->
                            updateManager.requestInstallPermission()
                            updateManager.downloadUpdate(updateInfo.downloadUrl)
                        }
                        .setNegativeButton("Daha Sonra", null)
                        .setCancelable(false)
                        .show()
                } else if (showLoading) {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle("Güncelleme Kontrolü")
                        .setMessage("Uygulamanız güncel! (${BuildConfig.VERSION_NAME})")
                        .setPositiveButton("Tamam", null)
                        .show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Uygulamanız güncel! (${BuildConfig.VERSION_NAME})",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                progressDialog?.dismiss()
                MaterialAlertDialogBuilder(this@MainActivity)
                    .setTitle("Hata")
                    .setMessage(e.message)
                    .setPositiveButton("Tamam", null)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            // Kullanıcı bilinmeyen kaynaklardan yükleme iznini verdi
            updateManager.installUpdate()
        }
    }
} 