package com.polo.Blog.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.polo.Blog.Domain.DTO.LoginUserDTO;
import com.polo.Blog.Domain.DTO.UserDTO;
import com.polo.Blog.Domain.Entity.User;
import com.polo.Blog.Domain.OV.UserVO;
import com.polo.Blog.Utils.Result;
import org.springframework.stereotype.Service;

public interface UserService extends IService<User> {
    /**
     * 用户登录处理
     * @param loginUserDTO 传入登录对象（包含用户名和密码）
     * @return 登录成功信息
     */
    Result loginHandel(LoginUserDTO loginUserDTO);

    /**
     * 用户注册处理
     * @param loginUserDTO 传入注册对象（包含用户名和密码）
     * @param isAuth 是否注册创作者
     * @return 注册成功信息
     */
    String registerHandel(LoginUserDTO loginUserDTO, String isAuth);

    /**
     * 用户登出处理
     * @param username 操作对象
     * @return 登出成功信息
     */
    String logoutHandel(String username);

    /**
     * 用户删除处理
     * @param loginUserDTO 操作对象
     * @return 返回删除成功信息
     */
    String deleteUser(LoginUserDTO loginUserDTO);

    /**
     * 获取用户信息信息
     * @param username 前端传入的操作对象
     * @return 返回VO给前端展示
     */
    UserVO getUserDetail(String username);

    /**
     * 更新用户信息
     * @param userDTO 操作对象
     * @return 返回更新成功信息
     */
    String updateUserInfo(UserDTO userDTO);

    /**
     * 修改密码
     * @param passWord 新密码
     * @param oldPassWord 旧密码
     * @return 修改成功
     */
    String updatePassWord(String passWord, String oldPassWord);

    /**
     * 根据id获取用户详情
     * @param id 用户id
     * @return 返回用户详情
     */
    UserVO getUserDetailById(Long id);

    /**
     * 删除用户
     * @param id 用户id
     * @return 返回删除成功信息
     */
    String deleteUserById(Long id);

    /**
     * 分页获取用户列表
     * @param pageNum 页数
     * @param pageSize 页大小
     * @return 返回一页
     */
    IPage<UserVO> getUserList(int pageNum, int pageSize);

    /**
     * 分页获取搜索用户列表
     * @param pageNum 页数
     * @param pageSize 页大小
     * @param keyword 搜索词
     * @return 返回一页
     */
    IPage<UserVO> searchUserByKeyWord(int pageNum, int pageSize, String keyword);
}
