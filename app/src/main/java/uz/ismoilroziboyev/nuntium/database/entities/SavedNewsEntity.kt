package uz.ismoilroziboyev.nuntium.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SavedNewsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val url: String,
    val imageUrl: String,
    val topic: String
)