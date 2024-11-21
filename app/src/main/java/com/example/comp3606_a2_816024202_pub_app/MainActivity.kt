/*
-------------------------------------------
Student Information:
-------------------------------------------
Student Name: Teal Trim
Student ID: 816024202
Course: Wireless and Mobile Computing (COMP3606)
Assignment: COMP3606 Assignment 2
Date: 20/11/2024
-------------------------------------------
*/

package com.example.comp3606_a2_816024202_pub_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import java.util.UUID

class MainActivity : AppCompatActivity()
{
    //Publisher Application: (Enter Student ID, Start and Stop Publishing)

    //Attributes:
    //MQTT Attribute:
    private var client: Mqtt5BlockingClient? = null
    //UI Attributes:
    private var etStudentID: EditText? = null
    private var btStartPublishing: Button? = null
    private var btStopPublishing: Button? = null
    //Location Attributes:
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

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

        // Initialize location services:
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

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
        // Connect to the MQTT broker:
        connectToBroker()

        // Get the typed in Student ID, check for empty field:
        val studentID = etStudentID?.text.toString()
        if (studentID.isBlank()) {
            Toast.makeText(this, "Please enter your Student ID.", Toast.LENGTH_SHORT).show()
            return
        }

        //Location Stuff:
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Update every 5 seconds.
            fastestInterval = 2000 // Minimum time of 2 seconds between updates.
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Request high accuracy GPS.
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val speed = location.speed
                    publishLocationMQTT(studentID, latitude, longitude, speed)
                }
            }
        }
    }

    private fun stopPublishing ()
    {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        // Disconnect from the MQTT broker:
        disconnectFromBroker()
    }

    private fun publishLocationMQTT(studentID: String, latitude: Double, longitude: Double, speed: Float) {
        val message = "StudentID: $studentID, Latitude: $latitude, Longitude: $longitude, Speed: $speed"
        try {
            client?.publishWith()
                ?.topic("assignment/location")
                ?.payload(message.toByteArray())
                ?.send()
            Toast.makeText(this, "Published: $message", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error publishing location: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
