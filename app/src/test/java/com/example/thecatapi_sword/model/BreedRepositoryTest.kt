package com.example.thecatapi_sword.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class BreedRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: BreedRepository

    private val mockApi = mockk<BreedClient>()
    private val mockDao = mockk<BreedDao>()
    private val mockContext = mockk<Context>(relaxed = true)
    private val mockConnectivityManager = mockk<ConnectivityManager>(relaxed = true)
    private val mockNetworkCapabilities = mockk<NetworkCapabilities>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { mockContext.getSystemService(Context.CONNECTIVITY_SERVICE) } returns mockConnectivityManager
        every { mockConnectivityManager.activeNetwork } returns mockk()
        every { mockConnectivityManager.getNetworkCapabilities(any()) } returns mockNetworkCapabilities
        every { mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true

        repository = BreedRepository(mockApi, mockDao, mockContext)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `syncBreedsFromApi should insert breeds when API call is successful`() = runTest {
        val apiResponse = listOf(
            Breed("abys", "Abyssinian", "abc", "Egypt", "Active", "Desc", "14")
        )
        coEvery { mockApi.getAllBreeds(any(), any(), any()) } returns Response.success(apiResponse)
        coEvery { mockApi.getImageById("abc") } returns Response.success(ImageData("abc", "https://url.com/img.jpg"))


        repository.syncBreedsFromApi()


        testDispatcher.scheduler.advanceUntilIdle()


        coEvery { mockDao.insertAll(any()) } just Runs
        coEvery { mockDao.clearAll() } just Runs
    }


    @Test
    fun `syncBreedsFromApi should do nothing when offline`() = runTest {
        every { mockNetworkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns false

        repository.syncBreedsFromApi()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { mockApi.getAllBreeds(any(), any(), any()) }
        coVerify(exactly = 0) { mockDao.insertAll(any()) }
    }

    @Test
    fun `getBreedsFromDb returns data from DAO`() = runTest {
        val fakeList = listOf(BreedEntity("a", "Abyssinian", "abc", "Egypt", "Calm", "desc", "img"))
        coEvery { mockDao.getAll() } returns fakeList

        val result = repository.getBreedsFromDb()
        assertEquals(1, result.size)
        assertEquals("Abyssinian", result[0].name)
    }

    @Test
    fun `getBreedFromDbById returns correct item`() = runTest {
        val breed = BreedEntity("b", "Bengal", "xyz", "USA", "Playful", "desc", "img")
        coEvery { mockDao.getById("b") } returns breed

        val result = repository.getBreedFromDbById("b")
        assertEquals("Bengal", result?.name)
    }

    @Test
    fun `getImageUrl returns url on success`() = runTest {
        coEvery { mockApi.getImageById("img123") } returns Response.success(ImageData("img123", "http://image.url"))

        val result = repository.getImageUrl("img123")
        assertEquals("http://image.url", result)
    }

    @Test
    fun `getImageUrl returns null on failure`() = runTest {
        coEvery { mockApi.getImageById("img123") } returns Response.error(404, mockk(relaxed = true))

        val result = repository.getImageUrl("img123")
        assertNull(result)
    }
}

