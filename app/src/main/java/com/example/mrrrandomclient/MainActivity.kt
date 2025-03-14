package com.example.mrrrandomclient

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.mrrrandomclient.storage.TokenManager
import kotlinx.coroutines.launch
import com.example.mrrrandomclient.udp.UdpClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp(context = this)
        }
    }
}

@Composable
fun MainApp(context: Context, udpClient: UdpClient = UdpClient("10.0.2.2", 9876)) {
    val tokenManager = remember { TokenManager(context) }
    var playerToken by remember { mutableStateOf(tokenManager.getStoredToken() ?: "") }
    var playerName by remember { mutableStateOf(tokenManager.getStoredPlayerName() ?: "") }
    var isLoggedIn by remember { mutableStateOf(playerToken.isNotEmpty()) }

    val coroutineScope = rememberCoroutineScope() // Создаем scope для корутин

    if (isLoggedIn) {
        GameScreen(playerName, playerToken, udpClient) {
            coroutineScope.launch {
                tokenManager.clearData() // Запускаем в корутине
                playerToken = ""
                playerName = ""
                isLoggedIn = false
            }
        }
    } else {
        AuthScreen(tokenManager, udpClient) { name, token ->
            playerName = name
            playerToken = token
            isLoggedIn = true
        }
    }
}


@Composable
fun AuthScreen(tokenManager: TokenManager, udpClient: UdpClient, onLoginSuccess: (String, String) -> Unit) {
    var playerName by remember { mutableStateOf(TextFieldValue(tokenManager.getStoredPlayerName() ?: "")) }
    var serverResponse by remember { mutableStateOf("Ожидание ответа...") }
    val coroutineScope = rememberCoroutineScope()

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
                        val response = udpClient.sendCommand("JOIN $name")
                        serverResponse = response

                        val tokenPrefix = "Ваш токен: "
                        if (response.contains(tokenPrefix)) {
                            val token = response.substringAfter(tokenPrefix).trim()
                            tokenManager.saveToken(token)
                            tokenManager.savePlayerName(name)
                            onLoginSuccess(name, token)
                        }
                    } catch (e: Exception) {
                        serverResponse = "Ошибка: ${e.message}"
                    }
                }
            }
        }) {
            Text("Присоединиться")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = serverResponse)
    }
}

@Composable
fun GameScreen(playerName: String, playerToken: String, udpClient: UdpClient, onLogout: () -> Unit) {
    var serverResponse by remember { mutableStateOf("Готов к движению...") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Игрок: $playerName", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = {
            coroutineScope.launch {
                try {
                    val x = (0..100).random()
                    val y = (0..100).random()
                    serverResponse = udpClient.sendCommand("MOVE $playerName $playerToken $x $y")
                } catch (e: Exception) {
                    serverResponse = "Ошибка: ${e.message}"
                }
            }
        }) {
            Text("Двигаться")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(onClick = { onLogout() }) {
            Text("Выйти из аккаунта")
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(text = serverResponse)
    }
}
