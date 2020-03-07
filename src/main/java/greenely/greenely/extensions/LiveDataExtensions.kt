package greenely.greenely.extensions

import androidx.lifecycle.*
import org.reactivestreams.Publisher

/** Notify a value change. Resets the value once the value has been updated. */
fun <T> MutableLiveData<T>.notify(value: T) {
    this.value = value
    this.value = null
}

/** Converts a [LiveData] object to a [Publisher] object. */
fun <T> LiveData<T>.toPublisher(lifecyclerOwner: LifecycleOwner): Publisher<T> =
        LiveDataReactiveStreams.toPublisher<T>(lifecyclerOwner, this)

/** Converts a [Publisher] object to a [LiveData] object. */
fun <T> Publisher<T>.toLiveData(): LiveData<T> =
        LiveDataReactiveStreams.fromPublisher(this)

