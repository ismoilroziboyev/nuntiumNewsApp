package uz.ismoilroziboyev.nuntium.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import uz.ismoilroziboyev.nuntium.database.AppDatabase
import uz.ismoilroziboyev.nuntium.database.dao.BannerNewsDao
import uz.ismoilroziboyev.nuntium.database.dao.FavouriteTopicsDao
import uz.ismoilroziboyev.nuntium.database.dao.RecommendedNewsDao
import uz.ismoilroziboyev.nuntium.database.dao.SavedNewsDao
import javax.inject.Singleton

@Module
class DatabaseModule(val context: Context) {


    @Provides
    @Singleton
    fun provieContext(): Context = context

    @Provides
    @Singleton
    fun provideRoom(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "my_db")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()
    }


    @Provides
    @Singleton
    fun provideFavouriteTopicsDao(appDatabase: AppDatabase): FavouriteTopicsDao =
        appDatabase.favouriteTopicsDao()


    @Provides
    @Singleton
    fun provideBannerNewsDao(appDatabase: AppDatabase): BannerNewsDao = appDatabase.bannerNewsDao()


    @Provides
    @Singleton
    fun provideRecommendedNewsDao(appDatabase: AppDatabase): RecommendedNewsDao =
        appDatabase.recommendedNewsDao()

    @Provides
    @Singleton
    fun provideSavedNewsDao(appDatabase: AppDatabase): SavedNewsDao = appDatabase.savedNewsDao()
}