package greenely.greenely.retail.ui

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.alert.HttpExceptionHandler
import greenely.greenely.retailonboarding.ui.RetailOnboardingActivity
import javax.inject.Inject

@OpenClassOnDebug
class RedeemCodeErrorHandler @Inject constructor(
        private val activity: RetailOnboardingActivity
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {
            val httpExceptionHandler = HttpExceptionHandler(activity)
            val ioExceptionHandler = HttpExceptionHandler(activity)
            val anyExceptionHandler = HttpExceptionHandler(activity)

            httpExceptionHandler.next = ioExceptionHandler
            ioExceptionHandler.next = anyExceptionHandler

            return httpExceptionHandler
        }
}
