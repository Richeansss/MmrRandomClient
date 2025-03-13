package com.example.mrrrandomclient.udp

import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class UdpClient(private val serverIp: String, private val serverPort: Int) {

    suspend fun sendCommand(command: String): String {
        return withContext(Dispatchers.IO) {
            try {
                DatagramSocket().use { socket ->
                    val address = InetAddress.getByName(serverIp)
                    val buffer = command.toByteArray()
                    val packet = DatagramPacket(buffer, buffer.size, address, serverPort)

                    socket.send(packet)

                    val responseBuffer = ByteArray(1024)
                    val responsePacket = DatagramPacket(responseBuffer, responseBuffer.size)
                    socket.receive(responsePacket)

                    return@withContext String(responsePacket.data, 0, responsePacket.length).trim()
                }
            } catch (e: Exception) {
                return@withContext "Ошибка: ${e.localizedMessage}"
            }
        }
    }
    fun listenResponses(callback: (String) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            DatagramSocket(serverPort).use { socket ->
                val buffer = ByteArray(1024)
                while (true) {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val message = String(packet.data, 0, packet.length).trim()
                    withContext(Dispatchers.Main) { callback(message) }
                }
            }
        }
    }
}
