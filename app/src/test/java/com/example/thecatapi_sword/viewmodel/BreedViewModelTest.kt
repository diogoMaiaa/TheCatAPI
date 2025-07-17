package com.example.thecatapi_sword.viewmodel

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.thecatapi_sword.model.BreedEntity
import com.example.thecatapi_sword.model.BreedRepository
import io.mockk.*
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class BreedViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: BreedViewModel
    private val repository = mockk<BreedRepository>(relaxed = true)

    private val fakeList = listOf(
        BreedEntity(
            id = "a",
            name = "Abyssinian",
            imageUrl = "url1",
            origin = "Egypt",
            reference_image_id = "abc1",
            temperament = "Active",
            description = "Elegant and intelligent breed",
            life_span = "14"
        ),
        BreedEntity(
            id = "b",
            name = "Bengal",
            imageUrl = "url2",
            origin = "USA",
            reference_image_id = "abc2",
            temperament = "Energetic and playful",
            description = "Spotted coat like a leopard",
            life_span = "12"
        )
    )

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)

        coEvery { repository.syncBreedsFromApi() } just Runs
        coEvery { repository.getBreedsFromDb() } returns fakeList

        val context = mockk<Application>(relaxed = true)
        viewModel = BreedViewModel(context, repository, preloadImages = false)

        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testBreedsLoadedCorrectly() = runTest {
        assertEquals(2, viewModel.breeds.size)
        assertEquals("Abyssinian", viewModel.breeds[0].name)
    }

    @Test
    fun testPagination() = runTest {
        viewModel.fetchBreeds(0)
        val page = viewModel.getPagedBreeds("")
        assertEquals(2, page.size)
    }

    @Test
    fun testSearchQuery() = runTest {
        viewModel.fetchBreeds(0)
        val result = viewModel.getPagedBreeds("Ben")
        assertEquals(1, result.size)
        assertEquals("Bengal", result[0].name)
    }
}
