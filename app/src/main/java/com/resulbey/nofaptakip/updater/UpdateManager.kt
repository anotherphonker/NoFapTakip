package com.resulbey.nofaptakip.updater

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import com.resulbey.nofaptakip.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.URL

class UpdateManager(private val activity: Activity) {
    private val downloadManager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    suspend fun checkForUpdates(): UpdateInfo = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/anotherphonker/NoFapTakip/releases/latest")
            val connection = url.openConnection()
            val response = connection.getInputStream().bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(response)
            
            val latestVersion = jsonResponse.getString("tag_name").removePrefix("v")
            val currentVersion = BuildConfig.VERSION_NAME
            val downloadUrl = jsonResponse.getJSONArray("assets")
                .getJSONObject(0)
                .getString("browser_download_url")
            
            UpdateInfo(
                hasUpdate = isNewerVersion(currentVersion, latestVersion),
                latestVersion = latestVersion,
                downloadUrl = downloadUrl
            )
        } catch (e: Exception) {
            throw Exception("Güncelleme kontrolü başarısız: ${e.message}")
        }
    }

    private fun isNewerVersion(currentVersion: String, latestVersion: String): Boolean {
        val current = currentVersion.split(".").map { it.toInt() }
        val latest = latestVersion.split(".").map { it.toInt() }
        
        for (i in 0..2) {
            if (latest[i] > current[i]) return true
            if (latest[i] < current[i]) return false
        }
        return false
    }

    fun downloadUpdate(downloadUrl: String) {
        val destination = "${activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/update.apk"
        val file = File(destination)
        if (file.exists()) file.delete()

        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("NoFap Takip Güncelleme")
            .setDescription("Güncelleme indiriliyor...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(file))

        downloadManager.enqueue(request)
    }

    fun installUpdate() {
        val file = File("${activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)}/update.apk")
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(activity, "${BuildConfig.APPLICATION_ID}.provider", file)
        } else {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        activity.startActivity(intent)
    }

    fun requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.packageManager.canRequestPackageInstalls()) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${activity.packageName}")
                )
                activity.startActivityForResult(intent, 1234)
            }
        }
    }
}

data class UpdateInfo(
    val hasUpdate: Boolean,
    val latestVersion: String,
    val downloadUrl: String
) 