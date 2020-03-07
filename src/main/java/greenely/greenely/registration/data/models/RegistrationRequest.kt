package greenely.greenely.registration.data.models

import com.squareup.moshi.Json

data class RegistrationRequest(
        @field:Json(name = "email") val email: String,
        @field:Json(name = "password") val password: String
)

