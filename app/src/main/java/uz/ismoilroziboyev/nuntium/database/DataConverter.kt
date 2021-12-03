package uz.ismoilroziboyev.nuntium.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import uz.ismoilroziboyev.nuntium.models.News

class DataConverter {


    @TypeConverter
    fun toJson(list: List<String>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJson(string: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type

        return Gson().fromJson(
            string, type
        )
    }


    @TypeConverter
    fun toJsonNewsList(list: List<News>): String {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun fromJsonNewsList(string: String): List<News> {
        val type = object : TypeToken<List<News>>() {}.type

        return Gson().fromJson(
            string, type
        )
    }

}