package greenely.greenely.push.data.models

import com.squareup.moshi.Json

data class RegisterDeviceRequest(@field:Json(name = "device_id") val deviceId: String)

