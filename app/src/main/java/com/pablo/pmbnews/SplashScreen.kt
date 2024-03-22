package com.pablo.pmbnews

import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.constraintlayout.widget.ConstraintLayout
import com.pablo.pmbnews.ui.MainActivity


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.splashLayout)
        val transitionDrawable = constraintLayout.background as TransitionDrawable
        transitionDrawable.startTransition(1500)

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }
}