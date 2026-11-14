package io.naira.assistant.presentation.home.main.screen

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import io.naira.assistant.presentation.main.screen.MainScreen
import kotlin.math.abs
import io.naira.assistant.presentation.theme.NairaAssistantTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun homeShowsBrandAndFourNavigationItemsInOrderWithCenteredMicFab() {
        composeRule.setContent {
            NairaAssistantTheme(darkTheme = true) {
                MainScreen()
            }
        }

        composeRule.onNodeWithTag("home-screen-brand").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Settings").assertIsDisplayed()
        composeRule.onNodeWithContentDescription("Notifications").assertIsDisplayed()
        composeRule.onNodeWithContentDescription(
            "Long mic. Voice input is unavailable."
        ).assertIsDisplayed()
        val micBounds = composeRule.onNodeWithTag("home-action-long-mic")
            .fetchSemanticsNode().boundsInRoot
        val navigationBounds = composeRule.onNodeWithTag("home-bottom-navigation")
            .fetchSemanticsNode().boundsInRoot
        val micCenterX = micBounds.center.x
        val screenCenterX = composeRule.onRoot()
            .fetchSemanticsNode().boundsInRoot.width / 2f
        assertTrue("Long mic FAB should be horizontally centered.", abs(micCenterX - screenCenterX) < 1f)
        assertTrue("Long mic FAB should overlap both sides of the navigation bar's top edge.", micBounds.top < navigationBounds.top && micBounds.bottom > navigationBounds.top)
        assertTrue("Long mic FAB should straddle the navigation bar's top edge.", abs(micBounds.center.y - navigationBounds.top) < 1f)
        assertTrue(
            "Long mic FAB should be larger than the default size.",
            micBounds.height > 56f * composeRule.density.density,
        )
        val itemTags = listOf(
            "home-nav-home",
            "home-nav-chat",
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

        composeRule.onNodeWithTag("home-action-long-mic").performClick()
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
