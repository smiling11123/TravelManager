package com.polo.Blog.Service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polo.Blog.Domain.DTO.ArticleDTO;
import com.polo.Blog.Domain.Entity.*;
import com.polo.Blog.Domain.OV.ArticleVO;
import com.polo.Blog.Mapper.ArticleMapper;
import com.polo.Blog.Service.*;
import com.polo.Blog.Utils.EntityListToVOList;
import com.polo.Blog.Utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    private ArticleTagService articleTagService;
    @Autowired
    private UserService userService;
    @Autowired
    private SearchHistoryService searchHistoryService;
    @Autowired
    private CategoryService categoryService;
    @Override
    public List<ArticleVO> getArticleList() {
        UserContext.LoginUser loginUser = UserContext.get();

        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        //不是管理员就过滤草稿和已删除的文章
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) {
            wrapper.eq(Article::getStatus, 1).eq(Article::getIsDeleted, 0);
        }
        //按更新时间排序
        wrapper.orderByDesc(Article::getUpdateTime);
        List<Article> articles = this.list(wrapper);
        return EntityListToVOList.articleListToVOList(articles, userService);
    }

    @Override
    public ArticleVO getArticleById(Long id) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getId, id);
        Article article = this.getOne(wrapper);
        long viewCount = article.getViewCount();
        article.setViewCount(viewCount + 1);
        this.updateById(article);
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        //获取作者名
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getId, article.getCreateBy());
        User user = userService.getOne(userWrapper);
        articleVO.setAuth(user.getUsername());
        //分类
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(Category::getId, articleVO.getCategoryId());
        Category category = categoryService.getOne(categoryWrapper);
        articleVO.setCategoryName(category.getName());

        return articleVO;
    }

    @Override
    public List<ArticleVO> getArticleByKeyWord(String keyWord, List<Long> categoryId){
        UserContext.LoginUser loginUser = UserContext.get();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if(keyWord != null && !keyWord.isEmpty()){
            wrapper.like(Article::getTitle, keyWord);
        }

        if(!Objects.equals(loginUser.getRoleKey(), "admin")) {
            wrapper.eq(Article::getStatus, 1).eq(Article::getIsDeleted, 0);
        }
        if(categoryId != null && !categoryId.isEmpty()){
            wrapper.in(Article::getCategoryId, categoryId);
        }
        List<Article> articles = this.list(wrapper);
        //记录搜索记录
        if(keyWord != null && !keyWord.isEmpty()) {
            LambdaQueryWrapper<SearchHistory> searchHistoryWrapper = new LambdaQueryWrapper<>();
            searchHistoryWrapper.eq(SearchHistory::getKeyword, keyWord);
            //查询是否已有记录
            SearchHistory searchHistory = searchHistoryService.getOne(searchHistoryWrapper);
            SearchHistory searchHistoryNew = new SearchHistory();
            if (searchHistory == null) {
                searchHistoryNew.setKeyword(keyWord);
                searchHistoryNew.setCreateTime(LocalDateTime.now());
                searchHistoryNew.setSearchTimes(1);
            }
            if (searchHistory != null) {
                //搜索量加 1
                searchHistory.setSearchTimes(searchHistory.getSearchTimes() + 1);
            }
            searchHistoryService.save(searchHistoryNew);
        }
        return EntityListToVOList.articleListToVOList(articles, userService);
    }
    @Override
    public List<ArticleVO> getMyArticleByKeyWord(String keyWord){
        UserContext.LoginUser loginUser = UserContext.get();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Article::getTitle, keyWord);
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) {
            //普通用户直接返回空
            if(Objects.equals(loginUser.getRoleKey(), "user")) return new ArrayList<>();
            //作者还需要过滤不是自己的文章
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getUsername, loginUser.getUsername());
            User user = userService.getOne(userWrapper);
            wrapper.eq(Article::getCreateBy, user.getId()).eq(Article::getStatus, 1).eq(Article::getIsDeleted, 0);
        }

        List<Article> articles = this.list(wrapper);

        return EntityListToVOList.articleListToVOList(articles, userService);
    }
    @Override
    public List<ArticleVO> getMyArticleByTag(List<String> tag){
        UserContext.LoginUser loginUser = UserContext.get();
        LambdaQueryWrapper<ArticleTag> tagwrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        //查询对应文章id
        tagwrapper.in(ArticleTag::getName, tag);
        tagwrapper.select(ArticleTag::getId);
        List<ArticleTag> ArticleTagList =  articleTagService.list(tagwrapper);
        if(ArticleTagList.isEmpty()){
            return new ArrayList<>();
        }
        //处理关联关系，集合去重提取文章id
        Set<Long> ArticleIdList = ArticleTagList.stream().map(ArticleTag::getId).collect(Collectors.toSet());
        //查询文章
        wrapper.in(Article::getId, ArticleIdList);
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) {
            //普通用户直接返回空
            if(Objects.equals(loginUser.getRoleKey(), "user")) return new ArrayList<>();
            //作者还需要过滤不是自己的文章
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getUsername, loginUser.getUsername());
            User user = userService.getOne(userWrapper);
            wrapper.eq(Article::getCreateBy, user.getId()).eq(Article::getStatus, 1).eq(Article::getIsDeleted, 0);
        }
        //按更新时间排序
        wrapper.orderByDesc(Article::getUpdateTime);
        List<Article> articles = this.list(wrapper);

        return EntityListToVOList.articleListToVOList(articles, userService);
    }
    @Override
    public List<ArticleVO> getArticleByTag(List<String> tag){
        UserContext.LoginUser loginUser = UserContext.get();
        LambdaQueryWrapper<ArticleTag> tagwrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        //查询对应文章id
        tagwrapper.in(ArticleTag::getName, tag);
        tagwrapper.select(ArticleTag::getId);
        List<ArticleTag> ArticleTagList =  articleTagService.list(tagwrapper);
        if(ArticleTagList.isEmpty()){
            return new ArrayList<>();
        }
        //处理关联关系，集合去重提取文章id
        Set<Long> ArticleIdList = ArticleTagList.stream().map(ArticleTag::getId).collect(Collectors.toSet());
        //查询文章
        wrapper.in(Article::getId, ArticleIdList);
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) {
            wrapper.eq(Article::getStatus, 1).eq(Article::getIsDeleted, 0);
        }
        //按更新时间排序
        wrapper.orderByDesc(Article::getUpdateTime);
        List<Article> articles = this.list(wrapper);

        return EntityListToVOList.articleListToVOList(articles, userService);
    }

    @Override
    public String viewCountUpdate(ArticleDTO articleDTO){
        UpdateWrapper<Article> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", articleDTO.getId());
        updateWrapper.setSql("view_count = view_count + 1");
        this.update(updateWrapper);
        return "浏览量更新成功";
    }

    @Override
    public IPage<ArticleVO> getHotArticle(int pageNum, int pageSize){
        //分页
        Page<Article> pageInfo = new Page<>(pageNum, pageSize);
        UserContext.LoginUser loginUser = UserContext.get();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) {
            wrapper.eq(Article::getStatus, 1).eq(Article::getIsDeleted, 0);
        }
        //按浏览量降序
        wrapper.orderByDesc(Article::getViewCount);
        IPage<Article> result = this.page(pageInfo, wrapper);
        IPage<ArticleVO> articleVOList = new Page<>();

        BeanUtils.copyProperties(result, articleVOList);
        return  articleVOList.setRecords(EntityListToVOList.articleListToVOList(pageInfo.getRecords(), userService));

    }
    //==============创作者权限===================================//

    @Override
    public List<ArticleVO> getArticleManagerList(){
        UserContext.LoginUser loginUser = UserContext.get();
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        //不是管理员就过滤草稿和已删除的文章
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) {
            //普通用户直接返回空
            if(Objects.equals(loginUser.getRoleKey(), "user")) return new ArrayList<>();
            //作者还需要过滤不是自己的文章
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getUsername, loginUser.getUsername());
            User user = userService.getOne(userWrapper);
            wrapper.eq(Article::getCreateBy, user.getId()).eq(Article::getIsDeleted, 0);
        }
        //按更新时间排序
        wrapper.orderByDesc(Article::getUpdateTime);
        List<Article> articles = this.list(wrapper);
        return EntityListToVOList.articleListToVOList(articles, userService);
    }

    @Override
    public String deleteMyArticle(ArticleDTO articleDTO){
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getId, articleDTO.getId()).eq(Article::getTitle, articleDTO.getTitle());
        Article article = this.getOne(wrapper);
        //查看是否是作者
        UserContext.LoginUser loginUser = UserContext.get();
        userWrapper.eq(User::getUsername, loginUser.getUsername());
        User user = userService.getOne(userWrapper);
        if(article.getCreateBy() != user.getId()){
            return "错误";
        }
        //逻辑删除
        article.setIsDeleted(1);
        this.updateById(article);
        return "删除成功";
    }
//==============管理员权限===================================//
    @Override
    public List<ArticleVO> getArticleListAdmin() {
        /*
          管理员Token校验
         */
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();

        //按更新时间排序
        wrapper.orderByDesc(Article::getUpdateTime);
        List<Article> articles = this.list(wrapper);

        return EntityListToVOList.articleListToVOList(articles, userService);
    }

    @Override
    @Transactional(rollbackFor =  Exception.class)
    public String publishArticle(ArticleDTO articleDTO){
        UserContext.LoginUser loginUser = UserContext.get();
        if(Objects.equals(loginUser.getRoleKey(), "user")) return "错误操作";
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUser.getUsername());
        User user = userService.getOne(wrapper);
        /*
          管理员Token校验
         */
        Article article = new Article();
        BeanUtils.copyProperties(articleDTO, article);
        //填写系统生成数据
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());
        //创建人
        article.setCreateBy(user.getId());
        //分类
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(Category::getName, articleDTO.getCategoryName());
        Category category = categoryService.getOne(categoryWrapper);
        if(category == null){
            //创建分类
            Category categoryNew = new Category();
            categoryNew.setName(articleDTO.getCategoryName());
            categoryNew.setCreateTime(LocalDateTime.now());
            categoryService.save(categoryNew);
            //给新分类的id
            article.setCategoryId(categoryNew.getId());
        }
        else{
            //给id
            article.setCategoryId(category.getId());
        }
        //默认申请
        //article.setStatus(0);
        this.save(article);
        // 处理表关联

        return "发布成功";
    }

    @Override
    public String updateArticle(ArticleDTO articleDTO){
        UserContext.LoginUser loginUser = UserContext.get();
        //权限校验
        if(Objects.equals(loginUser.getRoleKey(), "user")) return "错误操作";
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUser.getUsername());
        User user = userService.getOne(wrapper);
        if(user == null) return "错误操作";
        //作者id校验
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getId, articleDTO.getId());
        Article article = this.getOne(articleWrapper);
        if(article == null || article.getCreateBy() != user.getId()) return "错误操作";
        //更新文章
        BeanUtils.copyProperties(articleDTO, article);
        //更新时间
        article.setUpdateTime(LocalDateTime.now());
        //分类
        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.eq(Category::getName, articleDTO.getCategoryName());
        Category category = categoryService.getOne(categoryWrapper);
        if(category == null){
            //创建分类
            Category categoryNew = new Category();
            categoryNew.setName(articleDTO.getCategoryName());
            categoryNew.setCreateTime(LocalDateTime.now());
            categoryService.save(categoryNew);
            //给新分类的id
            article.setCategoryId(categoryNew.getId());
        }
        else{
            //给id
            article.setCategoryId(category.getId());
        }
        this.updateById(article);
        // 处理表关联
        return "更新成功";
    }

    @Override
    public String deleteArticle(ArticleDTO articleDTO){

        Article article = this.getById(articleDTO.getId());
        article.setIsDeleted(1);
        this.updateById(article);

        return "删除成功";
    }
    @Override
    public String accessArticle(Long id){
        UserContext.LoginUser loginUser = UserContext.get();
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) return "错误操作";
        Article article = this.getById(id);
        if(article == null) return "错误操作";
        //允许通过
        article.setStatus(1);
        this.updateById(article);
        return "操作成功";
    }
}
