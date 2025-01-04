package com.resulbey.nofaptakip

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.resulbey.nofaptakip.notification.DailyNotificationReceiver
import com.resulbey.nofaptakip.updater.UpdateInfo
import com.resulbey.nofaptakip.updater.UpdateManager
import kotlinx.coroutines.launch
import java.util.*
import java.text.SimpleDateFormat
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
    private lateinit var updateManager: UpdateManager
    private lateinit var checkUpdateButton: MaterialButton
    private lateinit var aboutButton: MaterialButton
    
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
        "Bugünün fedakarlığı, yarının zaferidir.",

        // Yeni Eklenen 50 Mesaj
        "Her gün yeni bir fırsat, her an yeni bir başlangıç.",
        "Güçlü olmak bir seçim, sen doğru seçimi yapıyorsun.",
        "Beynin seni kandırmaya çalışabilir, ama sen daha güçlüsün.",
        "Bugün zorlu olabilir, ama yarın daha güçlü olacaksın.",
        "Her 'hayır' ile karakterin güçleniyor.",
        "Gelecekteki sen, bugünkü kararların için teşekkür edecek.",
        "Bağımlılık zincirlerini kırmak zor, ama özgürlük paha biçilemez.",
        "Her başarılı insan bir yerden başladı, sen de başladın!",
        "Düşüncelerini kontrol edebilirsen, davranışlarını da kontrol edebilirsin.",
        "Kendini geliştirmek için attığın her adım değerli.",
        "Başarı bir yolculuktur, sen doğru yoldasın.",
        "Her gün biraz daha iyiye gidiyorsun, bunu hisset!",
        "Geçmiş seni tanımlamaz, bugünkü seçimlerin geleceğini belirler.",
        "Beynin yeniden şekilleniyor, her gün biraz daha güçleniyorsun.",
        "Zorluklar seni yıldırmasın, her zorluk yeni bir fırsat.",
        "Sen düşündüğünden çok daha güçlüsün, bunu unutma!",
        "Her gün yeni bir zafer, her an yeni bir başarı.",
        "Değişim içeriden başlar, sen değişimi başlattın!",
        "Kararlılığın her geçen gün artıyor, bunu hisset!",
        "Özgürlük yolunda ilerliyorsun, devam et!",
        "Her 'hayır' bir zaferdir, her zafer bir adımdır.",
        "Beynin iyileşiyor, sen güçleniyorsun.",
        "Bugünün fedakarlığı, yarının zaferidir.",
        "Her gün biraz daha özgürleşiyorsun.",
        "Kendine yatırım yapıyorsun, bu çok değerli.",
        "Bağımlılık geçici, karakter kalıcıdır.",
        "Her zorluk yeni bir fırsat, her fırsat yeni bir başlangıç.",
        "Sen seçimlerinle var oluyorsun, doğru seçimler yapıyorsun.",
        "İrade kasını güçlendiriyorsun, devam et!",
        "Beynindeki değişim başladı, sen kazanacaksın!",
        "Her gün yeni bir başarı hikayesi yazıyorsun.",
        "Özgürlük yolunda emin adımlarla ilerliyorsun.",
        "Karanlık geçici, aydınlık kalıcıdır.",
        "Her nefes yeni bir başlangıç şansı.",
        "Kendini keşfediyorsun, bu yolculuğun tadını çıkar.",
        "Bağımlılık zincirlerini kırıyorsun, özgürlüğe koş!",
        "Her an yeni bir fırsat, her fırsat yeni bir zafer.",
        "Güçlü karakterin oluşuyor, bunu hisset!",
        "Beynin yenileniyor, sen değişiyorsun.",
        "Her gün biraz daha güçlü, biraz daha özgür.",
        "Seçimlerin geleceğini şekillendiriyor.",
        "Kararlılığın takdire şayan, devam et!",
        "Özgürlük yolunda ilerliyorsun, durma!",
        "Her zorluk seni güçlendirir, unutma!",
        "Değişim senin ellerinde, sen yapabilirsin!",
        "Bağımlılık geçici, özgürlük kalıcı.",
        "Her gün yeni bir zafer kazanıyorsun.",
        "Kendine olan inancın güçleniyor.",
        "Sen kendi hikayenin kahramanısın!",
        "Özgürlüğün tadını çıkar, sen hak ediyorsun!"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefs = getSharedPreferences("NoFapPrefs", MODE_PRIVATE)
        
        // Tema modunu uygula
        val themeMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(themeMode)
        
        setContentView(R.layout.activity_main)
        
        updateManager = UpdateManager(this)
        
        // İzinleri kontrol et
        checkPermissions()
        
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
        aboutButton = findViewById(R.id.aboutButton)
        
        setupWelcomeMessage()
        updateStreak()
        setupButtons()
        updateBadges()
        updateDarkModeButton()
        setupDailyNotification()
        
        // Uygulama başlatılırken otomatik güncelleme kontrolü
        checkForUpdates(true)
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
            checkForUpdates(true)
        }
        
        aboutButton.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
    }
    
    private fun toggleDarkMode() {
        val currentMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val modes = arrayOf(
            Pair(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, "Sistemi Takip Et"),
            Pair(AppCompatDelegate.MODE_NIGHT_YES, "Koyu Mod"),
            Pair(AppCompatDelegate.MODE_NIGHT_NO, "Açık Mod")
        )
        
        val currentIndex = modes.indexOfFirst { it.first == currentMode }
        val nextIndex = (currentIndex + 1) % modes.size
        val newMode = modes[nextIndex]
        
        AppCompatDelegate.setDefaultNightMode(newMode.first)
        prefs.edit().putInt("theme_mode", newMode.first).apply()
        updateDarkModeButton()
    }
    
    private fun updateDarkModeButton() {
        val currentMode = prefs.getInt("theme_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val modeName = when (currentMode) {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> "Sistemi Takip Et"
            AppCompatDelegate.MODE_NIGHT_YES -> "Koyu Mod"
            AppCompatDelegate.MODE_NIGHT_NO -> "Açık Mod"
            else -> "Sistemi Takip Et"
        }
        
        darkModeButton.text = "Tema Modu: $modeName"
        darkModeButton.setIconResource(
            when (currentMode) {
                AppCompatDelegate.MODE_NIGHT_YES -> R.drawable.ic_light_mode
                AppCompatDelegate.MODE_NIGHT_NO -> R.drawable.ic_dark_mode
                else -> R.drawable.ic_dark_mode
            }
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
        val badgesText = StringBuilder()
        badges.forEach { (days, badge) ->
            badgesText.append("🏆 ${badge.first} ($days gün)\n${badge.second}\n\n")
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
            .setTitle("Relapse Nedeni")
            .setView(dialogView)
            .setMessage("Lütfen relapse nedenini girin. Bu bilgi gelecekte benzer durumlardan kaçınmanıza yardımcı olacak.")
            .setPositiveButton("Kaydet") { _, _ ->
                val reason = reasonInput.text.toString()
                if (reason.isBlank()) {
                    Toast.makeText(this, "Lütfen bir neden girin", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                saveRelapse(reason)
            }
            .setNegativeButton("İptal", null)
            .show()
    }
    
    private fun saveRelapse(reason: String) {
        val oldStartDate = prefs.getLong("startDate", 0)
        
        // Eski başlangıç tarihini ve nedeni sakla
        prefs.edit()
            .putLong("startDate_$oldStartDate", oldStartDate)
            .putString("lastRelapseReason_$oldStartDate", reason)
            .putString("lastRelapseReason", reason)
            .putLong("startDate", System.currentTimeMillis())
            .apply()
        
        updateStreak()
        updateBadges()
        
        Toast.makeText(this, "Vazgeçme! Her düşüş yeni bir başlangıçtır.", Toast.LENGTH_LONG).show()
    }
    
    private fun showMotivationalTip() {
        val randomTip = motivationalTips.random()
        
        MaterialAlertDialogBuilder(this)
            .setTitle("Motivasyon")
            .setMessage(randomTip)
            .setPositiveButton("Teşekkürler", null)
            .show()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
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
        
        // Her gün saat 09:00'da bildirim gönder
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            
            // Eğer belirlenen saat geçtiyse, bir sonraki güne ayarla
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }

    private fun checkForUpdates(showLoading: Boolean = false) {
        if (showLoading) {
            Toast.makeText(this, "Güncelleme kontrol ediliyor...", Toast.LENGTH_SHORT).show()
        }
        
        lifecycleScope.launch {
            try {
                val updateInfo = updateManager.checkForUpdates()
                if (updateInfo.hasUpdate) {
                    showUpdateDialog(updateInfo)
                } else if (showLoading) {
                    Toast.makeText(this@MainActivity, "Uygulama güncel!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                if (showLoading) {
                    Toast.makeText(this@MainActivity, "Güncelleme kontrolü başarısız: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    private fun showUpdateDialog(updateInfo: UpdateInfo) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Yeni Güncelleme Mevcut!")
            .setMessage("Yeni versiyon (${updateInfo.latestVersion}) mevcut. Güncellemek ister misiniz?")
            .setPositiveButton("Güncelle") { _, _ ->
                updateManager.downloadUpdate(updateInfo.downloadUrl)
                Toast.makeText(this, "Güncelleme indiriliyor...", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            // Kullanıcı bilinmeyen kaynaklardan yükleme iznini verdi
            updateManager.installUpdate()
        }
    }

    private fun checkPermissions() {
        Toast.makeText(this, "İzinler kontrol ediliyor...", Toast.LENGTH_SHORT).show()
        
        if (!hasRequiredPermissions()) {
            startActivity(Intent(this, PermissionsActivity::class.java))
            finish()
            return
        }
        
        Toast.makeText(this, "Tüm izinler verildi!", Toast.LENGTH_SHORT).show()
    }
    
    private fun hasRequiredPermissions(): Boolean {
        // Android 7 (API 24) için özel kontrol
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            return true // Android 7'de izinleri otomatik olarak verilmiş kabul et
        }

        // Bildirim izni kontrolü
        val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        
        // Bilinmeyen kaynaklardan yükleme izni kontrolü
        val hasInstallPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            packageManager.canRequestPackageInstalls()
        } else {
            true
        }
        
        return hasNotificationPermission && hasInstallPermission
    }

    override fun onResume() {
        super.onResume()
        // Her ön plana geldiğinde izinleri kontrol et
        checkPermissions()
    }
} 