package com.example.dictionary

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val logo = findViewById<ImageView>(R.id.logo)
        val title = findViewById<TextView>(R.id.title)

        // Animation
        logo.animate()
            .alpha(1f)
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(1200)
            .setInterpolator(DecelerateInterpolator())
            .start()

        title.animate()
            .alpha(1f)
            .setDuration(2000)
            .start()

        // Move to HomeActivity
        // Coroutine delay
        lifecycleScope.launch {
            delay(2000)
            startActivity(Intent(this@MainActivity, Dictionary::class.java))
            finish()
        }
    }
}