package com.resulbey.nofaptakip

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val appVersionText = findViewById<TextView>(R.id.appVersionText)
        val developerText = findViewById<TextView>(R.id.developerText)
        val purposeText = findViewById<TextView>(R.id.purposeText)
        val androidVersionText = findViewById<TextView>(R.id.androidVersionText)
        val sourceCodeButton = findViewById<Button>(R.id.sourceCodeButton)

        appVersionText.text = "Versiyon: ${BuildConfig.VERSION_NAME}"
        developerText.text = "Geliştirici: Resul Çelik"
        purposeText.text = "NoFapTakip, bağımlılıkla mücadele eden kullanıcılara yardımcı olmak için tasarlanmış bir uygulamadır. " +
            "Kullanıcıların ilerlemesini takip eder, motivasyon sağlar ve başarılarını rozetlerle ödüllendirir. " +
            "Günlük bildirimler ve acil durum desteği ile kullanıcıların yolculuğunda yanında olmayı amaçlar."
        androidVersionText.text = "Desteklenen Android Sürümleri: Android 7.0 (API 24) ve üzeri"

        sourceCodeButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/anotherphonker/NoFapTakip"))
            startActivity(intent)
        }
    }
} 