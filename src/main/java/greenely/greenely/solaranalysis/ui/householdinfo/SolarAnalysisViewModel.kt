package greenely.greenely.solaranalysis.ui.householdinfo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.solaranalysis.data.SolarAnalysisRepo
import greenely.greenely.solaranalysis.models.AddressErrors
import greenely.greenely.solaranalysis.models.Analysis
import greenely.greenely.solaranalysis.models.ContactInfo
import greenely.greenely.solaranalysis.models.HouseholdInfo
import greenely.greenely.solaranalysis.ui.householdinfo.events.Event
import greenely.greenely.solaranalysis.ui.householdinfo.validations.AddressValidator
import greenely.greenely.solaranalysis.ui.householdinfo.validations.ContactInfoValidator
import greenely.greenely.utils.NonNullObservableField
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

@OpenClassOnDebug
class SolarAnalysisViewModel @Inject constructor(
        private val repo: SolarAnalysisRepo,
        val addressValidator: AddressValidator,
        val contactInfoValidator: ContactInfoValidator
) : ViewModel() {

    val index = ObservableInt(0)
    val householdInfo = HouseholdInfo()
    val analysis = ObservableField<Analysis>()
    val addressErrors = NonNullObservableField<AddressErrors>(AddressErrors())
    val contactInfo = ContactInfo()
    val validationTextWatcher: TextWatcher = ValidateOnTextChangeListener()

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    val stepperListener: StepperLayout.StepperListener
        get() = object : StepperLayout.StepperListener {
            override fun onStepSelected(newStepPosition: Int) {
                // Nothing to do.
            }

            override fun onError(verificationError: VerificationError?) {
                // Nothing to do.
            }

            override fun onReturn() {
                // Nothing to do.
            }

            override fun onCompleted(completeButton: View?) {

                val analysis = analysis.get()
                        ?: run {
                            Log.e(javaClass.simpleName, "Analysis is null!")
                            return
                        }

                _events.value = Event.Done(contactInfo, householdInfo, analysis)
            }
        }

    val errorModel
        get() = contactInfoValidator.errorModel

    inner class ValidateOnTextChangeListener : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (addressErrors.get().hasErrors()) {
                validateAddressInput()
            } else if (errorModel.hasErrors()) {
                validateContactInfo()
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

    }

    fun validateAddress(callback: (Throwable?) -> Unit) {
        repo.validateAddress(householdInfo)
                .doOnSubscribe { _events.value = Event.ShowLoader(true) }
                .subscribeBy(
                        onComplete = {
                            _events.value = Event.ShowLoader(false)
                            callback(null)
                        },
                        onError = {
                            _events.value = Event.ShowLoader(false)
                            callback(it)
                        }
                )
    }

    fun getAnalysisDetails(): LiveData<Analysis> {
        val analysisResult = MutableLiveData<Analysis>()
        repo.sendHouseholdInfo(householdInfo)
                .doOnSubscribe { _events.value = Event.ShowLoader(true) }
                .subscribeBy(
                        onNext = {
                            _events.value = Event.ShowLoader(false)
                            analysis.set(it)
                            analysisResult.value = it
                        },
                        onError = {
                            _events.value = Event.ShowLoader(false)
                            _events.value = Event.ShowError(it)
                        }
                )
        return analysisResult
    }

    fun validateAddressInput(): AddressErrors {
        val errors = addressValidator.validate(householdInfo)
        addressErrors.set(errors)
        return errors
    }

    fun validateContactInfo() {
        contactInfoValidator.validate(contactInfo)
    }

    fun abort() {
        _events.value = Event.Abort
    }

    fun requestContact() {
        val analysis = analysis.get()
                ?: run {
                    Log.e(javaClass.simpleName, "Analysis is null!")
                    return
                }
        _events.value = Event.Done(contactInfo, householdInfo, analysis)
    }
}

