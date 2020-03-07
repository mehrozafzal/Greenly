package greenely.greenely.retailonboarding.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import android.text.Editable
import android.text.TextWatcher
import greenely.greenely.R
import greenely.greenely.models.Resource
import greenely.greenely.retailinvite.util.PromocodeInputValidator
import greenely.greenely.retail.data.RetailRepo
import greenely.greenely.retailinvite.models.PromoCodeErrorModel
import greenely.greenely.retailinvite.models.PromocodeData
import greenely.greenely.retailonboarding.models.VerifyPromoCodeResponse
import greenely.greenely.retail.ui.events.Event
import greenely.greenely.retailonboarding.data.RetailOnboardingRepo
import greenely.greenely.utils.NonNullObservableField
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class RedeemCodeViewModel @Inject constructor(
        private val inputValidator: PromocodeInputValidator,
        private val repo: RetailOnboardingRepo,
        private val application: Application
) : ViewModel() {


    val promocodeData = PromocodeData()

    val errors = NonNullObservableField<PromoCodeErrorModel>(PromoCodeErrorModel.noErrors())

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events


    private val _verifyPromoCodeResponse = SingleLiveEvent<Resource<VerifyPromoCodeResponse>>()

    val verifyPromoCodeResponse: SingleLiveEvent<Resource<VerifyPromoCodeResponse>>
        get() = _verifyPromoCodeResponse

    val validationTextWatcher: TextWatcher = ValidateOnTextChangeListener()



    fun verifyPromoCode() {

        inputValidator.validate(promocodeData).let {
            errors.set(it)
            if (!it.hasErrors()) {
                        repo.verifyPromoCode(promocodeData.promocode)
                                .doOnSubscribe { _verifyPromoCodeResponse.value = Resource.Loading() }
                                .subscribeBy (
                                        onNext = {
                                            _verifyPromoCodeResponse.value = Resource.Success(it)
                                            if(!it.isValid)
                                                errors.set(PromoCodeErrorModel(application.getString(R.string.invalid_promocode)))
                                        },
                                        onError = {
                                            _verifyPromoCodeResponse.value = Resource.Error(it)
                                        }
                                )

            }
        }


    }




    inner class ValidateOnTextChangeListener : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {

            inputValidator.validate(promocodeData)?.let {
                errors.set(it)
            }

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }
    }



}