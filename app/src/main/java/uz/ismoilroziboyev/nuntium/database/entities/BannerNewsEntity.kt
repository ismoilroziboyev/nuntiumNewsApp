package uz.ismoilroziboyev.nuntium.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.ismoilroziboyev.nuntium.models.News

@Entity
class BannerNewsEntity(
    @PrimaryKey
    var id: Int = 0,
    val news: List<News>
)