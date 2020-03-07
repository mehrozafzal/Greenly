package greenely.greenely.competefriend.ui

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
import greenely.greenely.gamification.ui.DeleteFriendEvent
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
class CompeteFriendViewModel @Inject constructor(private val repo: CompeteFriendRepo, private val mapper: RankResponseMapper) : ViewModel() {

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

    fun fetchRankedUserList(resolution: String, rankingMethod: String) {
        disposables.add(
                repo.getRankedUserListResponse(resolution, rankingMethod)
                        .doOnSubscribe {
                            rankResponse.value = Resource.Loading()
                        }
                        .subscribeBy(
                                onNext = {
                                    rankResponse.value = Resource.Success(it)
                                },
                                onError = {
                                    rankResponse.value = Resource.Error(it)
                                }
                        ))
    }

    fun fetchInviteUserResponse() {
        disposables.add(
                repo.getInviteUserResponse()
                        .doOnSubscribe {
                            inviteUserResponse.value = Resource.Loading()
                        }
                        .subscribeBy(
                                onNext = {
                                    inviteUserResponse.value = Resource.Success(it)
                                },
                                onError = {
                                    inviteUserResponse.value = Resource.Error(it)
                                }
                        ))
    }


    fun fetchAddFriendResponse(invitationID: String) {
        disposables.add(
                repo.getAddFriendToListResponse(invitationID)
                        .doOnSubscribe {
                            addFriendResponse.value = Resource.Loading()
                        }
                        .subscribeBy(
                                onNext = {
                                    addFriendResponse.value = Resource.Success(it)
                                },
                                onError = {
                                    addFriendResponse.value = Resource.Error(it)
                                }
                        ))
    }

    /**
     * delete friend for the provided [invitation_id].
     */
    fun deleteFriend(friendID: String) {
        disposables.add(repo.deleteFriend(friendID)
                .subscribeBy(
                        onComplete = {
                            events.notify(DeleteFriendEvent.SHOW_SUCCESS)
                        },
                        onError = {
                            error.notify(it)
                        }
                ))
    }


    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}

