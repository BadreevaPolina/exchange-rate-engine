package com.example.data.impl

import com.example.data.errors.ErrorHandler
import com.example.data.errors.ErrorMapper
import com.example.data.errors.SuitableItemNotFoundException
import com.example.data.impl.JsonRoutes.RESPONSE_EMPTY_ITEMS_LIST_JSON
import com.example.data.impl.JsonRoutes.RESPONSE_OK_JSON
import com.example.data.impl.JsonRoutes.RESPONSE_SUITABLE_ITEM_NOT_FOUND_JSON
import com.example.domain.interfaces.BybitAPI
import com.example.domain.interfaces.ExchangeRepository
import com.example.domain.models.requests.Fiat
import io.ktor.client.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Before
import org.junit.Test
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.File
import kotlin.test.assertEquals

class BybitExchangeRepositoryImplTest : KoinTest {
    private val exchangeRepository: ExchangeRepository by inject()
    private val projectDir = System.getProperty("user.dir")
    private val testUtils = TestNetworkingUtils()

    private fun startTestKoin(httpStatusCode: HttpStatusCode, responseText: String = "{}") {
        GlobalContext.getOrNull()?.let { stopKoin() }
        startKoin {
            modules(module {
                single<HttpClient> {
                    testUtils.createHttpClient(
                        testUtils.createMockEngine(
                            httpStatusCode,
                            responseText
                        )
                    )
                }
                single { ErrorMapper() }
                single { ErrorHandler(get()) }
                factory<BybitAPI> { BybitAPIImpl(get(), get()) }
                single<ExchangeRepository> { BybitExchangeRepositoryImpl(get(), get()) }
            })
        }
    }

    @Before
    fun before() {
        startTestKoin(HttpStatusCode.OK, File(projectDir + RESPONSE_OK_JSON).readText())
    }

    @Test
    fun `getExchangeRate should return expected price on success`() = runBlocking {
        val expectedPrice = loadExpectedPrice(RESPONSE_OK_JSON)
        val actualPrice = exchangeRepository.getExchangeRate(Fiat.IDR, "1", 1)
        assertEquals(expectedPrice, actualPrice)
    }

    @Test(expected = SuitableItemNotFoundException::class)
    fun `getExchangeRate should throw SuitableItemNotFoundException for empty items list`(): Unit = runBlocking {
        startTestKoin(HttpStatusCode.OK, createMockResponse(RESPONSE_EMPTY_ITEMS_LIST_JSON))
        exchangeRepository.getExchangeRate(Fiat.IDR, "1", 1)
    }

    @Test(expected = SuitableItemNotFoundException::class)
    fun `getExchangeRate should throw SuitableItemNotFoundException`(): Unit = runBlocking {
        startTestKoin(HttpStatusCode.OK, createMockResponse(RESPONSE_SUITABLE_ITEM_NOT_FOUND_JSON))
        exchangeRepository.getExchangeRate(Fiat.IDR, "1", 1)
    }

    @Test(expected = SuitableItemNotFoundException::class)
    fun `getExchangeRate should throw ClientRequestException`(): Unit = runBlocking {
        startTestKoin(HttpStatusCode.BadRequest, createMockResponse(RESPONSE_SUITABLE_ITEM_NOT_FOUND_JSON))
        exchangeRepository.getExchangeRate(Fiat.IDR, "1", 1)
    }

    @Test
    fun executeExchangeShouldReturnExpectedResultOnSuccess(): Unit = runBlocking {
        val expectedToCurrencyPrice = 1.0
        val expectedFromCurrencyPrice = loadExpectedPrice(RESPONSE_OK_JSON)
        val resultExchange = exchangeRepository.executeExchange(Fiat.IDR)
        print(resultExchange)
        assertEquals(expectedFromCurrencyPrice, resultExchange.fromCurrencyPrice)
        assertEquals(expectedToCurrencyPrice, resultExchange.toCurrencyPrice)
        assertEquals("IDR", resultExchange.fromCurrency)
        assertEquals("USDT", resultExchange.toCurrency)
    }

    @Test(expected = SuitableItemNotFoundException::class)
    fun executeExchangeShouldReturnExpectedResultOnFail(): Unit = runBlocking {
        startTestKoin(HttpStatusCode.OK, createMockResponse(RESPONSE_EMPTY_ITEMS_LIST_JSON))
        val resultExchange = exchangeRepository.executeExchange(Fiat.IDR)
        print(resultExchange)
    }


    private fun loadExpectedPrice(jsonFilePath: String): Double {
        val jsonElement = Json.parseToJsonElement(File(projectDir + jsonFilePath).readText())
        val items = jsonElement.jsonObject["result"]?.jsonObject?.get("items")?.jsonArray
        val item = items?.firstOrNull()?.jsonObject
        return item?.get("price")?.jsonPrimitive?.content?.toDouble() ?: error("Price not found in JSON")
    }

    private fun createMockResponse(path: String = ""): String {
        return File(projectDir + path).readText()
    }
}
