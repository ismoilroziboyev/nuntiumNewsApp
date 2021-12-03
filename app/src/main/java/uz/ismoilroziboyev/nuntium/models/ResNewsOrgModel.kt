package uz.ismoilroziboyev.nuntium.models

data class ResNewsOrgModel(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)
