package com.android.home_automation

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.*

@Composable
fun HomeScreen(db: DatabaseReference) {
    val context = LocalContext.current

    var gasValue by remember { mutableStateOf(0) }
    var temperature by remember { mutableStateOf(0f) }
    var peopleCount by remember { mutableStateOf(0) }

    var ledOn by remember { mutableStateOf(false) }
    var doorOpen by remember { mutableStateOf(false) }
    var fanOn by remember { mutableStateOf(false) }

    var alarmTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        db.child("gasValue").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gasValue = snapshot.getValue(Int::class.java) ?: 0

                if (gasValue > 950 && !alarmTriggered) {
                    alarmTriggered = true
                    playSiren(context)
                }

                if (gasValue <= 950) {
                    alarmTriggered = false
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        db.child("temperature").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                temperature = snapshot.getValue(Float::class.java) ?: 0f
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        db.child("peopleCount").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                peopleCount = snapshot.getValue(Int::class.java) ?: 0
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(
        Modifier
            .padding(16.dp)
            .padding(top = 30.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1))
        ) {
            Image(
                painter = painterResource(id = R.drawable.homeautomation),
                contentDescription = "Home Status",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("🌡️ Temperature: $temperature°C", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("🔥 Gas Level: $gasValue", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("👥 People in House: $peopleCount", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Divider(Modifier.padding(vertical = 8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(label = "💡 LED", state = ledOn,backgroundColor = Color(0xFFFFCDD2)) {
                    ledOn = !ledOn
                    db.child("ledOn").setValue(ledOn)
                }

                ActionCard(label = "🌀 Fan", state = fanOn,backgroundColor = Color(0xFFFFF9C4)) {
                    fanOn = !fanOn
                    db.child("fanOn").setValue(fanOn)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ActionCard(label = "🚪 Door", state = doorOpen,backgroundColor = Color(0xFFC8E6C9)) {
                    doorOpen = !doorOpen
                    db.child("doorOpen").setValue(doorOpen)
                }

                Spacer(modifier = Modifier.size(140.dp))
            }
        }

    }
}

@Composable
fun ActionCard(label: String, state: Boolean, backgroundColor: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .size(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(if (state) "ON" else "OFF", color = Color.DarkGray)
        }
    }
}



fun playSiren(context: Context) {
    val mediaPlayer = MediaPlayer.create(context, R.raw.enzaar)
    mediaPlayer?.start()

    Handler(Looper.getMainLooper()).postDelayed({
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }, 60_000)
}
