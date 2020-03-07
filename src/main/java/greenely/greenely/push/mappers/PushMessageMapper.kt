package greenely.greenely.push.mappers

import android.os.Bundle
import greenely.greenely.push.models.*
import javax.inject.Inject

class PushMessageMapper @Inject constructor() {
    fun fromBundle(data: Bundle): PushMessageVisitable? {
        val deepLink: String? = data.getString("deep_link_url")

        return when (deepLink) {
            "greenelyapp://home" -> DataMessage(data.getString("body"), data.getString("title"), MessageType.HOME)
            "greenelyapp://history" -> DataMessage(data.getString("body"), data.getString("title"), MessageType.HISTORY)
            "greenelyapp://retail" -> DataMessage(data.getString("body"), data.getString("title"), MessageType.RETAIL)
            "greenelyapp://feed" -> DataMessage(data.getString("body"), data.getString("title"), MessageType.FEED)
            "greenelyapp://guidance" -> DataMessage(data.getString("body"), data.getString("title"), MessageType.GUIDANCE)
            "greenelyapp://compete" -> DataMessage(data.getString("body"), data.getString("title"), MessageType.COMPETE)
            else -> UnknownMessage(data.getString("body"), data.getString("title"))
        }
    }
}

