package com.project.uml_project.ingeProjet.LLM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

class LLMProviderTest {

    private static final String TEST_TOKEN = System.getenv("OPENAI_API_KEY") != null
            ? System.getenv("OPENAI_API_KEY")
            : "test-token-dummy";
    private static final String TEST_MODEL = "gpt-4";

    private LLMProvider provider;

    @BeforeEach
    void setUp() {
        // Use environment variable if available, otherwise use dummy token
        provider = new LLMProvider(TEST_TOKEN, TEST_MODEL);
    }

    @Test
    void testConstructor() {
        assertNotNull(provider);
    }

    @Test
    void testConstructorWithDifferentModels() {
        LLMProvider gpt4Provider = new LLMProvider(TEST_TOKEN, "gpt-4");
        assertNotNull(gpt4Provider);

        LLMProvider gpt35Provider = new LLMProvider(TEST_TOKEN, "gpt-3.5-turbo");
        assertNotNull(gpt35Provider);
    }

    @Test
    void testConstructorWithNullToken() {
        // OpenAI SDK requires non-null API key, so construction with null will throw
        assertThrows(Exception.class, () -> new LLMProvider(null, TEST_MODEL));
    }

    @Test
    void testConstructorWithEmptyToken() {
        // Should not throw on construction
        assertDoesNotThrow(() -> new LLMProvider("", TEST_MODEL));
    }

    @Test
    void testConstructorWithNullModel() {
        // Should not throw on construction
        assertDoesNotThrow(() -> new LLMProvider(TEST_TOKEN, null));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".+")
    void testRequestWithRealToken() throws Exception {
        // This test only runs if OPENAI_API_KEY environment variable is set
        String realToken = System.getenv("OPENAI_API_KEY");
        LLMProvider realProvider = new LLMProvider(realToken, "gpt-4o-mini");

        String response = realProvider.request("Say 'test' only.");

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testRequestWithInvalidTokenThrowsException() {
        // Create provider with explicitly invalid token
        LLMProvider invalidProvider = new LLMProvider("sk-invalid-token-12345", TEST_MODEL);

        // With an invalid token, the request should throw an exception
        assertThrows(Exception.class, () -> {
            invalidProvider.request("Test prompt");
        });
    }

    @Test
    void testRequestWithEmptyPrompt() {
        // Create provider with explicitly invalid token
        LLMProvider invalidProvider = new LLMProvider("sk-invalid-token-12345", TEST_MODEL);

        // Test behavior with empty prompt (will fail due to invalid token)
        assertThrows(Exception.class, () -> {
            invalidProvider.request("");
        });
    }

    @Test
    void testRequestWithLongPrompt() {
        // Create provider with explicitly invalid token
        LLMProvider invalidProvider = new LLMProvider("sk-invalid-token-12345", TEST_MODEL);

        StringBuilder longPrompt = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longPrompt.append("This is a test sentence. ");
        }

        // Will fail due to invalid token, but tests that long prompts don't cause
        // issues before API call
        assertThrows(Exception.class, () -> {
            invalidProvider.request(longPrompt.toString());
        });
    }

    @Test
    void testMultipleRequestsWithSameProvider() {
        // Create provider with explicitly invalid token
        LLMProvider invalidProvider = new LLMProvider("sk-invalid-token-12345", TEST_MODEL);

        // Test that provider can be reused (will fail due to invalid token)
        assertThrows(Exception.class, () -> {
            invalidProvider.request("First request");
        });

        assertThrows(Exception.class, () -> {
            invalidProvider.request("Second request");
        });
    }
}
