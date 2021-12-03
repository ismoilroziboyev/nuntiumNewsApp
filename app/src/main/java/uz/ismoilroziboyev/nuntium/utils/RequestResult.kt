package uz.ismoilroziboyev.nuntium.utils

sealed class RequestResult<out T : Any> {

    object Loading : RequestResult<Nothing>()

    data class Error(val message: String) : RequestResult<Nothing>()

    data class Success<out T : Any>(val data: T) : RequestResult<T>()

}