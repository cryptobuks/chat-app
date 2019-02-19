package com.wellin5.chatapp.Controller

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.wellin5.chatapp.R
import com.wellin5.chatapp.Services.AuthService
import com.wellin5.chatapp.Services.UserDataService
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class CreateUserActivity : AppCompatActivity() {

    var userAvatar = "profileDefault"
    var avatarColor = "[0.5,0.5,0.5,1]"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)
        spinnerCreate.visibility = View.INVISIBLE
    }

    fun generateUserAvatar(view: View) {
        val random = Random()
        val color = random.nextInt(2)
        val avatar = random.nextInt(28)


        if (color == 0) {
            userAvatar = "light$avatar"
        } else {
            userAvatar = "dark$avatar"
        }

        val resourceId = resources.getIdentifier(userAvatar, "drawable", packageName)
        imgCreateAvatar.setImageResource(resourceId)
    }

    fun clickGenerateColor(view: View) {

        val random = Random()
        val r = random.nextInt(255)
        val g = random.nextInt(255)
        val b = random.nextInt(255)

        imgCreateAvatar.setBackgroundColor(Color.rgb(r, g, b))

        val savedR = r.toDouble() / 255
        val savedG = g.toDouble() / 255
        val savedB = g.toDouble() / 255

        avatarColor = "[$savedR, $savedG, $savedB, 1]"
        println(avatarColor)
    }

    fun clickCreateUser(view: View) {
        enableSpinner(true)

        val username = txtCreateUsername.text.toString()
        val email = txtCreateEmail.text.toString()
        val password = txtCreatePassword.text.toString()


        if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {

            AuthService.registerUser(this, email, password) { registerSuccess ->
                if (registerSuccess) {
                    AuthService.loginUser(this, email, password) { loginSuccess ->
                        if (loginSuccess) {
                            AuthService.createUser(this, username, email, userAvatar, avatarColor) { createSuccess ->
                                if (createSuccess) {
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
                    errorToast()
                }
            }
        } else {
            Toast.makeText(this, "Fields are empty", Toast.LENGTH_LONG).show()
            enableSpinner(false)
        }

    }

    fun errorToast() {
        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
        enableSpinner(false)
    }

    fun enableSpinner(enable: Boolean) {
        if (enable) {
            spinnerCreate.visibility = View.VISIBLE
        } else {
            spinnerCreate.visibility = View.INVISIBLE
        }
        btnBackgroundColor.isEnabled = !enable
        btnCreateUser.isEnabled = !enable
        imgCreateAvatar.isEnabled = !enable
    }

}