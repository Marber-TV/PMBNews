package com.pablo.pmbnews.ui

import android.content.Intent
import android.graphics.drawable.TransitionDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.GoogleAuthProvider
import com.pablo.pmbnews.R
import com.pablo.pmbnews.databinding.ActivityMainBinding
import com.pablo.pmbnews.firecore.AuthManager
import com.pablo.pmbnews.firecore.AuthRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private val auth = AuthManager(this)
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val constraintLayout = findViewById<ConstraintLayout>(R.id.login_layout)
        val transitionDrawable = constraintLayout.background as TransitionDrawable

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val googleSignLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) { result ->
            when (val account =
                auth.handleSignInResult(GoogleSignIn.getSignedInAccountFromIntent(result.data))) {
                is AuthRes.Success -> {
                    Log.e("account", account.toString())
                    val credential = GoogleAuthProvider.getCredential(account.data?.idToken, null)
                    GlobalScope.launch {

                        val firebaseUser = auth.googleSignInCredential(credential)
                        Log.e("firebaseUser", firebaseUser.toString())
                        when (firebaseUser) {
                            is AuthRes.Success -> {
                                val intent = Intent(this@MainActivity, Inicio::class.java)
                                startActivity(intent)
                            }

                            is AuthRes.Error -> {
                                Snackbar.make(
                                    binding.root,
                                    "Error al iniciar sesión",
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                is AuthRes.Error -> {
                    Snackbar.make(binding.root, "Error al iniciar sesión", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }



        if (auth.getCurrentUser() != null){
            val intent = Intent(this@MainActivity, Inicio::class.java)
            startActivity(intent)
        }

        with(binding){
            createAccount.setOnClickListener {
                val intent = Intent(this@MainActivity, CrearCuenta::class.java)
                startActivity(intent)
            }

            login.setOnClickListener {
                emailPassSignIn(username.text.toString(), password.text.toString(), binding)
            }

            forgotPassword.setOnClickListener {
                val intent = Intent(this@MainActivity, ResetPassword::class.java)
                startActivity(intent)
            }

            inicioGoogle.setOnClickListener {
                auth.signInWithGoogle(googleSignLauncher)
            }
        }


        lifecycleScope.launch(Dispatchers.Main) {
            val handler = Handler()
            for (i in 0..1000) {
                handler.postDelayed({
                    transitionDrawable.startTransition(3000)
                    handler.postDelayed({
                        transitionDrawable.reverseTransition(3000)
                    }, 3000)
                }, (i * 6000).toLong())
            }
        }



    }

    private fun emailPassSignIn(eMail: String, password: String, binding: ActivityMainBinding) {
        if (eMail.isNotEmpty() && password.isNotEmpty()) {
            GlobalScope.launch(Dispatchers.IO) {
                when (auth.signInWithEmailAndPassword(
                    eMail,
                    password
                )){
                    is AuthRes.Success -> {
                        val intent = Intent(this@MainActivity, Inicio::class.java)
                        startActivity(intent)
                    }
                    is AuthRes.Error -> {
                        Snackbar.make(binding.root, "Error al iniciar sesión", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }else{
            Snackbar.make(binding.root, "Por favor, rellene todos los campos", Snackbar.LENGTH_SHORT).show()
        }
    }
}
