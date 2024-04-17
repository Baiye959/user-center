package com.baiye959.usercenter;

import com.baiye959.usercenter.mapper.UserMapper;
import com.baiye959.usercenter.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@SpringBootTest(classes = {UserCenterApplication.class})
//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
public class SampleTest {
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
