package com.example.fitapp.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.fitapp.R
import com.example.fitapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.FirebaseApp
import androidx.work.*
import com.example.fitapp.NotificationHelper
import com.example.fitapp.ReminderWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.welcomeFragment, R.id.workoutListFragment
        ).build()

        setupActionBarWithNavController(navController, appBarConfiguration)
        NotificationHelper.createNotificationChannel(this)

        scheduleDailyReminder()
        auth = FirebaseAuth.getInstance()
        FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance("https://fitapp-9e7b1-default-rtdb.europe-west1.firebasedatabase.app").reference

        Log.d("MainActivity", "Checking authentication status")
        checkAuthentication()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.d("MainActivity", "Navigated to destination: ${destination.label}")
            if (destination.id == R.id.welcomeFragment || destination.id == R.id.workoutListFragment) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun checkAuthentication() {
        if (auth.currentUser == null) {
            Log.d("MainActivity", "User is not authenticated, navigating to WelcomeFragment")
            navController.navigate(R.id.welcomeFragment)
        } else {
            Log.d("MainActivity", "User is authenticated, navigating to WorkoutListFragment")
            navController.navigate(R.id.workoutListFragment)
        }
    }
    private fun scheduleDailyReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS) // Adjust this to schedule at a specific time
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "daily_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }
}
