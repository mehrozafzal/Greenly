package greenely.greenely.models

/**
 * Stateful representation of a resource.
 */
sealed class Resource<T> {

    /**
     * When the resource is loading.
     */
    class Loading<T> : Resource<T>() {
        /**
         * Any loading state should be equal to another loading state.
         */
        override fun equals(other: Any?): Boolean = other is Loading<*>

        /**
         * Generate hash code.
         */
        override fun hashCode(): Int {
            return javaClass.hashCode()
        }
    }

    /**
     * When a the resource has successfully loaded.
     */
    data class Success<T>(val value: T) : Resource<T>()

    /**
     * When there was an error loading the resource.
     */
    data class Error<T>(val error: Throwable) : Resource<T>()
}

