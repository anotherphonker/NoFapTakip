package com.example.nofaptakip

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.*

class OnboardingActivity : AppCompatActivity() {
    private lateinit var nameInput: TextInputEditText
    private lateinit var datePickerButton: MaterialButton
    private lateinit var selectedDateText: TextView
    private lateinit var startButton: MaterialButton
    private var selectedDate: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Eğer kullanıcı zaten kayıtlıysa ana ekrana yönlendir
        val prefs = getSharedPreferences("NoFapPrefs", MODE_PRIVATE)
        if (prefs.contains("user_name") && prefs.contains("startDate")) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_onboarding)
        
        nameInput = findViewById(R.id.nameInput)
        datePickerButton = findViewById(R.id.datePickerButton)
        selectedDateText = findViewById(R.id.selectedDateText)
        startButton = findViewById(R.id.startButton)
        
        setupDatePicker()
        setupStartButton()
        setupNameInput()
    }
    
    private fun setupDatePicker() {
        datePickerButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val selected = Calendar.getInstance().apply {
                        set(year, month, day, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    
                    if (selected.timeInMillis > System.currentTimeMillis()) {
                        selectedDateText.text = "İleri tarihli bir gün seçemezsiniz!"
                        selectedDate = 0
                        startButton.isEnabled = false
                        return@DatePickerDialog
                    }
                    
                    selectedDate = selected.timeInMillis
                    selectedDateText.text = SimpleDateFormat("dd MMMM yyyy", Locale("tr")).format(Date(selectedDate))
                    validateForm()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.maxDate = System.currentTimeMillis()
            }.show()
        }
    }
    
    private fun setupStartButton() {
        startButton.isEnabled = false
        
        startButton.setOnClickListener {
            val name = nameInput.text.toString()
            
            getSharedPreferences("NoFapPrefs", MODE_PRIVATE).edit()
                .putString("user_name", name)
                .putLong("startDate", selectedDate)
                .apply()
            
            startActivity(Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
            finish()
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
        startButton.isEnabled = name.isNotBlank() && selectedDate != 0L
    }
} 