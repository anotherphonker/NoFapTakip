package com.resulbey.nofaptakip

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionsActivity : AppCompatActivity() {
    private lateinit var notificationPermissionButton: Button
    private lateinit var installPermissionButton: Button
    private lateinit var continueButton: Button
    private lateinit var notificationInfoText: TextView
    private lateinit var installInfoText: TextView
    private lateinit var versionInfoText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        notificationPermissionButton = findViewById(R.id.notificationPermissionButton)
        installPermissionButton = findViewById(R.id.installPermissionButton)
        continueButton = findViewById(R.id.continueButton)
        notificationInfoText = findViewById(R.id.notificationInfoText)
        installInfoText = findViewById(R.id.installInfoText)
        versionInfoText = findViewById(R.id.versionInfoText)

        setupVersionInfo()
        setupPermissionButtons()
        updatePermissionStates()
    }

    private fun setupVersionInfo() {
        when (Build.VERSION.SDK_INT) {
            35 -> { // Android 15 (API 35)
                versionInfoText.text = "Android 15 kullanıyorsunuz. Bildirim izni için onay vermeniz gerekiyor. Bilinmeyen kaynaklardan yükleme izni için de ayarlardan izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> { // Android 14 (API 34)
                versionInfoText.text = "Android 14 kullanıyorsunuz. Bildirim izni için onay vermeniz gerekiyor. Bilinmeyen kaynaklardan yükleme izni için de ayarlardan izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.TIRAMISU -> { // Android 13 (API 33)
                versionInfoText.text = "Android 13 kullanıyorsunuz. Bildirim izni için onay vermeniz gerekiyor. Bilinmeyen kaynaklardan yükleme izni için de ayarlardan izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.S_V2 -> { // Android 12L (API 32)
                versionInfoText.text = "Android 12L kullanıyorsunuz. Bildirim izni varsayılan olarak açık. Bilinmeyen kaynaklardan yükleme izni için ayarlardan izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.S -> { // Android 12 (API 31)
                versionInfoText.text = "Android 12 kullanıyorsunuz. Bildirim izni varsayılan olarak açık. Bilinmeyen kaynaklardan yükleme izni için ayarlardan izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.R -> { // Android 11 (API 30)
                versionInfoText.text = "Android 11 kullanıyorsunuz. Bildirim izni varsayılan olarak açık. Bilinmeyen kaynaklardan yükleme izni için ayarlardan izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.Q -> { // Android 10 (API 29)
                versionInfoText.text = "Android 10 kullanıyorsunuz. Bildirim izni varsayılan olarak açık. Bilinmeyen kaynaklardan yükleme izni için ayarlardan izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.P -> { // Android 9 (API 28)
                versionInfoText.text = "Android 9 kullanıyorsunuz. Bildirim izni varsayılan olarak açık. Bilinmeyen kaynaklardan yükleme izni için ayarlardan izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.O_MR1 -> { // Android 8.1 (API 27)
                versionInfoText.text = "Android 8.1 kullanıyorsunuz. Bildirim izni varsayılan olarak açık. Bilinmeyen kaynaklardan yükleme izni için güvenlik ayarlarından izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.O -> { // Android 8.0 (API 26)
                versionInfoText.text = "Android 8.0 kullanıyorsunuz. Bildirim izni varsayılan olarak açık. Bilinmeyen kaynaklardan yükleme izni için güvenlik ayarlarından izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.N_MR1 -> { // Android 7.1 (API 25)
                versionInfoText.text = "Android 7.1 kullanıyorsunuz. Bildirim izni varsayılan olarak açık. Bilinmeyen kaynaklardan yükleme izni için güvenlik ayarlarından izin vermeniz gerekiyor."
            }
            Build.VERSION_CODES.N -> { // Android 7.0 (API 24)
                versionInfoText.text = "Android 7.0 kullanıyorsunuz. Bildirim izni varsayılan olarak açık. Bilinmeyen kaynaklardan yükleme izni için güvenlik ayarlarından izin vermeniz gerekiyor."
            }
            else -> { // Desteklenmeyen sürüm
                versionInfoText.text = "Android ${Build.VERSION.RELEASE} kullanıyorsunuz. Bu sürüm desteklenmiyor. Lütfen Android 7.0 veya üzeri bir sürüm kullanın."
            }
        }
    }

    private fun setupPermissionButtons() {
        // Bildirim izni butonu ve açıklaması
        when (Build.VERSION.SDK_INT) {
            35, // Android 15
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE, // Android 14
            Build.VERSION_CODES.TIRAMISU -> { // Android 13
                notificationPermissionButton.visibility = View.VISIBLE
                notificationInfoText.text = "Bildirim izni: Günlük motivasyon mesajları ve rozet kazanma bildirimleri için gereklidir."
                notificationPermissionButton.setOnClickListener {
                    requestNotificationPermission()
                }
            }
            Build.VERSION_CODES.S_V2, // Android 12L
            Build.VERSION_CODES.S, // Android 12
            Build.VERSION_CODES.R, // Android 11
            Build.VERSION_CODES.Q, // Android 10
            Build.VERSION_CODES.P, // Android 9
            Build.VERSION_CODES.O_MR1, // Android 8.1
            Build.VERSION_CODES.O -> { // Android 8.0
                notificationPermissionButton.visibility = View.GONE
                notificationInfoText.text = "✅ Bildirim izni varsayılan olarak açık. Bu izin sayesinde günlük motivasyon mesajları ve rozet kazanma bildirimlerini alabileceksiniz."
            }
            else -> { // Android 7.x ve altı
                notificationPermissionButton.visibility = View.GONE
                notificationInfoText.text = "✅ Bildirim izni varsayılan olarak açık. Bu izin sayesinde günlük motivasyon mesajları ve rozet kazanma bildirimlerini alabileceksiniz."
            }
        }

        // Bilinmeyen kaynaklardan yükleme izni butonu ve açıklaması
        when (Build.VERSION.SDK_INT) {
            35, // Android 15
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE, // Android 14
            Build.VERSION_CODES.TIRAMISU, // Android 13
            Build.VERSION_CODES.S_V2, // Android 12L
            Build.VERSION_CODES.S, // Android 12
            Build.VERSION_CODES.R, // Android 11
            Build.VERSION_CODES.Q, // Android 10
            Build.VERSION_CODES.P, // Android 9
            Build.VERSION_CODES.O_MR1, // Android 8.1
            Build.VERSION_CODES.O -> { // Android 8.0
                installPermissionButton.visibility = View.VISIBLE
                installInfoText.text = "Bilinmeyen kaynaklardan yükleme izni: Uygulama güncellemelerini doğrudan indirebilmek için gereklidir. Bu izin sayesinde yeni özellikleri ve iyileştirmeleri hemen kullanabilirsiniz."
                installPermissionButton.setOnClickListener {
                    requestInstallPermission()
                }
            }
            Build.VERSION_CODES.N_MR1, // Android 7.1
            Build.VERSION_CODES.N -> { // Android 7.0
                installPermissionButton.visibility = View.VISIBLE
                installInfoText.text = "Bilinmeyen kaynaklardan yükleme izni: Uygulama güncellemelerini doğrudan indirebilmek için gereklidir. Güvenlik ayarlarından bu izni vermeniz gerekiyor."
                installPermissionButton.setOnClickListener {
                    // Android 7.x için güvenlik ayarlarına yönlendir
                    startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
                }
            }
            else -> { // Desteklenmeyen sürüm
                installPermissionButton.visibility = View.GONE
                installInfoText.text = "Bu Android sürümü desteklenmiyor."
            }
        }

        // Devam butonu
        continueButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startActivity(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:$packageName")
            })
        }
    }

    private fun updatePermissionStates() {
        // Bildirim izni durumu
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotificationPermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            notificationPermissionButton.text = if (hasNotificationPermission) {
                "✅ Bildirim İzni Verildi"
            } else {
                "Bildirim İzni Ver"
            }
            notificationPermissionButton.isEnabled = !hasNotificationPermission
        }

        // Bilinmeyen kaynaklardan yükleme izni durumu
        when (Build.VERSION.SDK_INT) {
            Build.VERSION_CODES.O, // Android 8.0
            Build.VERSION_CODES.O_MR1, // Android 8.1
            Build.VERSION_CODES.P, // Android 9
            Build.VERSION_CODES.Q, // Android 10
            Build.VERSION_CODES.R, // Android 11
            Build.VERSION_CODES.S, // Android 12
            Build.VERSION_CODES.S_V2, // Android 12L
            Build.VERSION_CODES.TIRAMISU, // Android 13
            Build.VERSION_CODES.UPSIDE_DOWN_CAKE, // Android 14
            35 -> { // Android 15
                val hasInstallPermission = packageManager.canRequestPackageInstalls()
                installPermissionButton.text = if (hasInstallPermission) {
                    "✅ Yükleme İzni Verildi"
                } else {
                    "Yükleme İzni Ver"
                }
                installPermissionButton.isEnabled = !hasInstallPermission
            }
            Build.VERSION_CODES.N_MR1, // Android 7.1
            Build.VERSION_CODES.N -> { // Android 7.0
                val hasInstallPermission = Settings.Secure.getInt(contentResolver, Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 1
                installPermissionButton.text = if (hasInstallPermission) {
                    "✅ Yükleme İzni Verildi"
                } else {
                    "Yükleme İzni Ver"
                }
                installPermissionButton.isEnabled = !hasInstallPermission
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            updatePermissionStates()
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionStates()
    }

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    }
} 