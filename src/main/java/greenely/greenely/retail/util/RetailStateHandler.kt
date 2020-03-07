package greenely.greenely.retail.util

import androidx.lifecycle.MutableLiveData
import androidx.databinding.ObservableBoolean
import greenely.greenely.retail.data.RetailRepo
import greenely.greenely.retail.models.RetailStateResponseModel
import greenely.greenely.store.UserStore
import greenely.greenely.utils.CommonUtils
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetailStateHandler @Inject constructor(val userStore: UserStore, val retailRepo: RetailRepo) {


    var showRetailPromoPromptFlag = SingleLiveEvent<Boolean>()

    var isRetailCustomer = ObservableBoolean()

    var retailStateResponseModel=MutableLiveData<RetailStateResponseModel>()

    var canBecomeRetailCustomer = ObservableBoolean()

    var shouldPromoteRetail = ObservableBoolean()




    fun refreshRetailState() : MutableLiveData<RetailStateResponseModel> {
        retailRepo.getRetailState()
                .subscribeBy(
                        onNext = {
                            retailStateResponseModel.value=it
                            isRetailCustomer.set(it.isRetailCustomer)
                            canBecomeRetailCustomer.set(it.canBecomeRetailCustomer)
                            shouldPromoteRetail.set(it.shouldPromoteRetail())
                            if(it.isRetailCustomer) hideRetailPromoPrompt()
                        },
                        onError = {
                        }
                )
        return retailStateResponseModel
    }




    fun setFibonacci(appSessionCount: Int) {

        if (CommonUtils.isFibonacci(appSessionCount))
            displayRetailPromoPrompt()
        else {
            hideRetailPromoPrompt()
            incrementAppSessionCount()
        }
    }

    fun displayRetailPromoPrompt() {
        showRetailPromoPromptFlag.postValue(true)
    }

    fun hideRetailPromoPrompt() {
        showRetailPromoPromptFlag.postValue(false)
    }

    fun incrementAppSessionCount() {
        userStore.incrementAppSessionCount()
    }


}