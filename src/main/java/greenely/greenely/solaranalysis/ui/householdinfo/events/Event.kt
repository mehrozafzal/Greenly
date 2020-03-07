package greenely.greenely.solaranalysis.ui.householdinfo.events

import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.solaranalysis.models.HouseholdInfo

sealed class Event {

    data class ShowError(val error: Throwable) : Event()
    data class ShowLoader(val shouldShow: Boolean) : Event()
    data class Done(
            val contactInfo: ContactInfo,
            val householdInfo: HouseholdInfo,
            val analysis: Analysis
    ) : Event()

    object Abort : Event()
}

