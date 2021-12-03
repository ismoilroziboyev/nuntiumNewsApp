package uz.ismoilroziboyev.nuntium.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.ismoilroziboyev.nuntium.networking.ApiService
import uz.ismoilroziboyev.nuntium.networking.NewsOrgApiService
import uz.mobiler.mvvmg.utils.NetworkHelper
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {
    @Provides
    @Singleton
    @Named("currentsApiBaseUrl")
    fun provideBaseUrl() = "https://api.currentsapi.services/"

    @Provides
    @Singleton
    @Named("newsOrgBaseUrl")
    fun provideBaseUrl2() = "https://newsapi.org/v2/"

    @Provides
    @Singleton
    fun provideGsonConvertorFactory() = GsonConverterFactory.create()


    @Provides
    @Singleton
    @Named("newsOrgRetrofit")
    fun provideRetrofitNEwsOrg(
        @Named("newsOrgBaseUrl") baseUrl: String,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory).build()
    }

    @Provides
    @Singleton
    @Named("newsOrgApiService")
    fun provideApiService(@Named("newsOrgRetrofit") retrofit: Retrofit): NewsOrgApiService =
        retrofit.create(NewsOrgApiService::class.java)


    @Provides
    @Singleton
    @Named("currentsRetrofit")
    fun provideRetrofit(
        @Named("currentsApiBaseUrl") baseUrl: String,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory).build()
    }


    @Provides
    @Singleton
    @Named("currentsApiService")
    fun provieApiService(@Named("currentsRetrofit") retrofit: Retrofit) =
        retrofit.create(ApiService::class.java)


    @Provides
    @Singleton
    fun getNewtworkhelper(context: Context) = NetworkHelper(context)
}