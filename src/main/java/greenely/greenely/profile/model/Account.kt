package greenely.greenely.profile.model

data class Account(
        var name: String,
        val email: String,
        val imageUrl: String,
        val userID: Int,
        var token: String?,
        var location: String
)