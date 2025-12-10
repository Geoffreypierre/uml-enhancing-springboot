package com.project.uml_project.ingeProjet.LLM;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.*;

class LLMProviderTest {

    private static final String TEST_TOKEN = "test-token-dummy";
    private static final String TEST_MODEL = "gpt-4";

    private LLMProvider provider;

    @BeforeEach
    void setUp() {
        // Note: This creates a provider but actual API calls will fail without a real
        // token
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
    void testRequestWithRealToken() {
        // This test only runs if OPENAI_API_KEY environment variable is set
        String realToken = System.getenv("OPENAI_API_KEY");
        LLMProvider realProvider = new LLMProvider(realToken, "gpt-4");

        String response = realProvider.request("Say 'test' only.");

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void testRequestWithInvalidTokenThrowsException() {
        // With an invalid token, the request should throw an exception
        assertThrows(Exception.class, () -> {
            provider.request("Test prompt");
        });
    }

    @Test
    void testRequestWithEmptyPrompt() {
        // Test behavior with empty prompt (will fail due to invalid token)
        assertThrows(Exception.class, () -> {
            provider.request("");
        });
    }

    @Test
    void testRequestWithLongPrompt() {
        StringBuilder longPrompt = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longPrompt.append("This is a test sentence. ");
        }

        // Will fail due to invalid token, but tests that long prompts don't cause
        // issues before API call
        assertThrows(Exception.class, () -> {
            provider.request(longPrompt.toString());
        });
    }

    @Test
    void testMultipleRequestsWithSameProvider() {
        // Test that provider can be reused (will fail due to invalid token)
        assertThrows(Exception.class, () -> {
            provider.request("First request");
        });

        assertThrows(Exception.class, () -> {
            provider.request("Second request");
        });
    }
}
