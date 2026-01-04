package com.polo.Blog.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polo.Blog.Domain.Entity.Article;
import com.polo.Blog.Domain.Entity.ArticleTag;
import com.polo.Blog.Domain.OV.ArticleVO;
import com.polo.Blog.Mapper.ArticleTagMapper;
import com.polo.Blog.Service.ArticleTagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {



}
