package com.samcheok.ecotour.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samcheok.ecotour.domain.TourTheme;
import com.samcheok.ecotour.dto.UserCreateRequest;
import com.samcheok.ecotour.dto.UserResponse;
import com.samcheok.ecotour.exception.DuplicateResourceException;
import com.samcheok.ecotour.exception.ResourceNotFoundException;
import com.samcheok.ecotour.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
@DisplayName("UserController 슬라이스 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("POST 유효 -> 201")
    void create_valid() throws Exception {
        when(userService.create(any())).thenReturn(
                new UserResponse(1L, "a@test.com", "관광객", TourTheme.ECO, 0L));
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequest("a@test.com", "관광객", TourTheme.ECO))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("a@test.com"));
    }

    @Test
    @DisplayName("POST 잘못된 이메일 -> 400")
    void create_invalidEmail() throws Exception {
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequest("not-an-email", "관광객", TourTheme.ECO))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST 중복 이메일 -> 409")
    void create_duplicate() throws Exception {
        when(userService.create(any())).thenThrow(new DuplicateResourceException("중복"));
        mockMvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserCreateRequest("a@test.com", "관광객", TourTheme.ECO))))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET 없음 -> 404")
    void getById_notFound() throws Exception {
        when(userService.getById(99L)).thenThrow(new ResourceNotFoundException("없음"));
        mockMvc.perform(get("/api/users/99")).andExpect(status().isNotFound());
    }
}
