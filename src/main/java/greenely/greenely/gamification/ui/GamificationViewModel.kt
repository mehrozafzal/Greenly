package greenely.greenely.gamification.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import greenely.greenely.OpenClassOnDebug
import greenely.greenely.competefriend.data.CompeteFriendRepo
import greenely.greenely.competefriend.json.rankListJson.RankResponseJson
import greenely.greenely.competefriend.mappers.RankResponseMapper
import greenely.greenely.competefriend.json.addFriendJson.AddFriendResponseJson
import greenely.greenely.competefriend.json.inviteUserJson.InviteUserResponseJson
import greenely.greenely.extensions.notify
import greenely.greenely.models.Resource
import greenely.greenely.utils.SingleLiveEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject


/**
 * Types of events that might occur.
 */
enum class DeleteFriendEvent {
    /**
     * Close the view.
     */
    FINISH,
    /**
     * Show success dialog.
     */
    SHOW_SUCCESS,
}


@OpenClassOnDebug
class GamificationViewModel @Inject constructor() : ViewModel() {

    private val _errors = SingleLiveEvent<Throwable>()
    val rankResponse = MutableLiveData<Resource<RankResponseJson>>()
    val inviteUserResponse = SingleLiveEvent<Resource<InviteUserResponseJson>>()
    val addFriendResponse = SingleLiveEvent<Resource<AddFriendResponseJson>>()
    val errors: LiveData<Throwable>
        get() {
            return _errors
        }
    /**
     * Stream of events that might occur.
     */
    val events = SingleLiveEvent<DeleteFriendEvent>()
    /**
     * If an error occured.
     */
    val error = MutableLiveData<Throwable>()

    private val disposables = CompositeDisposable()


    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}

