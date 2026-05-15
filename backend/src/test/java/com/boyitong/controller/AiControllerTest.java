package com.boyitong.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void chat_WithEmptyMessage_ShouldReturnErrorCode() throws Exception {
        mockMvc.perform(post("/api/ai/chat").contentType(MediaType.APPLICATION_JSON).content("{\"message\":\"\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void chat_WithMissingMessageField_ShouldReturnErrorCode() throws Exception {
        mockMvc.perform(post("/api/ai/chat").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void chat_WithNullMessage_ShouldReturnErrorCode() throws Exception {
        mockMvc.perform(post("/api/ai/chat").contentType(MediaType.APPLICATION_JSON).content("{\"message\":null}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void generateFollowUp_WithEmptyKeywords_ShouldReturnErrorCode() throws Exception {
        mockMvc.perform(post("/api/ai/generate-followup").contentType(MediaType.APPLICATION_JSON).content("{\"keywords\":\"\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void generateFollowUp_WithMissingKeywords_ShouldReturnErrorCode() throws Exception {
        mockMvc.perform(post("/api/ai/generate-followup").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(400));
    }
}