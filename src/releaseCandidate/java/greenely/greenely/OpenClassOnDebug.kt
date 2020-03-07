@file:Suppress("PackageDirectoryMismatch")

package greenely.greenely

/**
 * Turns off open class for release builds.
 */
@Target(AnnotationTarget.CLASS)
annotation class OpenClassOnDebug

