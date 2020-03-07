package greenely.greenely.retail.util

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import greenely.greenely.retail.data.RetailRepo
import greenely.greenely.store.UserStore
import io.kotlintest.mock.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class RetailStateHandlerTest {

    @JvmField
    @Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var repo: RetailRepo

    private lateinit var userStore: UserStore

    private lateinit var retailStateHandler: RetailStateHandler


    @Before
    fun setUp() {
        userStore= mock()
        repo= mock()
        retailStateHandler=RetailStateHandler(userStore,repo)
    }


    @Test
    fun testFebonacci() {
        getFibonacciNumbers().forEach {
            retailStateHandler.setFibonacci(it)
            assertEquals(retailStateHandler.showRetailPromoPromptFlag.value,true)

        }
    }

    @Test
    fun testNotFebonacci() {
        getNonFibonacciNumbers().forEach {
            retailStateHandler.setFibonacci(it)
            assertEquals(retailStateHandler.showRetailPromoPromptFlag.value,false)
        }
    }


    private fun getFibonacciNumbers()=mutableListOf<Int>(1,1,2,3,5,8,13,21,34)

    private fun getNonFibonacciNumbers()=mutableListOf<Int>(11,4,6,7,9,12,14,15,16,17,18,19)



}