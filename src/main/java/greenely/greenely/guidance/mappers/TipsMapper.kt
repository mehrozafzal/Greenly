package greenely.greenely.guidance.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.guidance.models.json.TipJson
import greenely.greenely.guidance.models.Tips
import javax.inject.Inject

@OpenClassOnDebug
class TipsMapper @Inject constructor() {

    fun fromTipsJson(json: TipJson): Tips {
        return Tips(
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
