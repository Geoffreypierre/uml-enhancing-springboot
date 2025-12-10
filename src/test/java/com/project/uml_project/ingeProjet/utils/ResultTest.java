package com.project.uml_project.ingeProjet.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void testSuccessCreation() {
        String data = "test data";
        Result<String> result = Result.success(data);

        assertNotNull(result);
        assertTrue(result.succeeded());
        assertFalse(result.failed());
        assertEquals(data, result.value());
        assertNull(result.message());
    }

    @Test
    void testFailureCreation() {
        String errorMessage = "error occurred";
        Result<String> result = Result.failure(errorMessage);

        assertNotNull(result);
        assertFalse(result.succeeded());
        assertTrue(result.failed());
        assertNull(result.value());
        assertEquals(errorMessage, result.message());
    }

    @Test
    void testSuccessWithNullData() {
        Result<String> result = Result.success(null);

        assertTrue(result.succeeded());
        assertFalse(result.failed());
        assertNull(result.value());
    }

    @Test
    void testFailureWithEmptyMessage() {
        Result<String> result = Result.failure("");

        assertFalse(result.succeeded());
        assertTrue(result.failed());
        assertEquals("", result.message());
    }

    @Test
    void testGenericTypes() {
        // Test with Integer
        Result<Integer> intResult = Result.success(42);
        assertEquals(42, intResult.value());

        // Test with custom object
        class TestObject {
            String name;

            TestObject(String name) {
                this.name = name;
            }
        }
        TestObject obj = new TestObject("test");
        Result<TestObject> objResult = Result.success(obj);
        assertEquals("test", objResult.value().name);
    }

    @Test
    void testMultipleFailures() {
        Result<String> result1 = Result.failure("error 1");
        Result<String> result2 = Result.failure("error 2");

        assertNotEquals(result1.message(), result2.message());
        assertTrue(result1.failed());
        assertTrue(result2.failed());
    }
}
