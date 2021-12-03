package uz.ismoilroziboyev.nuntium.database.dao

import androidx.room.Dao
import androidx.room.Query
import uz.ismoilroziboyev.nuntium.database.entities.BannerNewsEntity

@Dao
interface BannerNewsDao : BaseDao<BannerNewsEntity> {

    @Query("select  * from bannernewsentity")
    fun getAllBannerNews(): BannerNewsEntity

    @Query("select count(*) from bannernewsentity")
    fun getCountBannerNews(): Int

}