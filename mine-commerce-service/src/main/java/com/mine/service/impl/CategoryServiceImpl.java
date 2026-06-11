package com.mine.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mine.common.exception.BusinessException;
import com.mine.mapper.CategoryMapper;
import com.mine.model.entity.Category;
import com.mine.service.CategoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listByParentId(Long parentId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, parentId).orderByAsc(Category::getSort);
        return list(wrapper);
    }

    @Override
    public List<Category> listTree() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        List<Category> all = list(wrapper);
        Map<Long, List<Category>> group = all.stream().collect(Collectors.groupingBy(Category::getParentId));
        return buildTree(0L, group);
    }

    private List<Category> buildTree(Long parentId, Map<Long, List<Category>> group) {
        List<Category> children = group.getOrDefault(parentId, new ArrayList<>());
        for (Category child : children) {
            child.setChildren(buildTree(child.getId(), group));
        }
        return children;
    }

    @Override
    public Category getCategoryById(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    @Override
    public boolean addCategory(Category category) {
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setDeleted(0);
        return save(category);
    }

    @Override
    public boolean updateCategory(Category category) {
        category.setUpdateTime(LocalDateTime.now());
        return updateById(category);
    }

    @Override
    public boolean deleteCategory(Long id) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, id);
        if (count(wrapper) > 0) {
            throw new BusinessException("存在子分类，无法删除");
        }
        return removeById(id);
    }
}
