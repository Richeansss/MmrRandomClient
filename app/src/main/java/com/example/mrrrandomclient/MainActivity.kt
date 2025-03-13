package com.example.mrrrandomclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.mrrrandomclient.udp.UdpClient

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClientScreen()
        }
    }
}

@Composable
fun ClientScreen(udpClient: UdpClient = UdpClient("10.0.2.2", 9876)) {
    var playerName by remember { mutableStateOf(TextFieldValue("")) }
    var serverResponse by remember { mutableStateOf("Ожидание ответа...") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        udpClient.listenResponses { response ->
            serverResponse = response
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = playerName,
            onValueChange = { playerName = it },
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            label = { Text("Введите имя игрока") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            val name = playerName.text.trim()
            if (name.isNotEmpty()) {
                coroutineScope.launch {
                    try {
                        serverResponse = udpClient.sendCommand("JOIN $name")
                    } catch (e: Exception) {
                        serverResponse = "Ошибка: ${e.message}"
                    }
                }
            }
        }) {
            Text("Присоединиться")
        }

        Button(onClick = {
            val name = playerName.text.trim()
            if (name.isNotEmpty()) {
                coroutineScope.launch {
                    try {
                        serverResponse = udpClient.sendCommand("LEAVE $name")
                    } catch (e: Exception) {
                        serverResponse = "Ошибка: ${e.message}"
                    }
                }
            }
        }) {
            Text("Выйти")
        }

        Button(onClick = {
            coroutineScope.launch {
                try {
                    serverResponse = udpClient.sendCommand("COUNT")
                } catch (e: Exception) {
                    serverResponse = "Ошибка: ${e.message}"
                }
            }
        }) {
            Text("Количество игроков")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = serverResponse)
    }
}
