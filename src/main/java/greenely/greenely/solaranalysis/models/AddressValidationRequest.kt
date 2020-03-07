package greenely.greenely.solaranalysis.models

import com.squareup.moshi.Json

data class AddressValidationRequest(@field:Json(name = "address") val address: String)

