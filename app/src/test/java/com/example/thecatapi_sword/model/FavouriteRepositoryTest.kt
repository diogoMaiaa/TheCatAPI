package com.example.thecatapi_sword.model

import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteRepositoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FavouriteRepository
    private val mockDao = mockk<FavouriteBreedDao>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FavouriteRepository(mockDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insert should call dao insert`() = runTest {
        val entity = FavouriteBreedEntity("abc")
        coEvery { mockDao.insert(entity) } just Runs

        repository.insert(entity)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { mockDao.insert(entity) }
    }

    @Test
    fun `delete should call dao delete`() = runTest {
        val entity = FavouriteBreedEntity("abc")
        coEvery { mockDao.delete(entity) } just Runs

        repository.delete(entity)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { mockDao.delete(entity) }
    }

    @Test
    fun `isFavorite should return true if dao returns true`() = runTest {
        coEvery { mockDao.isFavorite("abc") } returns true

        val result = repository.isFavorite("abc")
        assertTrue(result)
    }

    @Test
    fun `isFavorite should return false if dao returns false`() = runTest {
        coEvery { mockDao.isFavorite("abc") } returns false

        val result = repository.isFavorite("abc")
        assertFalse(result)
    }

    @Test
    fun `getFavouriteBreeds should return list from dao`() = runTest {
        val breeds = listOf(
            BreedEntity("abc", "Breed1", "img1", "USA", "Calm", "desc", "url"),
            BreedEntity("def", "Breed2", "img2", "UK", "Energetic", "desc2", "url2")
        )
        coEvery { mockDao.getFavouriteBreeds() } returns breeds

        val result = repository.getFavouriteBreeds()
        assertEquals(2, result.size)
        assertEquals("Breed1", result[0].name)
        assertEquals("Breed2", result[1].name)
    }
}
