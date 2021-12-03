package uz.ismoilroziboyev.nuntium.repositories

import kotlinx.coroutines.flow.flow
import uz.ismoilroziboyev.nuntium.database.dao.BannerNewsDao
import uz.ismoilroziboyev.nuntium.database.dao.FavouriteTopicsDao
import uz.ismoilroziboyev.nuntium.database.dao.RecommendedNewsDao
import uz.ismoilroziboyev.nuntium.database.dao.SavedNewsDao
import uz.ismoilroziboyev.nuntium.database.entities.BannerNewsEntity
import uz.ismoilroziboyev.nuntium.database.entities.FavouriteTopicsList
import uz.ismoilroziboyev.nuntium.database.entities.RecommendNewsEntity
import uz.ismoilroziboyev.nuntium.database.entities.SavedNewsEntity
import uz.ismoilroziboyev.nuntium.networking.ApiService
import uz.ismoilroziboyev.nuntium.networking.NewsOrgApiService
import javax.inject.Inject
import javax.inject.Named

class Repository @Inject constructor(
    @Named("currentsApiService") private val apiService: ApiService,
    @Named("newsOrgApiService") private val newsOrgApiService: NewsOrgApiService,
    private val favouriteTopicsDao: FavouriteTopicsDao,
    private val bannerNewsDao: BannerNewsDao,
    private val recommendedNewsDao: RecommendedNewsDao,
    private val savedNewsDao: SavedNewsDao
) {
    suspend fun getCategoriesList(lang: String) =
        flow { emit(apiService.getCategoriesList(lang = lang)) }

    suspend fun getFavouriteTopicsFromDb() = flow { emit(favouriteTopicsDao.getFavouriteTopics()) }
    fun insertFavouriteTopicToDb(favouriteTopicsList: FavouriteTopicsList) =
        favouriteTopicsDao.insert(favouriteTopicsList)

    fun updateFavouriteTopicsToDb(favouriteTopicsList: FavouriteTopicsList) =
        favouriteTopicsDao.update(favouriteTopicsList)

    fun getCountFavouriteTopicModelInDb() = favouriteTopicsDao.getCountFavouriteTopicModel()

    suspend fun getBannerNewsFormInt(lang: String) =
        flow { emit(apiService.getLatestNews(lang = lang)) }

    suspend fun getBannerNewsFromDb() = flow { emit(bannerNewsDao.getAllBannerNews()) }

    fun insertToDbBannerNews(bannerNewsEntity: BannerNewsEntity) =
        bannerNewsDao.insert(bannerNewsEntity)

    fun updateBannerNews(bannerNewsEntity: BannerNewsEntity) =
        bannerNewsDao.update(bannerNewsEntity)

    fun getCountBannerNewsInDb() = bannerNewsDao.getCountBannerNews()

    suspend fun getRecommendedNewsFromDb() = flow { emit(recommendedNewsDao.getRecommendedNews()) }

    suspend fun getRecommendedNewsFromInt(categories: String, lang: String) =
        flow {
            emit(
                apiService.getLatestNewsByCaterory(
                    category = categories,
                    page = 0,
                    lang = lang
                )
            )
        }

    fun getCountRecommendNewsFromDb() = recommendedNewsDao.getCountRecommendedEntities()

    fun insertRecommendedNewsToDb(recommendNewsEntity: RecommendNewsEntity) =
        recommendedNewsDao.insert(recommendNewsEntity)

    fun updateRecommendedNewsToDb(recommendNewsEntity: RecommendNewsEntity) =
        recommendedNewsDao.update(recommendNewsEntity)

    suspend fun getTopicNewsByTopic(topic: String, page: Int, lang: String) =
        flow {
            emit(
                apiService.getLatestNewsByCaterory(
                    category = topic,
                    page = page,
                    lang = lang
                )
            )
        }

    suspend fun getNextPageLatestNews(topic: String, page: Int, lang: String) =
        flow { emit(apiService.getNextPageLatestNews(category = topic, page = page, lang = lang)) }

    suspend fun getSearchedNews(query: String, lang: String) =
        flow { emit(newsOrgApiService.getSearchedNews(query = query, lang = lang)) }


    suspend fun getSavedNewsModels() = flow { emit(savedNewsDao.getAllSavedNewsModels()) }

    fun getCountThisSavedNewsEntity(title: String, description: String) =
        savedNewsDao.getCountThisEntity(title, description)

    fun getCountSavedNewsInDb(): Int = savedNewsDao.getCountSavedNews()

    fun insertNewSavedNewsEntity(savedNewsEntity: SavedNewsEntity) =
        savedNewsDao.insert(savedNewsEntity)

    fun deleteSavedNewsEntity(savedNewsEntity: SavedNewsEntity) =
        savedNewsDao.delete(savedNewsEntity)

    fun getSavedNewsEntity(title: String, description: String) =
        savedNewsDao.getSavedNewsDao(title, description)
}
