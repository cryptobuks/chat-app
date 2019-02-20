package com.wellin5.chatapp.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.wellin5.chatapp.R
import com.wellin5.chatapp.Services.AuthService
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    fun clickLoginButton (view: View){
        val email = txtLoginEmail.text.toString()
        val password = txtLoginPassword.text.toString()

        AuthService.loginUser(this, email, password) { loginSuccess ->
            if (loginSuccess){
                AuthService.findUserByEmail(this) { findSuccess ->
                    if (findSuccess){
                        finish()
                    }
                }
            }
        }

    }

    fun clickLoginCreateUser (view: View){
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }


}
