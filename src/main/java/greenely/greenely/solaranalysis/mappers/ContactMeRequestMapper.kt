package greenely.greenely.solaranalysis.mappers

import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.solaranalysis.models.ContactMeRequest
import javax.inject.Inject

class ContactMeRequestMapper @Inject constructor() {
    fun from(
            analysis: Analysis,
            contactInfo: ContactInfo
    ): ContactMeRequest {
        return ContactMeRequest(
                name = contactInfo.name.get(),
                email = contactInfo.email.get(),
                phoneNumber = contactInfo.phoneNumber.get(),
                qualityLead = contactInfo.qualityLead.get(),
                id = analysis.id
        )
    }
}

