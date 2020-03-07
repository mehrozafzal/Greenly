/*
package greenely.greenely.splash.errors

import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.snackbar.AnyExceptionHandler
import greenely.greenely.errors.snackbar.HttpExceptionHandler
import greenely.greenely.errors.snackbar.IOExceptionHandler
import greenely.greenely.splash.ui.SplashActivity
import javax.inject.Inject

class SplashErrorHandler @Inject constructor(private val activity: SplashActivity) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {

            val httpExceptionHandler = HttpExceptionHandler(activity)
            val ioExceptionHandler = IOExceptionHandler(activity)
            val anyExceptionHandler = AnyExceptionHandler(activity)

            httpExceptionHandler.next = ioExceptionHandler
            ioExceptionHandler.next = anyExceptionHandler

            return httpExceptionHandler
        }
}*/
