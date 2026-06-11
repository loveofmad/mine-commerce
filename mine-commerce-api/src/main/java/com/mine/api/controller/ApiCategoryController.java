package com.mine.api.controller;

import com.mine.common.result.Result;
import com.mine.model.entity.Category;
import com.mine.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "小程序分类接口")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class ApiCategoryController {

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
}
