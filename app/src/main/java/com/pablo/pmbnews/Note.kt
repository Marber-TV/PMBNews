package com.pablo.pmbnews

data class Note(
    var id: String = "",
    val userId: String,
    val title: String,
    val description: String
)
