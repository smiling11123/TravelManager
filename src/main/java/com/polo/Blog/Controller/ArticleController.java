package com.polo.Blog.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.polo.Blog.Domain.DTO.ArticleDTO;
import com.polo.Blog.Domain.Entity.Article;
import com.polo.Blog.Domain.OV.ArticleVO;
import com.polo.Blog.Service.ArticleService;
import com.polo.Blog.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;

    //根据文章id获取文章详细内容
    @GetMapping("/detail")
    public Result<ArticleVO> getArticleById(@RequestParam Long id) {
        return Result.success(articleService.getArticleById(id));
    }

    //前端前台获取文章列表
    @GetMapping("/list")
    public Result<List<ArticleVO>> getArticleList() {
        return Result.success(articleService.getArticleList());
    }
    //根据标签获取文章
    @GetMapping("/listByTags")
    public Result<List<ArticleVO>> getArticleListByTags(@RequestParam List<String> tag){
        return Result.success(articleService.getArticleByTag(tag));
    }

    @GetMapping("/search")
    public Result<List<ArticleVO>> getArticleByKeyWord(@RequestParam (defaultValue = "") String keyWord, @RequestParam (defaultValue = "") List<Long> categoryId){
        return Result.success(articleService.getArticleByKeyWord(keyWord, categoryId));
    }
    @GetMapping("/hotList")
    public Result<IPage<ArticleVO>> getHotArticleByViewCount(@RequestParam (defaultValue = "1") int page, @RequestParam (defaultValue = "20") int size){
        return Result.success(articleService.getHotArticle(page, size));
    }
    //======================================创作者权限===========================================//
    @PostMapping("/auth/delete")
    public Result<String> deleteMyArticle(@RequestBody ArticleDTO articleDTO){
        return Result.success(articleService.deleteMyArticle(articleDTO));
    }
    @GetMapping("/managerList")
    public Result<List<ArticleVO>> getArticleManagerList(){
        return Result.success(articleService.getArticleManagerList());
    }
    @GetMapping("/searchMyArticle")
    public Result<List<ArticleVO>> getMyArticleByKeyWord(@RequestParam String keyWord){
        return Result.success(articleService.getMyArticleByKeyWord(keyWord));
    }
    @GetMapping("/myArticleByTags")
    public Result<List<ArticleVO>> getMyArticleByTags(@RequestParam List<String> tag){
        return Result.success(articleService.getMyArticleByTag(tag));
    }
    //======================================管理员权限==========================================//
    //前端后台获取文章列表
    @GetMapping("/admin/list")
    public Result<List<ArticleVO>> getArticleListAdmin() {
        return Result.success(articleService.getArticleListAdmin());
    }
    @PostMapping("/publish")
    public Result<String> publishArticle(@RequestBody ArticleDTO articleDTO) {
        return Result.success(articleService.publishArticle(articleDTO));
    }
    @PostMapping("/update")
    public Result<String> updateArticle(@RequestBody ArticleDTO articleDTO) {
        return Result.success(articleService.updateArticle(articleDTO));
    }
    @PostMapping("/delete")
    public Result<String> deleteArticle(@RequestBody ArticleDTO articleDTO){
        return Result.success(articleService.deleteArticle(articleDTO));
    }
    @PostMapping("/admin/accessPublish")
    public Result<String> accessArticle(@RequestParam Long id){
        return Result.success(articleService.accessArticle(id));
    }
}
