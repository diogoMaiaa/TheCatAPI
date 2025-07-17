package com.example.thecatapi_sword

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.thecatapi_sword.view.Favourites
import org.junit.Rule
import org.junit.Test

class FavouriteScreenE2ETest {

    @get:Rule
    val composeRule = createAndroidComposeRule<Favourites>()

    @Test
    fun favouritesScreen_showsItems_andTogglesFavourite() {

        composeRule.waitUntil(timeoutMillis = 15000) {
            composeRule.onAllNodesWithTagPrefix("favourite_item_").fetchSemanticsNodes().isNotEmpty()
        }


        val itemNode = composeRule.onAllNodesWithTagPrefix("favourite_item_")[0]
        itemNode.assertExists()


        val breedId = extractBreedIdFromTag(itemNode)


        composeRule.onNodeWithTag("breed_image_$breedId", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("favourite_name_$breedId").assertExists()

        itemNode.performClick()

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTagPrefix("breed_image_").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithTagPrefix("breed_image_")[0].assertExists()

        composeRule.onNodeWithContentDescription("Voltar").performClick()


        val favIcon = composeRule.onNodeWithTag("favourite_icon_$breedId")
        favIcon.assertExists()
        favIcon.performClick()


        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag("favourite_item_$breedId").fetchSemanticsNodes().isEmpty()
        }



    }


    private fun extractBreedIdFromTag(node: SemanticsNodeInteraction): String {
        val tag = node.fetchSemanticsNode().config.getOrNull(SemanticsProperties.TestTag)
        return tag?.removePrefix("favourite_item_") ?: error("Breed ID Not Found")
    }


    private fun ComposeTestRule.onAllNodesWithTagPrefix(prefix: String): SemanticsNodeInteractionCollection {
        return onAllNodes(hasTestTagStartingWith(prefix))
    }


    private fun hasTestTagStartingWith(prefix: String): SemanticsMatcher {
        return SemanticsMatcher("TestTag starts with $prefix") {
            it.config.getOrNull(SemanticsProperties.TestTag)?.startsWith(prefix) ?: false
        }
    }
}
