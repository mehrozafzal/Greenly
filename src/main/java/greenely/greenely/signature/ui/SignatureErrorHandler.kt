package greenely.greenely.signature.ui

import greenely.greenely.errors.ErrorHandler
import greenely.greenely.errors.ErrorHandlerTemplate
import greenely.greenely.errors.alert.AnyExceptionHandler
import greenely.greenely.errors.alert.CustomVerificationExceptionHandler
import greenely.greenely.errors.alert.HttpExceptionHandler
import greenely.greenely.errors.alert.IOExceptionHandler
import javax.inject.Inject

class SignatureErrorHandler @Inject constructor(
        private val activity: SignatureActivity
) : ErrorHandlerTemplate() {
    override val errorHandler: ErrorHandler
        get() {
            val httpErrorHandler = HttpExceptionHandler(activity)
            val ioErrorHandler = IOExceptionHandler(activity)
            val anyErrorHandler = AnyExceptionHandler(activity)
            val verificationExceptionHandler = CustomVerificationExceptionHandler(activity)


            httpErrorHandler.next = ioErrorHandler
            ioErrorHandler.next=verificationExceptionHandler
            verificationExceptionHandler.next = anyErrorHandler


            return httpErrorHandler
        }
}

