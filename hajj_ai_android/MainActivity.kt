
package com.example.hajj_ai_assistant

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

// Define a Message data class
data class Message(val text: String, val isUser: Boolean)

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()
    private val backendUrl = "https://8000-ikc25b2txd15t75hjg4fg-75d27763.sg1.manus.computer/chat" // Updated with the actual proxied URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatScreen()
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ChatScreen() {
        val messages = remember { mutableStateListOf<Message>() }
        var inputText by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF006400)) // Deep green for Islamic theme
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Hajj AI Assistant",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Chat Messages List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                reverseLayout = false
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }
            }

            // Input Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about Hajj rituals or Duas...") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val userMessage = inputText
                            messages.add(Message(userMessage, true))
                            inputText = ""
                            
                            scope.launch {
                                val response = sendMessageToBackend(userMessage)
                                messages.add(Message(response, false))
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF006400))
                ) {
                    Text("Send")
                }
            }
        }
    }

    @Composable
    fun MessageBubble(message: Message) {
        val alignment = if (message.isUser) Alignment.End else Alignment.Start
        val backgroundColor = if (message.isUser) Color(0xFFDCF8C6) else Color(0xFFF0F0F0)
        val textColor = Color.Black

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalAlignment = alignment
        ) {
            Surface(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(12.dp),
                    color = textColor,
                    fontSize = 16.sp
                )
            }
        }
    }

    private suspend fun sendMessageToBackend(message: String): String {
        val json = JSONObject()
        json.put("message", message)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(backendUrl)
            .post(body)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                val jsonResponse = JSONObject(responseData ?: "{}")
                jsonResponse.getString("response")
            } else {
                "Error: ${response.code}"
            }
        } catch (e: IOException) {
            Log.e("MainActivity", "Network error", e)
            "Network error: ${e.message}"
        }
    }
}
