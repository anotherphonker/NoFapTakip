package com.resulbey.nofaptakip

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

class OnboardingActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var startButton: Button
    private lateinit var dateButton: Button
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Kullanıcı zaten giriş yapmışsa ana ekrana yönlendir
        if (getSharedPreferences("NoFapPrefs", MODE_PRIVATE).contains("user_name")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        nameInput = findViewById(R.id.nameInput)
        startButton = findViewById(R.id.startButton)
        dateButton = findViewById(R.id.dateButton)

        setupDatePicker()
        setupStartButton()
        setupNameInput()
    }

    private fun setupDatePicker() {
        dateButton.setOnClickListener {
            val today = MaterialDatePicker.todayInUtcMilliseconds()
            val constraints = CalendarConstraints.Builder()
                .setStart(today - (365L * 24 * 60 * 60 * 1000)) // En fazla 1 yıl öncesi
                .setEnd(today)
                .setValidator(object : CalendarConstraints.DateValidator {
                    override fun isValid(date: Long): Boolean {
                        return date <= today
                    }

                    override fun writeToParcel(dest: android.os.Parcel, flags: Int) {}
                    override fun describeContents(): Int = 0
                })
                .build()

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Başlangıç Tarihini Seçin")
                .setSelection(today)
                .setCalendarConstraints(constraints)
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                if (selection > today) {
                    Toast.makeText(this, "İleri tarih seçilemez!", Toast.LENGTH_SHORT).show()
                    return@addOnPositiveButtonClickListener
                }
                selectedDate.timeInMillis = selection
                dateButton.text = "Seçilen Tarih: ${android.text.format.DateFormat.format("dd/MM/yyyy", selectedDate)}"
                validateForm()
            }

            datePicker.show(supportFragmentManager, "date_picker")
        }
    }
    
    private fun setupStartButton() {
        startButton.isEnabled = false
        
        startButton.setOnClickListener {
            saveAndContinue()
        }
    }
    
    private fun setupNameInput() {
        nameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateForm()
            }
        })
    }
    
    private fun validateForm() {
        val name = nameInput.text.toString()
        startButton.isEnabled = name.isNotBlank() && selectedDate.timeInMillis > 0
    }
    
    private fun saveAndContinue() {
        val name = nameInput.text.toString()
        if (name.isBlank()) {
            nameInput.error = "Lütfen adınızı girin"
            return
        }

        if (selectedDate.timeInMillis == 0L) {
            Toast.makeText(this, "Lütfen bir başlangıç tarihi seçin", Toast.LENGTH_SHORT).show()
            return
        }

        // Kullanıcı adını ve başlangıç tarihini kaydet
        getSharedPreferences("NoFapPrefs", MODE_PRIVATE).edit()
            .putString("user_name", name)
            .putLong("startDate", selectedDate.timeInMillis)
            .apply()

        // İzinler ekranına yönlendir
        startActivity(Intent(this, PermissionsActivity::class.java))
        finish()
    }
} 