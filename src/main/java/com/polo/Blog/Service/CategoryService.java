package com.polo.Blog.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.polo.Blog.Domain.Entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {
    /**
     *  根据创建时间获取前10个分类
     * @return 10个分类
     */
    List<Category> getHotCategoryList();
}
