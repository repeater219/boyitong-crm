package com.boyitong.service;

import com.boyitong.repository.ContractRepository;
import com.boyitong.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ContractRepository contractRepository;

    private AiService aiService;

    @BeforeEach
    void setUp() {
        aiService = new AiService(customerRepository, contractRepository);
        // Set API key directly for tests
        aiService.setApiKeyForTest("sk-test-key");
        aiService.setApiUrlForTest("http://127.0.0.1:1"); // Fast connection refused, no timeout
        aiService.setModelForTest("test-model");
    }

    @Test
    void chat_WithEmptyMessage_ShouldHandleGracefully() {
        // This tests that chat doesn't crash with empty input
        // (actual AI call will fail due to fake URL, but shouldn't throw unexpected exceptions)
        String result = aiService.chat("");
        assertTrue(result.startsWith("AI 服务调用失败") || result.startsWith("抱歉"));
    }

    @Test
    void chat_WithNullMessage_ShouldHandleGracefully() {
        String result = aiService.chat(null);
        assertNotNull(result);
    }

    @Test
    void generateFollowUp_WithKeywords_ShouldReturnValidResponse() {
        String result = aiService.generateFollowUp("柳州 商铺转让 张睿");
        // With fake API, it should return error message, not crash
        assertNotNull(result);
        assertFalse(result.isBlank());
    }

    @Test
    void generateFollowUp_WithEmptyKeywords_ShouldHandleGracefully() {
        String result = aiService.generateFollowUp("");
        assertNotNull(result);
    }

    @Test
    void generateFollowUp_WithNullKeywords_ShouldHandleGracefully() {
        String result = aiService.generateFollowUp(null);
        assertNotNull(result);
    }

    @Test
    void callDeepSeek_WithInvalidEndpoint_ShouldReturnErrorMessage() {
        // Using a clearly invalid URL to test error handling
        aiService.setApiUrlForTest("https://nonexistent.example.com/api");
        String result = aiService.chat("hello");
        assertTrue(result.startsWith("AI 服务调用失败") || result.startsWith("抱歉"));
    }

    @Test
    void chat_WhenApiKeyIsEmpty_ShouldHandleGracefully() {
        aiService.setApiKeyForTest("");
        String result = aiService.chat("hello");
        assertTrue(result.startsWith("AI 服务调用失败") || result.startsWith("抱歉"));
    }

    @Test
    void chat_WithLongMessage_ShouldNotCrash() {
        String longMsg = "a".repeat(10000);
        String result = aiService.chat(longMsg);
        assertNotNull(result);
    }

    @Test
    void generateFollowUp_WithChineseKeywords_ShouldHandleCorrectly() {
        String result = aiService.generateFollowUp("北京 餐饮转让 李四 200m²");
        assertNotNull(result);
    }
}