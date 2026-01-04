package com.polo.Blog.Controller;

import com.polo.Blog.Domain.Entity.Category;
import com.polo.Blog.Service.CategoryService;
import com.polo.Blog.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/hotList")
    public Result<List<Category>> getHotCategoryList(){
        return Result.success(categoryService.getHotCategoryList());
    }
}
