package greenely.greenely.retailonboarding.ui.events

import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.retailonboarding.steps.PriceSummaryFragment
import javax.inject.Inject

class PriceSummaryErrorHandler@Inject constructor(
        private val fragment: PriceSummaryFragment
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