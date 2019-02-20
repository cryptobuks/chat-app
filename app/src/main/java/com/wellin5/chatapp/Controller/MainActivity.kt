package com.wellin5.chatapp.Controller

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.wellin5.chatapp.R
import com.wellin5.chatapp.Services.AuthService
import com.wellin5.chatapp.Services.UserDataService
import com.wellin5.chatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeReceiver, IntentFilter(
            BROADCAST_USER_DATA_CHANGE))

        hideKeyboard()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(AuthService.isLoggedIn){
                txtUserNameNavHeader.text = UserDataService.name
                txtUserEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                imgUserNavHeader.setImageResource(resourceId)
                imgUserNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                btnLoginNavHeader.text = "Logout"
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun clickLoginBtnNav (view: View) {
        if(AuthService.isLoggedIn){
            UserDataService.logout()
            txtUserNameNavHeader.text = ""
            txtUserEmailNavHeader.text = ""
            imgUserNavHeader.setImageResource(R.drawable.profiledefault)
            imgUserNavHeader.setBackgroundColor(Color.TRANSPARENT)
            btnLoginNavHeader.text = "Login"

        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

    fun clickAddChannel (view: View){
        if(AuthService.isLoggedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add", DialogInterface.OnClickListener {dialog, id ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.txtAddChannelName)
                    val descTextField = dialogView.findViewById<EditText>(R.id.txtAddChannelDescription)
                    val channelName = nameTextField.text.toString()
                    val description = descTextField.text.toString()

                    hideKeyboard()
                    // Create channel with the name and desc
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->

                    hideKeyboard()
                })
                .show()
        }
    }

    fun clickSendMessage (view: View) {

    }


    fun hideKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
