package greenely.greenely.guidance.mappers


import greenely.greenely.OpenClassOnDebug
import greenely.greenely.guidance.models.Offer
import greenely.greenely.guidance.models.json.OfferJson
import javax.inject.Inject

@OpenClassOnDebug
class OffersMapper @Inject constructor() {

    fun fromOffersJson(json: OfferJson): Offer {
        return Offer(
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