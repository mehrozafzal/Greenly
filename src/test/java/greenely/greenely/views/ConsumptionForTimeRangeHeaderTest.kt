package greenely.greenely.views

import android.view.ViewGroup
import android.widget.TextView
import com.nhaarman.mockito_kotlin.mock
import greenely.greenely.EmptyActivity
import greenely.greenely.R
import greenely.greenely.TestApplication
import greenely.greenely.di.DaggerTestComponent
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(
        packageName = "greenely.greenely",
        application = TestApplication::class,
        sdk = [21],
        manifest = Config.NONE
)
class ConsumptionForTimeRangeHeaderTest {

    private lateinit var activity: EmptyActivity
    private lateinit var view: ConsumptionForTimeRangeHeader

    @Before
    fun setUp() {
        DaggerTestComponent.builder().viewModelProviderFactory(mock()).build().inject(
                RuntimeEnvironment.application as TestApplication
        )

        activity = Robolectric.setupActivity(EmptyActivity::class.java)

        view = ConsumptionForTimeRangeHeader(activity)
        activity.findViewById<ViewGroup>(R.id.fragment).addView(view)
        view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        view.requestLayout()
    }

    @Test
    fun testSetModel() {
        // Given
        val model: ConsumptionForTimeRangeHeader.Model = object : ConsumptionForTimeRangeHeader.Model {
            override val timeRangeUnit: String
                get() = "Månad"
            override val timeRange: String
                get() = "September"
            override val consumption: String
                get() = "265 kWh"

        }

        // When
        view.model = model

        // Then
        assertThat(view.findViewById<TextView>(R.id.consumption).text).isEqualTo("265 kWh")
        assertThat(view.findViewById<TextView>(R.id.timeRange).text).isEqualTo("September")
        assertThat(view.findViewById<TextView>(R.id.timeRangeUnit).text).isEqualTo("Månad")
    }
}

