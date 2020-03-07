package greenely.greenely.settings.faq.mappers

import greenely.greenely.OpenClassOnDebug
import greenely.greenely.settings.faq.ui.models.FaqItem
import javax.inject.Inject

@OpenClassOnDebug
class FaqItemMapper @Inject constructor() {
    fun fromJson(json: List<List<String>>): List<FaqItem> {
        return json.mapNotNull { if (it.size == 2) FaqItem(it[0], it[1]) else null }
    }
}

