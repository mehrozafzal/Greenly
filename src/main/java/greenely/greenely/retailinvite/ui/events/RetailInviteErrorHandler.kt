package greenely.greenely.retailinvite.ui.events

import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.retailinvite.ui.RetailInviteFragment
import javax.inject.Inject

class RetailInviteErrorHandler@Inject constructor(
        private val fragment: RetailInviteFragment
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {

            val httpExceptionHandler = greenely.greenely.errors.snackbar.HttpExceptionHandler(fragment)
            val ioExceptionHandler = greenely.greenely.errors.snackbar.IOExceptionHandler(fragment)
            val anyExceptionHandler = greenely.greenely.errors.snackbar.AnyExceptionHandler(fragment)

            httpExceptionHandler.next = ioExceptionHandler
            ioExceptionHandler.next = anyExceptionHandler

            return httpExceptionHandler
        }
}