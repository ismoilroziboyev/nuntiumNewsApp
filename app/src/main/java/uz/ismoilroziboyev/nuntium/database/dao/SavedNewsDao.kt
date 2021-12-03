package uz.ismoilroziboyev.nuntium.database.dao

import androidx.room.Dao
import androidx.room.Query
import uz.ismoilroziboyev.nuntium.database.entities.SavedNewsEntity


@Dao
interface SavedNewsDao : BaseDao<SavedNewsEntity> {

    @Query("select * from savednewsentity")
    fun getAllSavedNewsModels(): List<SavedNewsEntity>

    @Query("select count(*) from savednewsentity where title = :title and description = :description")
    fun getCountThisEntity(title: String, description: String): Int

    @Query("select count(*) from savednewsentity")
    fun getCountSavedNews(): Int

    @Query("select * from savednewsentity where title=:title and description = :description")
    fun getSavedNewsDao(title: String, description: String): SavedNewsEntity
}