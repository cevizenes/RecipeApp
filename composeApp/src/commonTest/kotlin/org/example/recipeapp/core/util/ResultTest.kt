package org.example.recipeapp.core.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ResultTest {

    @Test
    fun `Success result should contain data`() {
        val data = "test data"
        val result = Result.Success(data)

        assertTrue(result.isSuccess)
        assertFalse(result.isLoading)
        assertFalse(result.isError)
        assertEquals(data, result.getOrNull())
        assertNull(result.exceptionOrNull())
    }

    @Test
    fun `Error result should contain exception and message`() {
        val exception = RuntimeException("Test error")
        val message = "Custom error message"
        val result = Result.Error(exception, message)

        assertTrue(result.isError)
        assertFalse(result.isLoading)
        assertFalse(result.isSuccess)
        assertNull(result.getOrNull())
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `Loading result should be in loading state`() {
        val result = Result.Loading

        assertTrue(result.isLoading)
        assertFalse(result.isSuccess)
        assertFalse(result.isError)
        assertNull(result.getOrNull())
        assertNull(result.exceptionOrNull())
    }

    @Test
    fun `onSuccess should execute action when result is Success`() {
        val data = "test data"
        val result = Result.Success(data)
        var executed = false
        var receivedData: String? = null

        result.onSuccess {
            executed = true
            receivedData = it
        }

        assertTrue(executed)
        assertEquals(data, receivedData)
    }

    @Test
    fun `onSuccess should not execute action when result is Error`() {
        val result = Result.Error(RuntimeException("Test"), "Error")
        var executed = false

        result.onSuccess {
            executed = true
        }

        assertFalse(executed)
    }

    @Test
    fun `onError should execute action when result is Error`() {
        val exception = RuntimeException("Test error")
        val message = "Error message"
        val result = Result.Error(exception, message)
        var executed = false
        var receivedException: Throwable? = null
        var receivedMessage: String? = null

        result.onError { ex, msg ->
            executed = true
            receivedException = ex
            receivedMessage = msg
        }

        assertTrue(executed)
        assertEquals(exception, receivedException)
        assertEquals(message, receivedMessage)
    }

    @Test
    fun `onError should not execute action when result is Success`() {
        val result = Result.Success("data")
        var executed = false

        result.onError { _, _ ->
            executed = true
        }

        assertFalse(executed)
    }

    @Test
    fun `onLoading should execute action when result is Loading`() {
        val result = Result.Loading
        var executed = false

        result.onLoading {
            executed = true
        }

        assertTrue(executed)
    }

    @Test
    fun `onLoading should not execute action when result is Success`() {
        val result = Result.Success("data")
        var executed = false

        result.onLoading {
            executed = true
        }

        assertFalse(executed)
    }

    @Test
    fun `chain onSuccess onError onLoading should maintain result`() {
        val data = "test"
        val result = Result.Success(data)
        var successCalled = false
        var errorCalled = false
        var loadingCalled = false

        val chainedResult = result
            .onSuccess { successCalled = true }
            .onError { _, _ -> errorCalled = true }
            .onLoading { loadingCalled = true }

        assertTrue(successCalled)
        assertFalse(errorCalled)
        assertFalse(loadingCalled)
        assertEquals(result, chainedResult)
    }
}

