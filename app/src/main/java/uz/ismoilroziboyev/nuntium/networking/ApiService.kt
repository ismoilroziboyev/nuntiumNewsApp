package uz.ismoilroziboyev.nuntium.networking

import retrofit2.Response
import retrofit2.http.*
import uz.ismoilroziboyev.nuntium.models.ResCategoriesModel
import uz.ismoilroziboyev.nuntium.models.ResNewsModel
import uz.ismoilroziboyev.nuntium.utils.apiKey


interface ApiService {

    @GET("v1/available/categories")
    suspend fun getCategoriesList(
        @Query("apiKey") apikey: String = apiKey,
        @Query("language") lang: String
    ): Response<ResCategoriesModel>

    @GET("v1/latest-news")
    suspend fun getLatestNews(
        @Query("apiKey") apikey: String = apiKey,
        @Query("language") lang: String
    ): Response<ResNewsModel>


    @GET("v1/latest-news")
    suspend fun getLatestNewsByCaterory(
        @Query("apiKey") apikey: String = apiKey,
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("language") lang: String
    ): Response<ResNewsModel>

    @GET("v1/latest-news")
    suspend fun getNextPageLatestNews(
        @Query("apiKey") apikey: String = apiKey,
        @Query("category") category: String,
        @Query("page") page: Int,
        @Query("language") lang: String
    ): Response<ResNewsModel>


}
