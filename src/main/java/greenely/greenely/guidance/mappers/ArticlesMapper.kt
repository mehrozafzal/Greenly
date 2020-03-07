package greenely.greenely.guidance.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.guidance.models.Article
import greenely.greenely.guidance.models.json.ArticleJson
import javax.inject.Inject

@OpenClassOnDebug
class ArticlesMapper @Inject constructor() {

    fun fromArticlesJson(json: ArticleJson): Article {
        return Article(
                thumbnailImageUrl = json.thumbnailImageUrl,
                thumbnailTitle = json.thumbnailTitle,
                imageUrl = json.imageUrl,
                title = json.title,
                text = json.text,
                link = json.link,
                label = json.label,
                id = json.id,
                linkText = json.linkText
        )
    }

}