package uz.ismoilroziboyev.nuntium.networking

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import uz.ismoilroziboyev.nuntium.models.ResNewsOrgModel

interface NewsOrgApiService {

    @GET("everything")
    suspend fun getSearchedNews(
        @Query("apiKey") apiKey: String = "d0b3887f19f749c69ef3ec330aad9e7e",
        @Query("q") query: String,
        @Query("language") lang: String
    ): Response<ResNewsOrgModel>


}