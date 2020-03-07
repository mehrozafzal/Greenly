package greenely.greenely.retail.errors

import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.snackbar.AnyExceptionHandler
import greenely.greenely.errors.snackbar.HttpExceptionHandler
import greenely.greenely.errors.snackbar.IOExceptionHandler
import greenely.greenely.retail.ui.RetailFragment
import javax.inject.Inject

class RetailErrorHandler @Inject constructor(
        private val fragment: RetailFragment
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {

            val httpExceptionHandler = HttpExceptionHandler(fragment)
            val ioExceptionHandler = IOExceptionHandler(fragment)
            val anyExceptionHandler = AnyExceptionHandler(fragment)

            httpExceptionHandler.next = ioExceptionHandler
            ioExceptionHandler.next = anyExceptionHandler

            return httpExceptionHandler
        }
}
