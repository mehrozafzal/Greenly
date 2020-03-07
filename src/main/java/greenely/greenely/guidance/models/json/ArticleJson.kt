package greenely.greenely.guidance.models.json

import com.squareup.moshi.Json


data class ArticleJson(
        @field:Json(name = "thumbnail_picture") val thumbnailImageUrl: String,
        @field:Json(name = "thumbnail_title") val thumbnailTitle: String,
        @field:Json(name = "picture") val imageUrl: String,
        @field:Json(name = "title") val title: String,
        @field:Json(name = "text") val text: String,
        @field:Json(name = "link") val link: String?,
        @field:Json(name = "label") val label: String,
        @field:Json(name = "id") val id: Int,
        @field:Json(name = "link_text") val linkText: String?
)