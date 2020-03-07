package greenely.greenely.setuphousehold.mappers

import greenely.greenely.setuphousehold.models.HouseholdInputOptions
import org.assertj.core.api.Assertions.*
import org.junit.Before
import org.junit.Test

class HouseholdInputOptionsMapperTest {

    lateinit var mapper: HouseholdInputOptionsMapper

    @Before
    fun setup(){
        mapper = HouseholdInputOptionsMapper()
    }

    @Test
    fun testToJson(){

        //Given
        val expected = listOf(1,"option")

        val inputOption = HouseholdInputOptions(
                id = 1,
                name = "option"
        )

        //when
        val json = mapper.inputOptionToJson(inputOption)

        //then
        assertThat(json).isEqualTo(expected)
    }

    @Test
    fun testFromJson(){

        //Given
        val expected = HouseholdInputOptions(
                id = 1,
                name = "option"
        )

        val json = listOf(1.0,"option")

        //when
        val option = mapper.inputOptionFromJson(json)

        //then
        assertThat(option).isEqualTo(expected)
    }
}
