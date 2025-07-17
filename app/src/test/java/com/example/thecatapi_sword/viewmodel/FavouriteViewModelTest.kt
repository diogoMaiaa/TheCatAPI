package com.example.thecatapi_sword.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.thecatapi_sword.model.BreedEntity
import com.example.thecatapi_sword.model.FavouriteBreedEntity
import com.example.thecatapi_sword.model.FavouriteRepository
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class FavouriteViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: FavouriteViewModel
    private lateinit var repository: FavouriteRepository

    private val breedA = BreedEntity(
        id = "a",
        name = "Abyssinian",
        imageUrl = "url1",
        origin = "Egypt",
        reference_image_id = "abc1",
        temperament = "Active",
        description = "Elegant and intelligent breed",
        life_span = "14"
    )

    private val breedB = BreedEntity(
        id = "b",
        name = "Bengal",
        imageUrl = "url2",
        origin = "USA",
        reference_image_id = "abc2",
        temperament = "Energetic",
        description = "Spotted coat",
        life_span = "12"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxed = true)
        val context = mockk<Application>(relaxed = true)

        coEvery { repository.getFavouriteBreeds() } returns listOf(breedA, breedB)

        viewModel = FavouriteViewModel(context, repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testLoadFavouritesUpdatesListAndAverage() = runTest {
        viewModel.loadFavourites()
        testDispatcher.scheduler.advanceUntilIdle()

        val favourites = viewModel.favourites.value
        assertEquals(2, favourites.size)
        assertEquals("Abyssinian", favourites[0].name)

        val expectedAverage = (14 + 12) / 2.0
        assertEquals(expectedAverage, viewModel.averageMinLifeSpan.value)
    }

    @Test
    fun testInsertFavoriteCallsRepository() = runTest {
        val fav = FavouriteBreedEntity(id = "a")

        coEvery { repository.insert(fav) } just Runs
        coEvery { repository.getFavouriteBreeds() } returns listOf(breedA)

        viewModel.insertFavorite(fav)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.insert(fav) }
        assertEquals(true, viewModel.isFavourite.value)
    }

    @Test
    fun testDeleteFavoriteCallsRepository() = runTest {
        val fav = FavouriteBreedEntity(id = "b")

        coEvery { repository.delete(fav) } just Runs
        coEvery { repository.getFavouriteBreeds() } returns emptyList()

        viewModel.deleteFavorite(fav)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { repository.delete(fav) }
        assertEquals(false, viewModel.isFavourite.value)
    }

    @Test
    fun testIsFavoriteReturnsCorrectValue() = runTest {
        coEvery { repository.isFavorite("a") } returns true
        val result = viewModel.isFavorite("a")
        assertEquals(true, result)

        coEvery { repository.isFavorite("a") } returns false
        val result2 = viewModel.isFavorite("a")
        assertEquals(false, result2)
    }
}
