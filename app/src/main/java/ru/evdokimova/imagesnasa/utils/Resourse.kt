package ru.evdokimova.imagesnasa.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Error<T>(message: String?, data: T? = null) : Resource<T>(message = message, data = data)
    class Loading<T> : Resource<T>()
    class Success<T>(data: T) : Resource<T>(data)
}
