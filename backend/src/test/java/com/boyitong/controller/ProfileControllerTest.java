package com.boyitong.controller;

import com.boyitong.entity.User;
import com.boyitong.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.startsWith;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "ADMIN", "管理员"));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @Test
    void getProfile_ShouldReturnUserWithoutPassword() throws Exception {
        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.displayName").value("管理员"))
                .andExpect(jsonPath("$.data.password").doesNotExist());
    }

    @Test
    void updateProfile_DisplayName_ShouldUpdate() throws Exception {
        mockMvc.perform(put("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"displayName\":\"新名字\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.displayName").value("新名字"));
    }

    @Test
    void updateProfile_EmptyBody_ShouldStillReturnUser() throws Exception {
        mockMvc.perform(put("/api/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void changePassword_WithCorrectOldPassword_ShouldSucceed() throws Exception {
        mockMvc.perform(put("/api/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"admin123\",\"newPassword\":\"newpass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void changePassword_WithWrongOldPassword_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/api/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"wrong\",\"newPassword\":\"newpass123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void changePassword_WithTooShortNewPassword_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/api/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"admin123\",\"newPassword\":\"123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void changePassword_WithMissingFields_ShouldReturn400() throws Exception {
        mockMvc.perform(put("/api/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void getProfile_AfterPasswordChange_NewPasswordShouldWork() throws Exception {
        // Change password
        mockMvc.perform(put("/api/profile/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"oldPassword\":\"admin123\",\"newPassword\":\"newpass123\"}"))
                .andExpect(status().isOk());

        // Verify can login with new password
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"newpass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void uploadAvatar_WithPng_ShouldReturnDataUrl() throws Exception {
        byte[] png = new byte[]{ (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A }; // minimal PNG header
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", png);

        mockMvc.perform(multipart("/api/profile/avatar").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.avatarUrl").isString())
                .andExpect(jsonPath("$.data.avatarUrl").value(startsWith("data:image/png;base64,")));
    }

    @Test
    void uploadAvatar_WithJpeg_ShouldReturnDataUrl() throws Exception {
        byte[] jpg = new byte[]{ (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0 };
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", jpg);

        mockMvc.perform(multipart("/api/profile/avatar").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.avatarUrl").value(startsWith("data:image/jpeg;base64,")));
    }

    @Test
    void uploadAvatar_WithNonImage_ShouldReturn400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "not an image".getBytes());

        mockMvc.perform(multipart("/api/profile/avatar").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void uploadAvatar_LargeFile_ShouldStoreSuccessfully() throws Exception {
        // Create a ~50KB "image"
        byte[] large = new byte[50000];
        large[0] = (byte)0x89; large[1] = 0x50; // PNG header
        MockMultipartFile file = new MockMultipartFile("file", "large.png", "image/png", large);

        mockMvc.perform(multipart("/api/profile/avatar").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.avatarUrl").isString());
    }

    @Test
    void uploadAvatar_UpdatesProfile_ThenGetProfileReturnsUrl() throws Exception {
        byte[] png = new byte[]{ (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
        MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", png);

        mockMvc.perform(multipart("/api/profile/avatar").file(file))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.avatarUrl").isString())
                .andExpect(jsonPath("$.data.avatarUrl").value(org.hamcrest.Matchers.startsWith("data:image/png;base64,")));
    }
}