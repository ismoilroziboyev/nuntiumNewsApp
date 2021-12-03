package uz.ismoilroziboyev.nuntium.database.dao

import androidx.room.Dao
import androidx.room.Query
import uz.ismoilroziboyev.nuntium.database.entities.FavouriteTopicsList

@Dao
interface FavouriteTopicsDao : BaseDao<FavouriteTopicsList> {

    @Query("select * from favouritetopicslist")
    fun getFavouriteTopics(): FavouriteTopicsList


    @Query("select count(*) from favouritetopicslist")
    fun getCountFavouriteTopicModel(): Int
}