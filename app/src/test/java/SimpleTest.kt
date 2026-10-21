import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class Summer {
    fun sum(n1: Int, n2: Int): Int {
        return n1 + n2
    }
}

class SimpleTest {
    @Before
    fun setUp() {
        summer = Summer()
    }

    @Test
    fun summer_OnePlusOne_ReturnsTwo() {
        assertEquals(2, summer.sum(1, 1))
    }

    companion object {
        private lateinit var summer: Summer
    }
}