package com.example.dictionary

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kotlin.reflect.KClass


class  MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this,Dictionary::class.java))
            finish()
        },2000)
    }
}
