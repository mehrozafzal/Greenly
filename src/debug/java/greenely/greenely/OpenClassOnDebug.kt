package greenely.greenely

/**
 * Creates intermediate annotation to open a class when app is running in debug.
 *
 * @see OpenClass
 */
@OpenClass
@Target(AnnotationTarget.CLASS)
annotation class OpenClassOnDebug

