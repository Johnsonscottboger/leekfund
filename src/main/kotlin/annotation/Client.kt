package annotation

/**
 * 指定的客户端
 * @property url 指定的 URL 地址
 */
@Target(AnnotationTarget.CLASS)
annotation class Client(val url: String)