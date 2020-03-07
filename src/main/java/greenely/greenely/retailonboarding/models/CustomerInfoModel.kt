package greenely.greenely.retailonboarding.models

import androidx.databinding.ObservableBoolean
import greenely.greenely.utils.NonNullObservableField

class CustomerInfoModel(
        val personalNumber: NonNullObservableField<String> = NonNullObservableField(""),
        val address: NonNullObservableField<String> = NonNullObservableField(""),
        val postalCode: NonNullObservableField<String> = NonNullObservableField(""),
        val postalRegion: NonNullObservableField<String> = NonNullObservableField(""),
        val email: NonNullObservableField<String> = NonNullObservableField(""),
        val phoneNumber: NonNullObservableField<String> = NonNullObservableField(""),
        val userTermsAccepted: ObservableBoolean = ObservableBoolean(false),
        val powerOfAttorneyTermsAccepted: ObservableBoolean = ObservableBoolean(false),
        var promocode: String? = null,
        var isFromPoaProcess: ObservableBoolean = ObservableBoolean(false)

        )