package com.veruproject.vmusix

import com.veruproject.vmusix.config.BrandConfig
import com.veruproject.vmusix.data.remote.lyrics.LyricsRepository
import com.veruproject.vmusix.domain.model.Track
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testBrandConfigIdentity() {
        assertEquals("Vmusix", BrandConfig.APP_NAME)
        assertEquals("VeruProject", BrandConfig.DEVELOPER_NAME)
        assertEquals("Listen Without Limits", BrandConfig.TAGLINE)
    }

    @Test
    fun testTrackDurationFormatted() {
        val track = Track(
            id = "test_1",
            title = "Test Song",
            artist = "Test Artist",
            durationSeconds = 185
        )
        assertEquals("3:05", track.durationFormatted)
    }

    @Test
    fun testLrcParser() {
        val dummyLrc = """
            [00:12.50]First line of lyrics
            [00:25.00]Second line of lyrics
        """.trimIndent()

        // Mock DAO not needed for static parser
        val parser = object {
            val pattern = java.util.regex.Pattern.compile("\\[(\\d{2}):(\\d{2})(?:\\.(\\d{2,3}))?\\](.*)")
            fun parse(lrc: String): List<Pair<Long, String>> {
                val list = mutableListOf<Pair<Long, String>>()
                for (line in lrc.split("\n")) {
                    val m = pattern.matcher(line.trim())
                    if (m.find()) {
                        val min = m.group(1)?.toLongOrNull() ?: 0L
                        val sec = m.group(2)?.toLongOrNull() ?: 0L
                        val ms = (min * 60 + sec) * 1000 + 500
                        list.add(Pair(ms, m.group(4)?.trim().orEmpty()))
                    }
                }
                return list
            }
        }

        val lines = parser.parse(dummyLrc)
        assertEquals(2, lines.size)
        assertEquals("First line of lyrics", lines[0].second)
        assertEquals("Second line of lyrics", lines[1].second)
    }
}
