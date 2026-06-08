package com.rainbowforest.userservice.service;

import com.rainbowforest.userservice.entity.User;
import com.rainbowforest.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class UserServiceTests {

    private final Long USER_ID = 2L;
    private final String USER_NAME = "test";
    private User user;
    private List<User> userList;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp(){
        user = new User();
        user.setId(USER_ID);
        user.setUserName(USER_NAME);
        userList = new ArrayList<>();
        userList.add(user);
    }

    @Test
    public void get_all_users_test(){

        when(userRepository.findAll()).thenReturn(userList);

        List<User> foundUsers = userService.getAllUsers();

        assertEquals(USER_NAME, foundUsers.get(0).getUserName());
        Mockito.verify(userRepository, Mockito.times(1)).findAll();
    }

    @Test
    public void get_user_by_id_test(){

        when(userRepository.getReferenceById(anyLong())).thenReturn(user);

        User foundUser = userService.getUserById(USER_ID);

        assertEquals(USER_NAME, foundUser.getUserName());
        Mockito.verify(userRepository, Mockito.times(1)).getReferenceById(anyLong());
    }

    @Test
    public void get_user_by_name_test(){

        when(userRepository.findByUserName(anyString())).thenReturn(user);

        User foundUser = userService.getUserByName(USER_NAME);

        assertEquals(USER_ID, foundUser.getId());
        Mockito.verify(userRepository, Mockito.times(1)).findByUserName(USER_NAME);
    }
}