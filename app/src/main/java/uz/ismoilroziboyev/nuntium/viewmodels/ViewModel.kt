package uz.ismoilroziboyev.nuntium.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import uz.ismoilroziboyev.nuntium.database.entities.BannerNewsEntity
import uz.ismoilroziboyev.nuntium.database.entities.FavouriteTopicsList
import uz.ismoilroziboyev.nuntium.database.entities.RecommendNewsEntity
import uz.ismoilroziboyev.nuntium.database.entities.SavedNewsEntity
import uz.ismoilroziboyev.nuntium.models.ResCategoriesModel
import uz.ismoilroziboyev.nuntium.models.ResNewsModel
import uz.ismoilroziboyev.nuntium.models.ResNewsOrgModel
import uz.ismoilroziboyev.nuntium.repositories.Repository
import uz.ismoilroziboyev.nuntium.utils.RequestResult
import uz.mobiler.mvvmg.utils.NetworkHelper
import java.lang.StringBuilder
import javax.inject.Inject

class ViewModel @Inject constructor(val repository: Repository, val networkHelper: NetworkHelper) :
    ViewModel() {

    val categoriesStateflow =
        MutableStateFlow<RequestResult<ResCategoriesModel>>(RequestResult.Loading)

    val favouriteTopicsList =
        MutableStateFlow<RequestResult<FavouriteTopicsList>>(RequestResult.Loading)

    val bannerNewsFlow = MutableStateFlow<RequestResult<BannerNewsEntity>>(RequestResult.Loading)

    val recommendedNewsFlow =
        MutableStateFlow<RequestResult<RecommendNewsEntity>>(RequestResult.Loading)

    val topicNewsFlow = MutableStateFlow<RequestResult<ResNewsModel>>(RequestResult.Loading)


    val searchedNewsFlow = MutableStateFlow<RequestResult<ResNewsOrgModel>>(RequestResult.Loading)

    val savedNewsFlow =
        MutableStateFlow<RequestResult<List<SavedNewsEntity>>>(RequestResult.Loading)

    fun fetchSavedNewsModels() {
        viewModelScope.launch {
            repository.getSavedNewsModels().catch {
                savedNewsFlow.emit(RequestResult.Error(it.message ?: ""))
            }.collect {
                savedNewsFlow.emit(RequestResult.Success(it))
            }
        }
    }

    private fun getLocale() = Lingver.getInstance().getLanguage()


    fun fetchNewSearchedNews(query: String) {
        viewModelScope.launch {
            searchedNewsFlow.emit(RequestResult.Loading)
            if (networkHelper.isNetworkConnected()) {
                repository.getSearchedNews(query, getLocale()).catch {
                    searchedNewsFlow.emit(
                        RequestResult.Error(
                            it.message ?: "Something went wrong!"
                        )
                    )
                }.collect {
                    if (it.isSuccessful) {
                        searchedNewsFlow.emit(RequestResult.Success(it.body()!!))
                    } else {
                        searchedNewsFlow.emit(
                            RequestResult.Error(
                                it.message() ?: "Something went wrong!"
                            )
                        )
                    }
                }
            } else {
                searchedNewsFlow.emit(RequestResult.Error("No internet connection!"))
            }
        }
    }

    fun getNextPageNews(topic: String, pager: Int): StateFlow<RequestResult<ResNewsModel>> {
        val mutableStateFlow = MutableStateFlow<RequestResult<ResNewsModel>>(RequestResult.Loading)

        viewModelScope.launch {
            repository.getNextPageLatestNews(topic, pager, getLocale()).catch {
                mutableStateFlow.emit(RequestResult.Error(it.message ?: ""))
            }.collect {
                if (it.isSuccessful) {
                    mutableStateFlow.emit(RequestResult.Success(it.body()!!))
                } else {
                    mutableStateFlow.emit(RequestResult.Error(it.message() ?: ""))
                }
            }
        }


        return mutableStateFlow
    }


    fun fetchTopicNewsModel(topic: String, page: Int) {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getTopicNewsByTopic(topic, page, getLocale()).catch {
                    topicNewsFlow.emit(RequestResult.Error(it.message ?: ""))
                }.collect {
                    if (it.isSuccessful) {
                        topicNewsFlow.emit(RequestResult.Success(it.body()!!))
                    } else {
                        topicNewsFlow.emit(RequestResult.Error(it.message() ?: ""))
                    }
                }
            } else {
                topicNewsFlow.emit(RequestResult.Error("No internet connection!"))
            }
        }
    }

    fun updateOrInsertFavouriteTopics(favouriteTopicsList: FavouriteTopicsList) {
        if (repository.getCountFavouriteTopicModelInDb() == 0) {
            repository.insertFavouriteTopicToDb(favouriteTopicsList)
        } else {
            repository.updateFavouriteTopicsToDb(favouriteTopicsList)
        }
    }

    fun getFavouritesList(): StateFlow<RequestResult<FavouriteTopicsList>> {
        viewModelScope.launch {
            if (repository.getCountFavouriteTopicModelInDb() != 0) {
                repository.getFavouriteTopicsFromDb().catch {
                    favouriteTopicsList.emit(RequestResult.Error(it.message ?: ""))
                }.collect {
                    favouriteTopicsList.emit(RequestResult.Success(it))
                }
            } else {
                favouriteTopicsList.emit(RequestResult.Error("Has no data"))
            }
        }

        return favouriteTopicsList
    }

    fun insertOrUpdateBannerNews(bannerNewsEntity: BannerNewsEntity) {
        viewModelScope.launch {
            if (repository.getCountBannerNewsInDb() == 0) {
                repository.insertToDbBannerNews(bannerNewsEntity)
            } else {
                repository.getBannerNewsFromDb().collect {
                    bannerNewsEntity.id = it.id
                    repository.updateBannerNews(bannerNewsEntity)
                }
            }
        }
    }

    fun fetchBannerNews() {
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getBannerNewsFormInt(getLocale()).catch {
                    bannerNewsFlow.emit(RequestResult.Error(it.message ?: ""))
                }.collect {
                    if (it.isSuccessful) {
                        val bannerNewsEntity = BannerNewsEntity(news = it.body()!!.news)
                        bannerNewsFlow.emit(RequestResult.Success(bannerNewsEntity))
                    } else {
                        bannerNewsFlow.emit(RequestResult.Error(it.message() ?: ""))
                    }
                }
            } else {
                if (repository.getCountBannerNewsInDb() == 0) {
                    bannerNewsFlow.emit(RequestResult.Error("Has no data"))
                } else {
                    repository.getBannerNewsFromDb().catch {
                        bannerNewsFlow.emit(RequestResult.Error("Has no data"))
                    }.collect {
                        bannerNewsFlow.emit(RequestResult.Success(it))
                    }
                }
            }
        }
    }

    fun fetchCategoriesList() {
        viewModelScope.launch {
            repository.getCategoriesList(getLocale()).catch {
                categoriesStateflow.emit(RequestResult.Error(it.message ?: ""))
            }.collect {
                if (it.isSuccessful) {
                    categoriesStateflow.emit(RequestResult.Success(it.body()!!))
                } else {
                    categoriesStateflow.emit(RequestResult.Error(it.message() ?: ""))
                }
            }
        }
    }

    fun fetchRecommendedNews() {
        val topics: String = getTopicsList()
        viewModelScope.launch {
            if (networkHelper.isNetworkConnected()) {
                repository.getRecommendedNewsFromInt(topics, getLocale()).catch {
                    recommendedNewsFlow.emit(RequestResult.Error(it.message ?: ""))
                }.collect {
                    if (it.isSuccessful) {
                        val recommendNewsEntity = RecommendNewsEntity(news = it.body()!!.news)
                        recommendedNewsFlow.emit(RequestResult.Success(recommendNewsEntity))
                    } else {
                        recommendedNewsFlow.emit(RequestResult.Error(it.message() ?: ""))
                    }
                }
            } else {
                if (repository.getCountRecommendNewsFromDb() == 0) {
                    recommendedNewsFlow.emit(RequestResult.Error("Has no data"))
                } else {
                    repository.getRecommendedNewsFromDb().catch {
                        recommendedNewsFlow.emit(RequestResult.Error("Has no data"))
                    }.collect {
                        recommendedNewsFlow.emit(RequestResult.Success(it))
                    }
                }
            }
        }
    }

    fun getTopicsList(): String {
        val string = StringBuilder()
        var result = ""

        viewModelScope.launch {
            repository.getFavouriteTopicsFromDb().collect {
                it.selectedTopics.forEachIndexed { index, s ->
                    if (index == it.selectedTopics.size - 1) {
                        string.append(s)
                    } else {
                        string.append("$s,")
                    }
                }

                result = string.toString()

            }
        }

        return result
    }

    fun insertOrUpdateRecommendedNews(recommendNewsEntity: RecommendNewsEntity) {
        viewModelScope.launch {
            if (repository.getCountRecommendNewsFromDb() == 0) {
                repository.insertRecommendedNewsToDb(recommendNewsEntity)
            } else {
                repository.getRecommendedNewsFromDb().collect {
                    recommendNewsEntity.id = it.id
                    repository.updateRecommendedNewsToDb(recommendNewsEntity)
                }
            }
        }
    }


}