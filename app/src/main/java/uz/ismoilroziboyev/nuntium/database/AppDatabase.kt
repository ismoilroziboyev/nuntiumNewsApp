package uz.ismoilroziboyev.nuntium.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import uz.ismoilroziboyev.nuntium.database.dao.BannerNewsDao
import uz.ismoilroziboyev.nuntium.database.dao.FavouriteTopicsDao
import uz.ismoilroziboyev.nuntium.database.dao.RecommendedNewsDao
import uz.ismoilroziboyev.nuntium.database.dao.SavedNewsDao
import uz.ismoilroziboyev.nuntium.database.entities.BannerNewsEntity
import uz.ismoilroziboyev.nuntium.database.entities.FavouriteTopicsList
import uz.ismoilroziboyev.nuntium.database.entities.RecommendNewsEntity
import uz.ismoilroziboyev.nuntium.database.entities.SavedNewsEntity

@Database(
    version = 1,
    entities = [FavouriteTopicsList::class, BannerNewsEntity::class, RecommendNewsEntity::class, SavedNewsEntity::class]
)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {


    abstract fun favouriteTopicsDao(): FavouriteTopicsDao
    abstract fun bannerNewsDao(): BannerNewsDao
    abstract fun recommendedNewsDao(): RecommendedNewsDao
    abstract fun savedNewsDao(): SavedNewsDao

}