package com.mine.admin.controller;

import com.mine.common.result.Result;
import com.mine.model.entity.Category;
import com.mine.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "后台管理分类接口")
@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @ApiOperation("获取分类列表")
    @GetMapping("/list")
    public Result<List<Category>> listCategories() {
        return Result.success(categoryService.listTree());
    }

    @ApiOperation("获取分类详情")
    @GetMapping("/{id}")
    public Result<Category> getCategory(@PathVariable Long id) {
        return Result.success(categoryService.getCategoryById(id));
    }

    @ApiOperation("添加分类")
    @PostMapping
    public Result<Boolean> addCategory(@RequestBody Category category) {
        return Result.success(categoryService.addCategory(category));
    }

    @ApiOperation("更新分类")
    @PutMapping("/{id}")
    public Result<Boolean> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return Result.success(categoryService.updateCategory(category));
    }

    @ApiOperation("删除分类")
    @DeleteMapping("/{id}")
    public Result<Boolean> deleteCategory(@PathVariable Long id) {
        return Result.success(categoryService.deleteCategory(id));
    }
}
