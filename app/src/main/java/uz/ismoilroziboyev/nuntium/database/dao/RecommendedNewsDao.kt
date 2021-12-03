package uz.ismoilroziboyev.nuntium.database.dao

import androidx.room.Dao
import androidx.room.Query
import uz.ismoilroziboyev.nuntium.database.entities.RecommendNewsEntity

@Dao
interface RecommendedNewsDao : BaseDao<RecommendNewsEntity> {


    @Query("select * from recommendnewsentity")
    fun getRecommendedNews(): RecommendNewsEntity

    @Query("select count(*) from recommendnewsentity")
    fun getCountRecommendedEntities(): Int

}