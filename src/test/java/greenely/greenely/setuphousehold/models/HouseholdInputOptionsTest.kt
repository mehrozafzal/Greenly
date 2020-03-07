package greenely.greenely.setuphousehold.models

import org.junit.Assert
import org.junit.Test

class HouseholdInputOptionsTest{

    @Test
    fun tesInputOptions(){
        val inputOptions = HouseholdInputOptions(1,"option")
        Assert.assertEquals(1, inputOptions.id)
        Assert.assertEquals("option", inputOptions.name)
    }
}
