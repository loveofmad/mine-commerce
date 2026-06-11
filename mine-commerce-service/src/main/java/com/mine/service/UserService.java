package com.mine.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mine.model.entity.User;

public interface UserService extends IService<User> {

    User register(String username, String password, String phone);

    User login(String username, String password);

    User getUserById(Long id);

    User getUserByUsername(String username);

    IPage<User> listUsers(int pageNum, int pageSize);

    boolean updateUser(User user);

    boolean updateStatus(Long id, Integer status);
}
