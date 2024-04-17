package com.baiye959.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baiye959.usercenter.model.domain.User;
import com.baiye959.usercenter.service.UserService;
import com.baiye959.usercenter.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
* @author 33835
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-04-17 18:59:48
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

}




