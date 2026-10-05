package com.jmvr.rescatandohuellas

import com.jmvr.rescatandohuellas.state.normalizarUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LandingStateTest {
    @Test
    fun `texto vacio o con espacios en medio no es una url`() {
        assertNull(normalizarUrl(""))
        assertNull(normalizarUrl("   "))
        assertNull(normalizarUrl("rescatando huellas"))
    }

    @Test
    fun `sin esquema se le antepone https`() {
        assertEquals("https://google.com", normalizarUrl("google.com"))
        assertEquals("https://google.com", normalizarUrl("  google.com  "))
    }

    @Test
    fun `con esquema se deja igual`() {
        assertEquals("https://example.com/a?b=1", normalizarUrl("https://example.com/a?b=1"))
        assertEquals("http://192.168.0.10:8080", normalizarUrl("http://192.168.0.10:8080"))
        assertEquals("file:///android_asset/landing.html", normalizarUrl("file:///android_asset/landing.html"))
    }
}
