package com.baiye959.usercenter;

import com.baiye959.usercenter.mapper.UserMapper;
import com.baiye959.usercenter.model.User;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class UserCenterApplicationTests {

//    @Test
//    void contextLoads() {
//    }

    @Resource
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assertions.assertEquals(0, userList.size());
        userList.forEach(System.out::println);
    }
}
