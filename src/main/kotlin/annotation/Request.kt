package annotation

import enum.RequestMethod
import resolver.IResolve
import resolver.impl.DefaultResolve
import kotlin.reflect.KClass

/**
 * 表示一个请求
 */
@Target(AnnotationTarget.FUNCTION)
annotation class Request(val method: RequestMethod,
                         val url: String,
                         val resolver : KClass<out IResolve<*>> = DefaultResolve::class)