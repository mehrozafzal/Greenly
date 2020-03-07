package greenely.greenely.retailonboarding.ui

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import greenely.greenely.BuildConfig
import greenely.greenely.R
import greenely.greenely.retail.models.RetailBankIdProcessJson
import greenely.greenely.retail.models.RetailOnboardConfig
import greenely.greenely.retailonboarding.data.RetailOnboardingRepo
import greenely.greenely.retailonboarding.models.BankIdState
import greenely.greenely.retailonboarding.models.CustomerConversionResponseJson
import greenely.greenely.retailonboarding.models.CustomerInfoErrorModel
import greenely.greenely.retailonboarding.models.CustomerInfoModel
import greenely.greenely.retailonboarding.ui.events.Event
import greenely.greenely.retailonboarding.ui.util.CombinedPOAFlow
import greenely.greenely.retailonboarding.ui.validations.CustomerInfoValidator
import greenely.greenely.signature.ui.models.PreFillInfo
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

class RetailOnboardingViewModel @Inject constructor(
        private val context: Application,
        private val inputValidator: CustomerInfoValidator,
        private val repo: RetailOnboardingRepo
) : ViewModel() {

    private val _errors = SingleLiveEvent<Throwable>()
    val errors: LiveData<Throwable>
        get() = _errors

    private val _events = SingleLiveEvent<Event>()
    val events: LiveData<Event>
        get() = _events

    val customerInfoInput = CustomerInfoModel()
    val customerInfoErrors = NonNullObservableField<CustomerInfoErrorModel>(CustomerInfoErrorModel())
    val validationTextWatcher: TextWatcher = ValidateOnTextChangeListener()

    val RETAIL_BANK_ID_STATUS_POLLING_TIMEOUT_MILLIS = 180000

    private val disposables = CompositeDisposable()

     var isCombinedPOADone=false

    inner class ValidateOnTextChangeListener : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            if (customerInfoErrors.get().hasPersonalNumberError()) {
                validatePersonalNumberStep()
            } else if (customerInfoErrors.get().hasAddressStepErrors()) {
                validateAddressStep()
            } else if (customerInfoErrors.get().hasContactInfoErrors()) {
                validateContactInformation()
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // Do nothing
        }

    }

    fun validatePersonalNumberStep(): CustomerInfoErrorModel {
        val errors = inputValidator.validatePersonalNumberStep(customerInfoInput)
        customerInfoErrors.set(errors)
        return errors
    }

    fun validateAddressStep(): CustomerInfoErrorModel {
        val errors = inputValidator.validateAddressStep(customerInfoInput)
        customerInfoErrors.set(errors)
        return errors
    }

    fun validateContactInformation(): CustomerInfoErrorModel {
        val errors = inputValidator.validateContactInformationStep(customerInfoInput)
        customerInfoErrors.set(errors)
        return errors
    }


    fun postCustomerInfo(promocode: String?): MutableLiveData<CustomerConversionResponseJson> {
        val convertResponseModel = MutableLiveData<CustomerConversionResponseJson>()
        customerInfoInput.promocode = promocode
        repo.sendCustomerInfo(customerInfoInput)
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

    //Make GET requests until new state is received or RETAIL_BANK_ID_STATUS_POLLING_TIMEOUT_MILLIS  timeout is reached
    fun getRetailBankIdProcess(bankidOrderRef: String?, pollingStartedTimestamp: Long, isCancelled: Boolean): MutableLiveData<RetailBankIdProcessJson> {
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
                                retailBankIdProcessModel.value = RetailBankIdProcessJson(BankIdState.TIMEOUT, "","")
                            }
                        }
                ))
        return retailBankIdProcessModel
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

    fun preFillFromPersonalNumber(personalNumber: String): LiveData<PreFillInfo> {
        val preFillInfo = MutableLiveData<PreFillInfo>()

        val request = personalNumber
        repo.preFillFromPersonalNumber(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = {
                            customerInfoInput.address.set(it.adress)
                            customerInfoInput.postalCode.set(it.postalCode)
                            customerInfoInput.postalRegion.set(it.postalRegion)
                        }
                )

        return preFillInfo
    }

    fun isInputtedPersonalNumberValid(): Boolean {
        val errors = inputValidator.validatePersonalNumberField(customerInfoInput)
        return errors.personalNumberError.isEmpty() || errors.personalNumberError.isBlank()
    }

    fun getPoaUrl(onboardConfig: RetailOnboardConfig?): String {
        if (isCombinedPoaProcess(onboardConfig)) {
            return context.getString(R.string.api_base) + context.getString(R.string.combined_poa_path)
        } else {
            return context.getString(R.string.api_base) + context.getString(R.string.retail_poa_path)
        }
    }


    fun isCombinedPoaProcess(onboardConfig: RetailOnboardConfig?): Boolean {
        return onboardConfig?.let {
            it.isCombinedPoaProcess()
        } ?: false
    }


}

