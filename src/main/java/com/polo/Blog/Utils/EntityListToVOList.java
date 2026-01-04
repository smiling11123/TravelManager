package com.polo.Blog.Utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.polo.Blog.Domain.Entity.Article;
import com.polo.Blog.Domain.Entity.User;
import com.polo.Blog.Domain.OV.ArticleVO;
import com.polo.Blog.Domain.OV.UserVO;
import com.polo.Blog.Service.UserService;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 实体类文章数据转换成给前端的数据
 */
public class EntityListToVOList {

    public static List<ArticleVO> articleListToVOList(List<Article> articles, UserService userService){

        List<ArticleVO> articleVOList = new ArrayList<>();
        //转换成 VO
        for(Article article : articles){
            ArticleVO articleVO = new ArticleVO();
            BeanUtils.copyProperties(article, articleVO);
            //获取作者名
            LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
            userWrapper.eq(User::getId, article.getCreateBy());
            User user = userService.getOne(userWrapper);
            articleVO.setAuth(user.getUsername());

            articleVOList.add(articleVO);
        }
        return articleVOList;
    }

    public static List<UserVO> userListToVOList(List<User> users){

        List<UserVO> userVOList = new ArrayList<>();
        //转换成 VO
        for(User user : users){
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            userVOList.add(userVO);
        }
        return userVOList;
    }
}
