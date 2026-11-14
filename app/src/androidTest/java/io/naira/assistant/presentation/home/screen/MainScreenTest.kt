package io.naira.assistant.presentation.home.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.naira.assistant.presentation.theme.NairaAssistantTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun homeShowsBrandAndFiveBottomNavigationItemsInOrder() {
        composeRule.setContent {
            NairaAssistantTheme(darkTheme = true) {
                MainScreen()
            }
        }

        composeRule.onNodeWithTag("home-screen-brand").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()
        val itemTags = listOf(
            "home-nav-home",
            "home-nav-chat",
            "home-nav-long-mic",
            "home-nav-camera",
            "home-nav-library",
        )
        val centers = itemTags.map { tag ->
            composeRule.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot.center.x
        }

        assertTrue(
            "Bottom navigation items must appear in the required order.",
            centers.zipWithNext().all { (left, right) -> left < right },
        )
    }

    @Test
    fun longMicReportsUnavailableWithoutStartingCapture() {
        composeRule.setContent {
            NairaAssistantTheme(darkTheme = true) {
                MainScreen()
            }
        }

        composeRule.onNodeWithTag("home-nav-long-mic").performClick()
        composeRule.onNodeWithText("Voice input isn't available yet.").assertIsDisplayed()
    }

    @Test
    fun cameraEntryReportsUnavailableInsteadOfOpeningCamera() {
        composeRule.setContent {
            NairaAssistantTheme(darkTheme = true) {
                MainScreen()
            }
        }

        composeRule.onNodeWithTag("home-nav-camera").performClick()
        composeRule.onNodeWithText("Camera isn't available yet.").assertIsDisplayed()
    }

    @Test
    fun settingsActionReportsSettingsScreenIsUnavailable() {
        composeRule.setContent {
            NairaAssistantTheme(darkTheme = true) {
                MainScreen()
            }
        }

        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Settings aren't available yet.").assertIsDisplayed()
    }
}
