package greenely.greenely.signature.ui.models

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import android.graphics.Bitmap
import greenely.greenely.utils.NonNullObservableField

data class SignatureInputModel(
        val personalNumber: NonNullObservableField<String> = NonNullObservableField(""),
        val firstName: NonNullObservableField<String> = NonNullObservableField(""),
        val lastName: NonNullObservableField<String> = NonNullObservableField(""),
        val address: NonNullObservableField<String> = NonNullObservableField(""),
        val postalCode: NonNullObservableField<String> = NonNullObservableField(""),
        val postalRegion: NonNullObservableField<String> = NonNullObservableField(""),
        val phoneNumber: NonNullObservableField<String> = NonNullObservableField(""),
        val signUpForRetail: ObservableBoolean = ObservableBoolean(false),
        val poaAgreementRead: ObservableBoolean = ObservableBoolean(false)
)