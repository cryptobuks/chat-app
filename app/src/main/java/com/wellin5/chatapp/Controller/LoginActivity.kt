package com.wellin5.chatapp.Controller

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.wellin5.chatapp.R
import com.wellin5.chatapp.Services.AuthService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        spinnerLogin.visibility = View.INVISIBLE
    }

    fun clickLoginButton (view: View){
        enableSpinner(true)
        val email = txtLoginEmail.text.toString()
        val password = txtLoginPassword.text.toString()
        hideKeyboard()

        if (email.isNotEmpty() && password.isNotEmpty()){

            AuthService.loginUser(email, password) { loginSuccess ->
                if (loginSuccess){
                    AuthService.findUserByEmail(this) { findSuccess ->
                        if (findSuccess){
                            enableSpinner(false)
                            finish()
                        } else {
                            errorToast()
                        }
                    }
                } else {
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Please fill fields", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }

    }

    fun clickLoginCreateUser (view: View){
        val createUserIntent = Intent(this, CreateUserActivity::class.java)
        startActivity(createUserIntent)
        finish()
    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            spinnerLogin.visibility = View.VISIBLE
        } else {
            spinnerLogin.visibility = View.INVISIBLE
        }
        btnLogin.isEnabled = !enable
        btnLoginCreateUser.isEnabled = !enable
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
