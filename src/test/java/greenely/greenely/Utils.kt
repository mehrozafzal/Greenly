@file:Suppress("KDocMissingDocumentation")

package greenely.greenely

import org.joda.time.DateTimeZone
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.Mockito

fun <T> anyObject(): T {
    @Suppress("DEPRECATION")
    Mockito.anyObject<T>()
    return uninitialized()
}

@Suppress("UNCHECKED_CAST")
fun <T> uninitialized(): T = null as T

class DefaultTimezone : TestWatcher() {

    private val origDefault = DateTimeZone.getDefault()

    override fun starting(description: Description?) {
        DateTimeZone.setDefault(DateTimeZone.UTC)
    }

    override fun finished(description: Description?) {
        DateTimeZone.setDefault(origDefault)
    }
}

