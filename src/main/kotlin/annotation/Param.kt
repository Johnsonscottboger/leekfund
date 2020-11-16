package annotation

/**
 * 指定参数
 * @property name 指定参数名称
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Param(val name: String)