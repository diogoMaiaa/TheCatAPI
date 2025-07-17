package com.example.thecatapi_sword

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.thecatapi_sword.view.MainActivity
import org.junit.Rule
import org.junit.Test

class MainActivityE2ETest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun NavigateTest() {
        composeRule.waitUntil(timeoutMillis = 70000) {
            composeRule.onAllNodesWithTag("loading_indicator").fetchSemanticsNodes().isEmpty()
        }

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTagPrefix("breed_item_").fetchSemanticsNodes().isNotEmpty()
        }

        val itemNode = composeRule.onAllNodesWithTagPrefix("breed_item_")[0]
        itemNode.assertExists()

        val breedTestTag = itemNode.fetchSemanticsNode().config
            .getOrNull(SemanticsProperties.TestTag) ?: ""
        val breedId = breedTestTag.removePrefix("breed_item_")

        itemNode.performClick()

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTagPrefix("breed_image_").fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onAllNodesWithTagPrefix("breed_image_")[0].assertExists()

        composeRule.onNodeWithContentDescription("Voltar").performClick()

        composeRule.waitUntil(timeoutMillis = 70000) {
            composeRule.onAllNodesWithTag("loading_indicator").fetchSemanticsNodes().isEmpty()
        }

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTagPrefix("breed_item_").fetchSemanticsNodes().isNotEmpty()
        }

        val favIcon = composeRule.onNodeWithTag("favourite_icon_$breedId")
        favIcon.assertExists()
        favIcon.performClick()

        composeRule.onAllNodesWithTagPrefix("breed_item_")[0].assertExists()
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
