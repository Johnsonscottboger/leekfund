package http.impl

import http.IRequest
import org.apache.http.client.CookieStore
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.message.BasicNameValuePair
import org.apache.http.ssl.SSLContextBuilder
import org.apache.http.util.EntityUtils

/**
 * Http 请求
 */
class HttpClientRequest : IRequest {
    private val cookieStore: CookieStore by lazy {
        BasicCookieStore()
    }

    private val httpClient: CloseableHttpClient by lazy {
        HttpClients.custom()
                .setConnectionManager(pool)
                .setConnectionManagerShared(true)
                .setDefaultCookieStore(cookieStore)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(DefaultHttpRequestRetryHandler(1, true))
                .build()
    }

    override fun get(url: String): String {
        return getInternal(HttpGet(url))
    }

    override fun post(url: String, maps: Map<String, String>): String {
        val httpPost = HttpPost(url)
        if(maps.isNotEmpty()){
            httpPost.entity = UrlEncodedFormEntity(maps.map { p -> BasicNameValuePair(p.key, p.value) }, CHARSET_UTF_8)
        }
        return postInternal(httpPost)
    }

    private fun getInternal(httpGet: HttpGet): String {
        val defaultContent = ""
        return httpClient.use {
            httpGet.config = requestConfig
            httpClient.execute(httpGet).use httpClient@{
                val response = it
                if (response.statusLine != null) {
                    if (response.statusLine.statusCode >= 300)
                        return@httpClient defaultContent
                    val responseContent = EntityUtils.toString(it.entity, CHARSET_UTF_8)
                    EntityUtils.consume(it.entity)
                    return@use responseContent
                } else {
                    return defaultContent
                }
            }
        }
    }

    private fun postInternal(httpPost: HttpPost): String {
        val defaultContent = ""
        return httpClient.use {
            httpPost.config = requestConfig
            httpClient.execute(httpPost).use httpClient@{
                val response = it
                if (response.statusLine != null) {
                    if (response.statusLine.statusCode >= 300)
                        return@httpClient defaultContent
                    val responseContent = EntityUtils.toString(it.entity, CHARSET_UTF_8)
                    EntityUtils.consume(it.entity)
                    return@use responseContent
                } else {
                    return defaultContent
                }
            }
        }
    }


    companion object {
        const val CHARSET_UTF_8 = "utf-8"

        const val CONTENT_TYPE_TEXT_HTML = "text/xml"

        const val CONTENT_TYPE_FORM_URL = "application/x-www-form-urlencoded"

        const val CONTENT_TYPE_JSON_URL = "application/json;charset=utf-8"

        private val pool: PoolingHttpClientConnectionManager by lazy {
            val builder = SSLContextBuilder()
            builder.loadTrustMaterial(null, TrustSelfSignedStrategy())
            val sslsf = SSLConnectionSocketFactory(builder.build())
            val socketFactoryRegistry = RegistryBuilder.create<ConnectionSocketFactory>()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslsf)
                    .build()
            PoolingHttpClientConnectionManager(socketFactoryRegistry).apply {
                maxTotal = 200
                defaultMaxPerRoute = 2
            }
        }

        private val requestConfig: RequestConfig by lazy {
            RequestConfig.custom()
                    .setConnectionRequestTimeout(60000)
                    .setSocketTimeout(60000)
                    .setConnectTimeout(60000)
                    .build()
        }
    }
}