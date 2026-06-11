package com.mine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.common.exception.BusinessException;
import com.mine.mapper.AdminMapper;
import com.mine.model.entity.Admin;
import com.mine.service.AdminService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public Admin login(String username, String password) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, username);
        Admin admin = getOne(wrapper);
        if (admin == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (!admin.getPassword().equals(password)) {
            throw new BusinessException("用户名或密码错误");
        }
        if (admin.getStatus() != 1) {
            throw new BusinessException("账号已被禁用");
        }
        return admin;
    }

    @Override
    public Admin getAdminById(Long id) {
        Admin admin = getById(id);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }
        return admin;
    }

    @Override
    public IPage<Admin> listAdmins(int pageNum, int pageSize) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Admin::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public boolean addAdmin(Admin admin) {
        LambdaQueryWrapper<Admin> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Admin::getUsername, admin.getUsername());
        if (count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        admin.setCreateTime(LocalDateTime.now());
        admin.setUpdateTime(LocalDateTime.now());
        admin.setDeleted(0);
        return save(admin);
    }

    @Override
    public boolean updateAdmin(Admin admin) {
        admin.setUpdateTime(LocalDateTime.now());
        return updateById(admin);
    }

    @Override
    public boolean deleteAdmin(Long id) {
        return removeById(id);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Admin admin = new Admin();
        admin.setId(id);
        admin.setStatus(status);
        admin.setUpdateTime(LocalDateTime.now());
        return updateById(admin);
    }
}
