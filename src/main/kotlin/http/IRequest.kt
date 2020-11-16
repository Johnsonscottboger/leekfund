package http

import http.impl.HttpClientRequest

interface IRequest {
    fun get(url: String): String

    fun post(url: String, maps: Map<String, String> = emptyMap()): String

    fun request(method: String, url: String, maps: Map<String, String> = emptyMap()): String {
        return when (method.toUpperCase()) {
            "GET" -> get(url)
            "POST" -> post(url, maps)
            else -> throw  UnsupportedOperationException()
        }
    }

    companion object {
        val default: IRequest = HttpClientRequest()
    }
}