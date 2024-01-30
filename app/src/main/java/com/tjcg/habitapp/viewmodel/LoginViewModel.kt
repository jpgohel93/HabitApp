package com.tjcg.habitapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tjcg.habitapp.LoginActivity

class LoginViewModel : ViewModel() {

    var loginUIMode = MutableLiveData(LoginActivity.LOGIN)
}