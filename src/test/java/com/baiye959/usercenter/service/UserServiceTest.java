package com.baiye959.usercenter.service;
import java.util.Date;

import com.baiye959.usercenter.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户服务测试
 *
 * @author Baiye959
 */

@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;
    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("Baiye959");
        user.setUseraccount("959");
        user.setAvatarurl("https://i0.hippopx.com/photos/299/326/436/write-plan-business-startup-preview.jpg");
        user.setGender(1);
        user.setUserpassword("959");
        user.setPhone("959");
        user.setEmail("959@qq.com");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }
}