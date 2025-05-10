package com.android.home_automation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.home_automation.ui.theme.HomeAutomationTheme
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {

    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
            setContent {
                HomeAutomationTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        HomeScreen(db)
                    }
                }
            }
        }
}
