package com.myth.playpick.ui

import com.myth.playpick.domain.model.FeedbackType
import com.myth.playpick.domain.model.Platform
import com.myth.playpick.domain.model.UserPreferences
import com.myth.playpick.domain.recommend.CandidateSource
import com.myth.playpick.domain.recommend.GetForYouFeedUseCase
import com.myth.playpick.domain.recommend.GetTasteProfileUseCase
import com.myth.playpick.testing.FakeFeedCacheRepository
import com.myth.playpick.testing.FakeFeedbackRepository
import com.myth.playpick.testing.FakeGameRepository
import com.myth.playpick.testing.FakeLibraryRepository
import com.myth.playpick.testing.FakePreferencesRepository
import com.myth.playpick.testing.MainDispatcherRule
import com.myth.playpick.testing.game
import com.myth.playpick.ui.foryou.ForYouViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import java.time.Clock

@OptIn(ExperimentalCoroutinesApi::class)
class ForYouViewModelTest {

    @get:Rule val main = MainDispatcherRule(StandardTestDispatcher())

    @Test
    fun `not for me from another screen re-ranks the feed`() = runTest {
        val rpg = 5
        val rawg = FakeGameRepository().apply { whenQuery({ rpg in it.genreIds }, listOf(game(1, genres = listOf(rpg)), game(2, genres = listOf(rpg)))) }
        val feedback = FakeFeedbackRepository()
        val library = FakeLibraryRepository()
        val prefs = FakePreferencesRepository(UserPreferences(onboardingComplete = true, platforms = setOf(Platform.PC), genreIds = setOf(rpg)))
        val feed = GetForYouFeedUseCase(GetTasteProfileUseCase(feedback, library, prefs), prefs, CandidateSource(rawg), FakeFeedCacheRepository(), Json, Clock.systemUTC())
        val vm = ForYouViewModel(feed, prefs, feedback, library)
        advanceUntilIdle()
        assertEquals(setOf(1, 2), vm.state.value.feed!!.topPicks.map { it.game.id }.toSet())

        // What the details screen does: write straight to the repository, never touching this view model.
        feedback.setFeedback(game(1, genres = listOf(rpg)), FeedbackType.NOT_INTERESTED)
        advanceUntilIdle()

        assertFalse(vm.state.value.feed!!.topPicks.any { it.game.id == 1 })
    }
}
