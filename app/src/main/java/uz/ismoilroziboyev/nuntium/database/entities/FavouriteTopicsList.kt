package uz.ismoilroziboyev.nuntium.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavouriteTopicsList(
    @PrimaryKey
    val id: Int = 0,
    val selectedTopics: List<String>,
    val allTopics: List<String>
)