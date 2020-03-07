package greenely.greenely.home.ui

import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.snackbar.AnyExceptionHandler
import greenely.greenely.errors.snackbar.HttpExceptionHandler
import greenely.greenely.errors.snackbar.IOExceptionHandler
import javax.inject.Inject

class HomeErrorHandler @Inject constructor(
        private val fragment: HomeFragment
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {
            val ioHandler = IOExceptionHandler(fragment)
            val httpHandler = HttpExceptionHandler(fragment)
            val anyHandler = AnyExceptionHandler(fragment)

            ioHandler.next = httpHandler
            httpHandler.next = anyHandler

            return ioHandler
        }

}

