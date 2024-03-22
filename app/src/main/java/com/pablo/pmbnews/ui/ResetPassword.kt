package com.pablo.pmbnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.pablo.pmbnews.App
import com.pablo.pmbnews.R
import com.pablo.pmbnews.databinding.ActivityResetPasswordBinding
import com.pablo.pmbnews.firecore.AuthRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ResetPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendButton.setOnClickListener {
            GlobalScope.launch {
                when ((application as App).auth.resetPassword(binding.emailEditText.text.toString())){
                    is AuthRes.Success -> {
                        Snackbar.make(binding.root, "Correo enviado correctamente", Snackbar.LENGTH_SHORT).show()
                        finish()
                    }
                    is AuthRes.Error -> {
                        Snackbar.make(binding.root, "Error al enviar el correo", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}