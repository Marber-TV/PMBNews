package com.pablo.pmbnews.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.pablo.pmbnews.App
import com.pablo.pmbnews.databinding.ActivityCrearCuentaBinding
import com.pablo.pmbnews.databinding.ActivityCrearCuentaBinding.*
import com.pablo.pmbnews.firecore.AuthRes
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class CrearCuenta : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            registerButton.setOnClickListener {
                signUp(emailEditText.text.toString(), passwordEditText.text.toString())
            }
            loginTextView.setOnClickListener {
                finish()
            }
        }
    }

    private fun ActivityCrearCuentaBinding.signUp(eMail: String, password: String) {
        if (eMail.isNotEmpty() && password.isNotEmpty()) {
            GlobalScope.launch {
                when ((application as App).auth.createUserWithEmailAndPassword(
                    eMail,
                    password
                )){
                    is AuthRes.Success -> {
                        Snackbar.make(root, "Usuario creado correctamente", Snackbar.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                    is AuthRes.Error -> {
                        Snackbar.make(root, "Error al crear el usuario", Snackbar.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
        else{
            Snackbar.make(root, "Debes llenar todos los campos", Snackbar.LENGTH_SHORT).show()
        }
    }
}