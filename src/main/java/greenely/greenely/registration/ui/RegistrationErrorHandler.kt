package greenely.greenely.registration.ui

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.alert.AnyExceptionHandler
import greenely.greenely.errors.alert.HttpExceptionHandler
import greenely.greenely.errors.alert.IOExceptionHandler
import javax.inject.Inject

@OpenClassOnDebug
class RegistrationErrorHandler @Inject constructor(
        private val activity: RegistrationActivity
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {
            val httpExceptionHandler = HttpExceptionHandler(activity)
            val ioExceptionHandler = IOExceptionHandler(activity)
            val anyExceptionHandler = AnyExceptionHandler(activity)

            httpExceptionHandler.next = ioExceptionHandler
            ioExceptionHandler.next = anyExceptionHandler

            return httpExceptionHandler
        }

}
