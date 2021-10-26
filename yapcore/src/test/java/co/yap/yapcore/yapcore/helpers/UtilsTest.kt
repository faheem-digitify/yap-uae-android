package co.yap.yapcore.yapcore.helpers

import co.yap.yapcore.helpers.Utils
import co.yap.yapcore.yapcore.base.BaseTestCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

class UtilsTest : BaseTestCase() {

    @Mock
    lateinit var sut: Utils

    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun test_encoded_string_with_emoji_is_decoding() {
        val stringWithEmojiEncoded = "Faheem \ud83e\udd15 in"
        val actualResult = sut.getStringWithEmojiSupport(stringWithEmojiEncoded)
        Assert.assertEquals(stringWithEmojiEncoded,actualResult)
    }

}