package greenely.greenely.solaranalysis.boardcasts

import android.content.Context
import android.content.Intent
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import greenely.greenely.R
import org.junit.Test


class ContactInfoSentBroadcastReceiverTest {
    @Test
    fun testSendBroadcastIntent() {
        // Given
        val context: Context = mock {
            on { getString(R.string.CONTACT_INFO_SENT) } doReturn "CONTACT_INFO_SENT"
        }
        val intent: Intent = mock()

        // When
        ContactInfoSentBroadcastReceiver.sendBroadcastIntent(context, intent)

        // Then
        verify(intent).action = "CONTACT_INFO_SENT"
        verify(context).sendBroadcast(intent)
    }
}
