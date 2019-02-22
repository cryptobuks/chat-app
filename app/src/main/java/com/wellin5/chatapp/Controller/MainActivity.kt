package com.wellin5.chatapp.Controller

import android.app.Activity
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
import android.widget.ArrayAdapter
import android.widget.EditText
import com.wellin5.chatapp.Model.Channel
import com.wellin5.chatapp.Model.Message
import com.wellin5.chatapp.R
import com.wellin5.chatapp.Services.AuthService
import com.wellin5.chatapp.Services.MessageService
import com.wellin5.chatapp.Services.UserDataService
import com.wellin5.chatapp.Utilities.BROADCAST_USER_DATA_CHANGE
import com.wellin5.chatapp.Utilities.SOCKET_URL
import io.socket.client.IO
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_channel_dialog.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : AppCompatActivity() {

    val socket = IO.socket(SOCKET_URL)
    lateinit var channelAdapter: ArrayAdapter<Channel>
    var selectedChannel : Channel? = null

    private fun setupAdapters() {
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, MessageService.channels)
        channel_list.adapter = channelAdapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        socket.connect()
        socket.on("channelCreated", onChannelCreated)
        socket.on("messageCreated", onNewMessage)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        setupAdapters()

        channel_list.setOnItemClickListener { _, _, position, _->
            selectedChannel = MessageService.channels[position]
            drawer_layout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        if (App.prefs.isLoggedIn){
            AuthService.findUserByEmail(this){}
        }
    }

    override fun onResume() {

        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            userDataChangeReceiver, IntentFilter(
                BROADCAST_USER_DATA_CHANGE
            )
        )

    }

    override fun onPause(){
        super.onPause()
    }


    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(userDataChangeReceiver)
        super.onDestroy()
    }

    private val userDataChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (App.prefs.isLoggedIn) {
                txtUserNameNavHeader.text = UserDataService.name
                txtUserEmailNavHeader.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.avatarName, "drawable", packageName)
                imgUserNavHeader.setImageResource(resourceId)
                imgUserNavHeader.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.avatarColor))
                btnLoginNavHeader.text = "Logout"

                MessageService.getChannels {complete ->
                    if (complete){
                        if (MessageService.channels.count()>0){
                            selectedChannel = MessageService.channels[0]
                            channelAdapter.notifyDataSetChanged()
                            updateWithChannel()
                        }
                    }
                }
            }
        }
    }

    fun updateWithChannel() {
        lblMainChannelName.text = "#${selectedChannel?.name}"
        //download messages for channel
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun clickLoginBtnNav(view: View) {
        if (App.prefs.isLoggedIn) {
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

    fun clickAddChannel(view: View) {
        if (App.prefs.isLoggedIn) {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)

            builder.setView(dialogView)
                .setPositiveButton("Add", DialogInterface.OnClickListener { _, _ ->
                    val nameTextField = dialogView.findViewById<EditText>(R.id.txtAddChannelName)
                    val descTextField = dialogView.findViewById<EditText>(R.id.txtAddChannelDescription)
                    val channelName = nameTextField.text.toString()
                    val channelDescription = descTextField.text.toString()

                    // Create channel with the name and desc
                    socket.emit("newChannel", channelName, channelDescription)
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _->

                })
                .show()
        }
    }

    private val onChannelCreated = Emitter.Listener { args ->
        runOnUiThread {
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String

            val newChannel = Channel(channelName, channelDescription, channelId)

            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }

    private val onNewMessage = Emitter.Listener { args ->
        runOnUiThread {
            val msgBody = args[0] as String
            val channelId = args[2] as String
            val userName = args[3] as String
            val userAvatar = args[4] as String
            val userAvatarColor = args[5] as String
            val id = args[6] as String
            val timestamp = args[6] as String


            val newMessage = Message(msgBody, userName, channelId, userAvatar, userAvatarColor, id, timestamp)
            MessageService.messages.add(newMessage)
            println(newMessage.message)
//            channelAdapter.notifyDataSetChanged()
        }
    }
    fun clickSendMessage(view: View) {
        if (App.prefs.isLoggedIn && txtMessage.text.isNotEmpty() && selectedChannel != null) {
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id // we are sure that the selected channel is not null

            socket.emit("newMessage", txtMessage.text.toString(), userId, channelId,
                UserDataService.name, UserDataService.avatarName, UserDataService.avatarColor)
            txtMessage.text.clear()
            hideKeyboard()
        }
    }


    private fun hideKeyboard() {
        val inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }

}
