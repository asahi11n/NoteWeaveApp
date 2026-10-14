package com.example.noteweaveapp.util.validation

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NoteValidatorTest {

    @Test
    fun testValidTitle_withValidText() {
        assertTrue(NoteValidator.isValidTitle("Sample Title"))
    }

    @Test
    fun testValidTitle_withEmptyString() {
        assertFalse(NoteValidator.isValidTitle(""))
    }

    @Test
    fun testValidTitle_withWhitespaceOnly() {
        assertFalse(NoteValidator.isValidTitle("   "))
    }

    @Test
    fun testValidContent_withValidText() {
        assertTrue(NoteValidator.isValidContent("Sample Content"))
    }

    @Test
    fun testValidContent_withEmptyString() {
        assertFalse(NoteValidator.isValidContent(""))
    }

    @Test
    fun testValidContent_withWhitespaceOnly() {
        assertFalse(NoteValidator.isValidContent("   "))
    }

    @Test
    fun testValidateNote_withValidInput() {
        val result = NoteValidator.validateNote("Title", "Content")
        assertEquals(ValidationResult.Valid, result)
    }

    @Test
    fun testValidateNote_withInvalidTitle() {
        val result = NoteValidator.validateNote("", "Content")
        assertTrue(result is ValidationResult.InvalidTitle)
    }

    @Test
    fun testValidateNote_withInvalidContent() {
        val result = NoteValidator.validateNote("Title", "")
        assertTrue(result is ValidationResult.InvalidContent)
    }

    @Test
    fun testValidateNote_withBothInvalid() {
        val result = NoteValidator.validateNote("", "")
        assertTrue(result is ValidationResult.InvalidTitle)
    }
}
