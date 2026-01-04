package com.polo.Blog.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polo.Blog.Domain.Entity.Category;
import com.polo.Blog.Mapper.CategoryMapper;
import com.polo.Blog.Service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getHotCategoryList(){
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Category::getCreateTime);
        List<Category> categoryList = this.list(wrapper);
        return categoryList.size() > 10 ? categoryList.subList(0, 11) : categoryList;
    }
}
