package uz.ismoilroziboyev.nuntium.models

import java.io.Serializable

data class News(
    val author: String,
    val category: List<String>,
    val description: String,
    val id: String,
    val image: String,
    val language: String,
    val published: String,
    val title: String,
    val url: String
):Serializable