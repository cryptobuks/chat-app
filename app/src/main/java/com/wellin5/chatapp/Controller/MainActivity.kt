package com.wellin5.chatapp.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.wellin5.chatapp.R
import com.wellin5.chatapp.Services.AuthService
import com.wellin5.chatapp.Services.UserDataService
import com.wellin5.chatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import kotlinx.android.synthetic.main.activity_main.*
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

    }

    fun clickSendMessage (view: View) {

    }

}
