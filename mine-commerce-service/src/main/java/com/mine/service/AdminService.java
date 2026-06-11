package com.mine.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mine.model.entity.Admin;

public interface AdminService extends IService<Admin> {

    Admin login(String username, String password);

    Admin getAdminById(Long id);

    IPage<Admin> listAdmins(int pageNum, int pageSize);

    boolean addAdmin(Admin admin);

    boolean updateAdmin(Admin admin);

    boolean deleteAdmin(Long id);

    boolean updateStatus(Long id, Integer status);
}
