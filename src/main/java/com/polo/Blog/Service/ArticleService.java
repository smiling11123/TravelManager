package com.polo.Blog.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.polo.Blog.Domain.DTO.ArticleDTO;
import com.polo.Blog.Domain.Entity.Article;
import com.polo.Blog.Domain.OV.ArticleVO;


import java.util.List;

/**
 *
 */
public interface ArticleService extends IService<Article> {
    /**
     * 根据文章id获取文章详细内容
     * @return 返回文章列表
     */
    List<ArticleVO> getArticleList();
    /**
     * 根据文章id获取文章详细内容
     * @param id 文章id
     * @return 返回一篇文章的详细信息
     */
    ArticleVO getArticleById(Long id);

    /**
     * 获取关键词搜索结果
     * @param keyWord 关键词（标题）
     * @return 返回对应列表
     */
    List<ArticleVO> getArticleByKeyWord(String keyWord, List<Long> categoryId);

    /**
     * 搜索“我的”文章
     * @param keyWord 搜索关键词
     * @return 返回“我的”文章
     */
    List<ArticleVO> getMyArticleByKeyWord(String keyWord);

    /**
     * 根据分类获取“我的”文章
     * @param tag 分类标签
     * @return 返回“我的”文章
     */
    List<ArticleVO> getMyArticleByTag(List<String> tag);

    /**
     * 获取对应标签分组
     * @param tag 标签组
     * @return 返回文章列表
     */
    List<ArticleVO> getArticleByTag(List<String> tag);

    /**
     * 分页获取热门文章 根据浏览量
     * @param pageNum 页数
     * @param pageSize 每页容量
     * @return 返回一页
     */
    IPage<ArticleVO> getHotArticle(int pageNum, int pageSize);

    /**
     * 获取对应角色的文章管理列表
     * @return 返回对应的文章
     */
    List<ArticleVO> getArticleManagerList();

    /**
     * 创作者删除作品
     * @param articleDTO 操作对象
     * @return 返回删除成功信息
     */
    String deleteMyArticle(ArticleDTO articleDTO);

    /**
     * 前端后台获取文章列表
     * @return 返回所有文章，不过滤
     */
    List<ArticleVO> getArticleListAdmin();

    /**
     * 管理员发布文章
     * @param articleDTO 操作对象
     * @return 成功发布的消息
     */
    String publishArticle(ArticleDTO articleDTO);

    /**
     * 管理员更新文章
     * @param articleDTO 操作对象
     */
    String updateArticle(ArticleDTO articleDTO);

    /**
     *
     * @param articleDTO 操作对象
     * @return 成功删除的消息（逻辑删除）
     */
    String deleteArticle(ArticleDTO articleDTO);

    /**
     * 更新浏览量
     * @param articleDTO 操作对象
     * @return 返回更新成功消息
     */
    String viewCountUpdate(ArticleDTO articleDTO);

    /**
     * 允许文章发布
     * @param id 文章Id
     * @return 返回操作成功信息
     */
    String accessArticle(Long id);
}
