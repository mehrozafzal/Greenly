package greenely.greenely.errors

/**
 * Chain for error handling.
 */
interface ErrorHandler {

    /**
     * Handle the [error].
     */
    fun handleError(error: Throwable)
}

