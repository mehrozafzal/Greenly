package greenely.greenely.push.models

interface PushMessageVisitable {
    fun accept(visitor: PushMessageVisitor)
}

/**
 * The different states of the history view.
 */
enum class MessageType {
    HOME,
    HISTORY,
    RETAIL,
    FEED,
    GUIDANCE,
    COMPETE,
    UNKNOWN;

    companion object {
        /**
         * Create a [HistoryState] from [int].
         */
        fun fromInt(int: Int): MessageType = when (int) {
            1 -> HOME
            2 -> HISTORY
            3 -> RETAIL
            4 -> FEED
            5 -> GUIDANCE
            6 -> COMPETE
            else -> UNKNOWN
        }
    }
}


