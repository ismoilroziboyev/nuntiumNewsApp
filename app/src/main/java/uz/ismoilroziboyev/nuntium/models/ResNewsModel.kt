package uz.ismoilroziboyev.nuntium.models

data class ResNewsModel(
    val status: String,
    val news: List<News>,
    val page: Int
)

