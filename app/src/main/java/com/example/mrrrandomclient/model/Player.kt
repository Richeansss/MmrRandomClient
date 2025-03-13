package com.example.mrrrandomclient.model

data class Player(
    val id: Long, // Это должно быть id игрока с сервера
    val name: String, // Имя игрока
    val score: Int, // Рейтинг игрока
    val number: Int // Сгенерированное число игрока
)
