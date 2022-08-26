package com.tjcg.habitapp

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.tjcg.habitapp.data.Constant
import com.tjcg.habitapp.databinding.ActivityLogin2Binding
import com.tjcg.habitapp.remote.ApiService
import com.tjcg.habitapp.remote.RegisterResponse
import com.tjcg.habitapp.viewmodel.LoginViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogin2Binding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = getSharedPreferences(Constant.PREFS_APP, MODE_PRIVATE)
        Constant.authorizationToken = sharedPreferences.getString(
            Constant.PREFS_AUTHORIZATION, "") ?: ""
        if (Constant.authorizationToken.isNotBlank()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        val loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        val deviceID: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("deviceToken", deviceID)
        loginViewModel.loginUIMode.observe(this) { mode ->
            when(mode) {
                LOGIN -> {
                    binding.loginTitle.text = "Login"
                    binding.nameText.visibility = View.GONE
                    binding.nameEditText.visibility = View.GONE
                    binding.reenterText.visibility = View.GONE
                    binding.reenterEditText.visibility = View.GONE
                    binding.loginBtn.visibility = View.VISIBLE
                    binding.nameEditText.setText("")
                    binding.passwordEditText.setText("")
                    binding.reenterText.setText("")
                    binding.registerBtn.visibility = View.VISIBLE
                    binding.errorText.visibility = View.GONE
                    binding.loginBtn.setOnClickListener {
                        if (binding.emailEditText.text.isNullOrBlank() ||
                                binding.passwordEditText.text.isNullOrBlank()) {
                            Toast.makeText(this, "Please provide all information", Toast.LENGTH_LONG).show()
                            return@setOnClickListener
                        }
                        binding.progressBar.visibility = View.VISIBLE
                        val email = binding.emailEditText.text.toString()
                        val password = binding.passwordEditText.text.toString()
                        ApiService.apiService?.loginUser(email, password)?.enqueue(
                            object : Callback<RegisterResponse> {
                                override fun onResponse(
                                    call: Call<RegisterResponse>,
                                    response: Response<RegisterResponse>
                                ) {
                                    if (response.isSuccessful && response.body()?.status == true) {
                                        binding.loginTitle.text = "Login Successful"
                                        Log.d("Register", "${response.body()?.message}")
                                        Constant.authorizationToken = response.body()?.token ?: "na"
                                        sharedPreferences.edit().putString(
                                            Constant.PREFS_AUTHORIZATION, Constant.authorizationToken).apply()
                                        Log.d("AuthToken", Constant.authorizationToken)
                                        Handler(mainLooper).postDelayed({
                                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                            finish()
                                        }, 3000)
                                    } else {
                                        binding.progressBar.visibility = View.GONE
                                        binding.errorText.visibility = View.VISIBLE
                                        binding.errorText.text = "Error: ${response.body()?.message}"
                                    }
                                }

                                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                                    binding.progressBar.visibility = View.GONE
                                    binding.errorText.visibility = View.VISIBLE
                                    binding.errorText.text = "Error: ${t.message}"
                                }

                            }
                        )
                    }
                    binding.registerBtn.setOnClickListener {
                        loginViewModel.loginUIMode.value = REGISTER
                    }
                }
                REGISTER -> {
                    binding.loginTitle.text = "Register"
                    binding.nameText.visibility = View.VISIBLE
                    binding.nameEditText.visibility = View.VISIBLE
                    binding.nameEditText.setText("")
                    binding.passwordEditText.setText("")
                    binding.reenterText.visibility = View.VISIBLE
                    binding.reenterEditText.visibility = View.VISIBLE
                    binding.loginBtn.visibility = View.GONE
                    binding.registerBtn.visibility = View.VISIBLE
                    binding.errorText.visibility = View.GONE
                    binding.registerBtn.setOnClickListener {
                        if (binding.nameEditText.text.isNullOrBlank() ||
                                binding.emailEditText.text.isNullOrBlank()) {
                            Toast.makeText(this, "Please provide all details", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        val pass1 = binding.passwordEditText.text.toString()
                        val pass2 = binding.reenterEditText.text.toString()
                        if (pass1 != pass2) {
                            binding.reenterEditText.error = "Not Matched!"
                            return@setOnClickListener
                        }
                        val name = binding.nameEditText.text.toString()
                        val email = binding.emailEditText.text.toString()
                        binding.progressBar.visibility = View.VISIBLE
                        ApiService.apiService?.registerUser(name, email, pass1, deviceID)?.enqueue(
                            object : Callback<RegisterResponse> {
                                override fun onResponse(
                                    call: Call<RegisterResponse>,
                                    response: Response<RegisterResponse>
                                ) {
                                    binding.progressBar.visibility = View.GONE
                                    if (response.isSuccessful && response.body()?.status == true) {
                                        binding.loginTitle.text = "Successfully Registered"
                                        Log.d("Register", "${response.body()?.message}")
                                        Constant.authorizationToken = response.body()?.token ?: "na"
                                        sharedPreferences.edit().putString(
                                            Constant.PREFS_AUTHORIZATION, Constant.authorizationToken).apply()
                                        Log.d("AuthToken", Constant.authorizationToken)
                                        Handler(mainLooper).postDelayed({
                                            loginViewModel.loginUIMode.value = LOGIN
                                        }, 3000)
                                    } else {
                                        binding.errorText.visibility = View.VISIBLE
                                        binding.errorText.text = "Error: ${response.body()?.message}"
                                    }
                                }

                                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                                    binding.progressBar.visibility = View.GONE
                                    binding.errorText.visibility = View.VISIBLE
                                    binding.errorText.text = "Error: ${t.message}"
                                }

                            }
                        )
                    }
                }
            }
        }
     /*   Constant.authorizationToken = sharedPreferences.getString(
            Constant.PREFS_AUTHORIZATION, "") ?: ""
        if (Constant.authorizationToken.isNotBlank()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }  */
    /*    val deviceID: String = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        Log.d("deviceToken", "$deviceID")
        changeUI()

        binding.nextButton.setOnClickListener {
            if (binding.loginAnswer.text.toString().isBlank()) {
                binding.loginAnswer.error = "Provide this information"
                return@setOnClickListener
            }
            when(mode) {
                REGISTER_1 -> {
                    userName = binding.loginAnswer.text.toString()
                    mode = REGISTER_2
                    changeUI()
                }
                REGISTER_2 -> {
                    userAge = binding.loginAnswer.text.toString()
                    mode = PROGRESS
                    changeUI()
                    ApiService.apiService?.registerUser(userName, userAge.toInt(),
                        deviceID, deviceID)?.enqueue(object : Callback<RegisterResponse> {
                        override fun onResponse(
                            call: Call<RegisterResponse>,
                            response: Response<RegisterResponse>
                        ) {
                            if (response.isSuccessful && response.body()?.status == true) {
                                mode = FINISH
                                changeUI()
                                Log.d("Register", "${response.body()?.message}")
                                Constant.authorizationToken = response.body()?.token ?: "na"
                                sharedPreferences.edit().putString(
                                    Constant.PREFS_AUTHORIZATION, Constant.authorizationToken).apply()
                                Log.d("AuthToken", Constant.authorizationToken)
                            } else {
                                mode = ERROR
                                changeUI()
                                Log.e("Register", "${response.body()?.message}")
                            }

                        }

                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            mode = ERROR
                            changeUI()
                            Log.e("Register", "${t.message}")
                        }

                    })
                }
                ERROR -> {
                    mode = REGISTER_1
                    changeUI()
                }
            }
        }  */

    }
 /*   private fun changeUI() {
        binding.questionLayout.visibility = View.VISIBLE
        binding.updateText.visibility = View.GONE
        when(mode) {
            REGISTER_1 -> {
                binding.nextButton.setImageResource(R.drawable.arrow)
                binding.progressBar.visibility = View.GONE
                binding.loginQuestion.text = "Your Name"
                binding.loginAnswer.setText("")
            }
            REGISTER_2 -> {
                binding.progressBar.visibility = View.GONE
                binding.loginQuestion.text = "Your Age"
                binding.loginAnswer.setText("")
            }
            PROGRESS -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.questionLayout.visibility = View.INVISIBLE
                binding.updateText.visibility = View.VISIBLE
                binding.updateText.text = "In Progress..."
            }
            ERROR -> {
                binding.progressBar.visibility = View.GONE
                binding.questionLayout.visibility = View.INVISIBLE
                binding.updateText.visibility = View.VISIBLE
                binding.updateText.text = "An error Occurred"
                binding.nextButton.setImageResource(R.drawable.back_button2)
            }
            FINISH -> {
                binding.progressBar.visibility = View.GONE
                binding.questionLayout.visibility = View.INVISIBLE
                binding.updateText.visibility = View.VISIBLE
                binding.updateText.text = "Successfully Registered"
                Handler(mainLooper).postDelayed( {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 5000)
            }
        }
    }  */

    companion object {
        const val REGISTER = 0
        const val LOGIN = 1
    }
}