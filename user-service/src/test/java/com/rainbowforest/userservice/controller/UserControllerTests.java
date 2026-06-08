package com.rainbowforest.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainbowforest.userservice.entity.User;
import com.rainbowforest.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTests {


private final Long USER_ID = 2L;
private final String USER_NAME = "test";

@Autowired
private MockMvc mockMvc;

@MockBean
private UserService userService;

@Test
public void get_all_users_controller_should_return200_when_validRequest() throws Exception {

    User user = new User();
    user.setId(USER_ID);
    user.setUserName(USER_NAME);

    List<User> users = new ArrayList<>();
    users.add(user);

    when(userService.getAllUsers()).thenReturn(users);

    mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(USER_ID))
            .andExpect(jsonPath("$[0].userName").value(USER_NAME));

    verify(userService, times(1)).getAllUsers();
}

@Test
public void get_all_users_controller_should_return404_when_userList_isEmpty() throws Exception {

    List<User> users = new ArrayList<>();

    when(userService.getAllUsers()).thenReturn(users);

    mockMvc.perform(get("/users"))
            .andExpect(status().isNotFound());

    verify(userService, times(1)).getAllUsers();
}

@Test
public void get_user_by_name_controller_should_return200_when_users_isExist() throws Exception {

    User user = new User();
    user.setId(USER_ID);
    user.setUserName(USER_NAME);

    when(userService.getUserByName(USER_NAME)).thenReturn(user);

    mockMvc.perform(get("/users").param("name", USER_NAME))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(USER_ID))
            .andExpect(jsonPath("$.userName").value(USER_NAME));

    verify(userService, times(1)).getUserByName(anyString());
}

@Test
public void get_user_by_name_controller_should_return404_when_users_is_notExist() throws Exception {

    when(userService.getUserByName(USER_NAME)).thenReturn(null);

    mockMvc.perform(get("/users").param("name", USER_NAME))
            .andExpect(status().isNotFound());

    verify(userService, times(1)).getUserByName(anyString());
}

@Test
public void get_user_by_id_controller_should_return200_when_users_isExist() throws Exception {

    User user = new User();
    user.setId(USER_ID);
    user.setUserName(USER_NAME);

    when(userService.getUserById(USER_ID)).thenReturn(user);

    mockMvc.perform(get("/users/{id}", USER_ID))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(USER_ID))
            .andExpect(jsonPath("$.userName").value(USER_NAME));

    verify(userService, times(1)).getUserById(anyLong());
}

@Test
public void get_user_by_id_controller_should_return404_when_users_is_notExist() throws Exception {

    when(userService.getUserById(USER_ID)).thenReturn(null);

    mockMvc.perform(get("/users/{id}", USER_ID))
            .andExpect(status().isNotFound());

    verify(userService, times(1)).getUserById(anyLong());
}

@Test
public void add_user_controller_should_return201_when_user_is_saved() throws Exception {

    User user = new User();
    user.setUserName(USER_NAME);

    ObjectMapper mapper = new ObjectMapper();
    String requestJson = mapper.writeValueAsString(user);

    when(userService.saveUser(any(User.class))).thenReturn(user);

    mockMvc.perform(post("/users")
            .content(requestJson)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.userName").value(USER_NAME));

    verify(userService, times(1)).saveUser(any(User.class));
}

@Test
public void add_user_controller_should_return400_when_user_isNull() throws Exception {

    mockMvc.perform(post("/users")
            .content("null")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
}


}
