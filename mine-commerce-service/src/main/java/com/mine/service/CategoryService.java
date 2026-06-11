package com.mine.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mine.model.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> listByParentId(Long parentId);

    List<Category> listTree();

    Category getCategoryById(Long id);

    boolean addCategory(Category category);

    boolean updateCategory(Category category);

    boolean deleteCategory(Long id);
}
