package api

import annotation.Client
import annotation.Param
import annotation.Request
import http.IRequest
import resolver.IResolve
import resolver.impl.DefaultResolve
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.reflect.full.createInstance

/**
 * Api
 */
object ApiManager : InvocationHandler {

    private val request = IRequest.default

    final inline fun <reified T> getInstance() : T {
        return Proxy.newProxyInstance(this::class.java.classLoader, arrayOf(T::class.java), this) as T
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any {
        val client = method!!.declaringClass.getAnnotation(Client::class.java) ?: throw UnsupportedOperationException()
        val host = client.url
        val requestAnnotation = method.getAnnotation(Request::class.java)
                ?: throw java.lang.UnsupportedOperationException()
        val resolve: IResolve<*> = requestAnnotation.resolver.createInstance()
        var api = requestAnnotation.url
        method.parameters.forEachIndexed { i, p ->
            p.getAnnotation(Param::class.java)?.run {
                val format = "{${this.name}}"
                api = args?.get(i)?.toString()?.let { api.replace(format, it) }.toString()
            }
        }

        val params = args?.get(0) as? Map<String, String>? ?: emptyMap()
        val url = if(api.startsWith("http", true)) api else host + api
        val content = request.request(requestAnnotation.method.name, url, params)
        return if (resolve !is DefaultResolve) {
            resolve.resolve(content)
        } else {
            return content
        }
    }
}