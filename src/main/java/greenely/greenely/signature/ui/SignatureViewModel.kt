package greenely.greenely.signature.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import com.stepstone.stepper.VerificationError
import greenely.greenely.BuildConfig
import greenely.greenely.R
import greenely.greenely.errors.exceptions.VerificationException
import greenely.greenely.retail.models.RetailBankIdProcessJson
import greenely.greenely.retail.util.RetailStateHandler
import greenely.greenely.retailonboarding.models.BankIdState
import greenely.greenely.retailonboarding.models.CustomerConversionResponseJson
import greenely.greenely.retailonboarding.models.PoaType
import greenely.greenely.signature.data.SignatureRepo
import greenely.greenely.signature.data.models.InputValidationModel
import greenely.greenely.signature.data.models.ValidationsList
import greenely.greenely.signature.mappers.PreFillDataRequestMapper
import greenely.greenely.signature.mappers.SignatureInputMapper
import greenely.greenely.signature.mappers.SignatureRequestModelMapper
import greenely.greenely.signature.ui.events.Event
import greenely.greenely.signature.ui.models.PreFillInfo
import greenely.greenely.signature.ui.models.SignatureInputErrorModel
import greenely.greenely.signature.ui.models.SignatureInputModel
import greenely.greenely.signature.ui.validation.SignatureInputValidator
import greenely.greenely.utils.NonNullObservableField
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketException
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SignatureViewModel @Inject constructor(
        private val repo: SignatureRepo,
        private val preFillDataRequestMapper: PreFillDataRequestMapper,
        private val inputMapper: SignatureInputMapper,
        private val requestMapper: SignatureRequestModelMapper,
        private val inputValidator: SignatureInputValidator,
        val retailStateHandler: RetailStateHandler
) : ViewModel() {
    private val _errors = SingleLiveEvent<Throwable>()
    val errors: LiveData<Throwable>
        get() = _errors

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events


    var poaType: PoaType = PoaType.NormalPoa

    val signatureInput = SignatureInputModel()
    val signatureErrors = NonNullObservableField<SignatureInputErrorModel>(SignatureInputErrorModel())

    val personalNumberStepValidationTextWatcher: TextWatcher = PersonalNumberStepOnTextChangeListener()
    val addressStepValidationTextWatcher: TextWatcher = AddresStepOnTextChangeListener()

    var checkChangeListener: CompoundButton.OnCheckedChangeListener? = null

    val RETAIL_BANK_ID_STATUS_POLLING_TIMEOUT_MILLIS = 180000

    private val disposables = CompositeDisposable()


    fun preFillFromPersonalNumber(): LiveData<PreFillInfo> {
        val preFillInfo = MutableLiveData<PreFillInfo>()

        val request = preFillDataRequestMapper.fromSignatureInputModel(signatureInput)
        repo.preFillFromPersonalNumber(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            inputMapper.updateFromPreFillResponse(signatureInput, it)
                            preFillInfo.value = PreFillInfo.Success(
                                    true
                            )
                        },
                        onError = {
                            //assume success if personal number is valid but prefill fails
                            preFillInfo.value = PreFillInfo.Success(true)
                        }
                )

        return preFillInfo
    }

    fun exit() {
        _events.value = Event.Exit
    }

    fun menuItemClicked(id: Int): Boolean =
            when (id) {
                R.id.read_poa -> {
                    _events.value = PoaType.fromPoaType(poaType)
                    true
                }
                else -> false
            }

    fun sendSignature(): LiveData<Boolean> {
        val isSuccess = MutableLiveData<Boolean>()

        val request = requestMapper.fromSignatureInput(signatureInput)

        repo.sendSignature(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onComplete = {
                            isSuccess.value = true
                            _events.value = Event.Done
                        },
                        onError = {
                            isSuccess.value = false
                            _errors.value = it
                        }
                )

        return isSuccess
    }


    fun postCustomerInfo(): MutableLiveData<CustomerConversionResponseJson> {
        val convertResponseModel = MutableLiveData<CustomerConversionResponseJson>()
        repo.sendCustomerInfo(requestMapper.fromSignatureInput(signatureInput))
                .subscribeBy(
                        onComplete = {
                            //_events.value = Event.Done
                        },
                        onError = {
                            _errors.value = it
                        },
                        onNext = {
                            convertResponseModel.value = it
                        }
                )
        return convertResponseModel
    }


    fun validateInput(): LiveData<Boolean> {
        val isValidated = MutableLiveData<Boolean>()
        val request = InputValidationModel(ValidationsList(
                signatureInput.personalNumber.get() ?: null))
        repo.validateMeterId(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            if (!it.pNoError.isNullOrEmpty() || !it.meterIdError.isNullOrEmpty()) {
                                val errors = SignatureInputErrorModel(
                                        it.pNoError ?: signatureErrors.get().personalNumberError,
                                        signatureErrors.get().addressError,
                                        signatureErrors.get().postalCodeError,
                                        signatureErrors.get().postalRegionError,
                                        signatureErrors.get().phoneNumberError
                                        )
                                signatureErrors.set(errors)
                                isValidated.value = false
                            } else
                                isValidated.value = true
                        },
                        onComplete = {
                        },
                        onError = {
                            _errors.value = it
                            isValidated.value = false
                        }
                )

        return isValidated
    }

    fun validateAllSteps(): SignatureInputErrorModel {
        val errors = inputValidator.validateAllSteps(signatureInput)
        signatureErrors.set(errors)
        return errors
    }

    fun validatePersonalNumberStep(): SignatureInputErrorModel {
        val errors = inputValidator.validatePersonalNumberStep(signatureInput)
        signatureErrors.set(errors)
        return errors
    }

    fun validateAddressStep(): SignatureInputErrorModel {
        val errors = inputValidator.validateAddressStep(signatureInput)
        signatureErrors.set(errors)
        return errors
    }

    fun validateSigningStep(): SignatureInputErrorModel {
        val errors = inputValidator.validateSigningStep(signatureInput)
        signatureErrors.set(errors)
        return errors
    }

    fun isInputtedPersonalNumberValid(): Boolean {
        val errors = inputValidator.validatePersonalNumberField(signatureInput)
        return errors.personalNumberError.isEmpty() || errors.personalNumberError.isBlank()
    }

    fun fetchRetailState() = retailStateHandler.refreshRetailState()


    inner class PersonalNumberStepOnTextChangeListener : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            if (editable.isNullOrEmpty() || editable.isNullOrBlank()) {
                return
            }
            if (signatureErrors.get().hasPersonalNumberStepErrors()) {
                validatePersonalNumberStep()
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }
    }

    inner class AddresStepOnTextChangeListener : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            if (editable.isNullOrEmpty() || editable.isNullOrBlank()) {
                return
            }
            if (signatureErrors.get().hasAddressStepErrors()) {
                validateAddressStep()
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }
    }

    fun cancelDisposables() {
        disposables.clear()
    }


    fun cancelExistingRetailBankIDProcess(bankidOrderRef: String?): MutableLiveData<RetailBankIdProcessJson> {
        val retailBankIdProcessJson = MutableLiveData<RetailBankIdProcessJson>()
        repo.cancelExistingBankIdProcess(bankidOrderRef)
                .subscribeBy(
                        onNext = {
                            retailBankIdProcessJson.value = it
                        },
                        onError = {
                            _errors.value = it
                        }
                )
        return retailBankIdProcessJson
    }


    fun onRetailToggleClicked(checked: Boolean) {
        if (checked) poaType = PoaType.CombinedPOA
        else poaType = PoaType.NormalPoa
    }

    fun getBankIdProcess(bankidOrderRef: String?, pollingStartedTimestamp: Long, isCancelled: Boolean): MutableLiveData<RetailBankIdProcessJson> {
        val retailBankIdProcessModel = MutableLiveData<RetailBankIdProcessJson>()
        val timeoutTimestamp = pollingStartedTimestamp + RETAIL_BANK_ID_STATUS_POLLING_TIMEOUT_MILLIS
        disposables.add(repo.getBankIdProcessStatus(bankidOrderRef)
                .retry(5)
                .repeatWhen { completed -> completed.delay(1, TimeUnit.SECONDS) }
                .takeUntil {
                    !retailBankIdProcessModel.value?.bankIdStatus.equals(BankIdState.PENDING) ||
                            System.currentTimeMillis() > timeoutTimestamp || isCancelled
                }
                .subscribeBy(
                        onNext = {
                            retailBankIdProcessModel.value = it
                        },
                        onError = {
                            it.printStackTrace()
                            if (it !is InterruptedIOException && it !is IOException && it !is SocketException) {
                                _errors.value = it
                            } else {
                                if (BuildConfig.DEBUG) {
                                    Log.d("unexpectedError", it.toString())
                                }
                            }
                        },
                        onComplete = {
                            if (!retailBankIdProcessModel.value?.bankIdStatus.equals(BankIdState.COMPLETED)) {
                                retailBankIdProcessModel.value = RetailBankIdProcessJson(BankIdState.TIMEOUT, "", "")
                            }
                        }
                ))
        return retailBankIdProcessModel
    }


    fun setBankIdVerficationDone() {
        _events.value = Event.Done
    }

    fun postVerificationError(message: String) {
        _errors.value=VerificationException(VerificationError(message))
    }
}

