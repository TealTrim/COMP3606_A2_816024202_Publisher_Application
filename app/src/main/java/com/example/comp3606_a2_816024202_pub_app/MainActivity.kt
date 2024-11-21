package com.example.comp3606_a2_816024202_pub_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.util.UUID

class MainActivity : AppCompatActivity()
{
    //Publisher Application: (Enter Student ID, Start and Stop Publishing)

    //Attributes:
    private var client: Mqtt5BlockingClient? = null
    private var etStudentID: EditText? = null
    private var btStartPublishing: Button? = null
    private var btStopPublishing: Button? = null

    //Methods:
    override fun onCreate(savedInstanceState: Bundle?)
    {
        //Old Stuff:
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //New Stuff:
        //Initialize UI Elements (Specifically Buttons):
        etStudentID = findViewById(R.id.editTextNumber)
        btStartPublishing = findViewById(R.id.button)
        btStopPublishing = findViewById(R.id.button2)

        //Set Up MQTT:
        client = Mqtt5Client.builder()
            .identifier(UUID.randomUUID().toString())
            .serverHost("broker.sundaebytestt.com")
            .serverPort(1883)
            .build()
            .toBlocking()

        // Set Button Click Listeners
        btStartPublishing?.setOnClickListener { startPublishing() }
        btStopPublishing?.setOnClickListener { stopPublishing() }
    }

    fun connectToBroker()
    {
        try
        {
            client?.connect()
            Toast.makeText(this,"Successfully connected to broker.", Toast.LENGTH_SHORT).show()
        } catch (e:Exception){
            Toast.makeText(this,"An error occurred when trying to connect to broker.", Toast.LENGTH_SHORT).show()
        }
    }

    fun disconnectFromBroker()
    {
        try
        {
            client?.disconnect()
            Toast.makeText(this,"Successfully disconnected from broker.", Toast.LENGTH_SHORT).show()
        } catch (e:Exception){
            Toast.makeText(this,"An error occurred when trying to disconnect from broker.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startPublishing ()
    {
    }

    private fun stopPublishing ()
    {
    }
}