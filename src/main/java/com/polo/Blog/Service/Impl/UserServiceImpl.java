package com.polo.Blog.Service.Impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.polo.Blog.Domain.DTO.LoginUserDTO;
import com.polo.Blog.Domain.DTO.UserDTO;
import com.polo.Blog.Domain.Entity.Role;
import com.polo.Blog.Domain.Entity.User;
import com.polo.Blog.Domain.Entity.UserRole;
import com.polo.Blog.Domain.OV.UserVO;
import com.polo.Blog.Mapper.UserMapper;
import com.polo.Blog.Service.RoleService;
import com.polo.Blog.Service.UserRoleService;
import com.polo.Blog.Service.UserService;
import com.polo.Blog.Utils.EntityListToVOList;
import com.polo.Blog.Utils.JwtUtils;
import com.polo.Blog.Utils.Result;
import com.polo.Blog.Utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserRoleService userRoleService;
    //在严谨的项目中需要给登录的用户创建Token用户后续操作的身份验证
    @Override
    public Result loginHandel(LoginUserDTO loginUserDTO){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUserDTO.getUsername());//.eq(User::getPassword, loginUserDTO.getPassword());
        User user = this.getOne(wrapper);
        if(user == null){
            return new Result<>( 500, "用户名错误", "");
        }
        //密文比较
        if(!BCrypt.checkpw(loginUserDTO.getPassword(), user.getPassword())){
            return new Result<>( 500, "密码错误", "");
        }
        //上线
        user.setStatus("1");
        this.updateById(user);
        //获取角色
        userRoleWrapper.eq(UserRole::getUserId, user.getId());
        UserRole userRole = userRoleService.getOne(userRoleWrapper);
        roleWrapper.eq(Role::getId, userRole.getRoleId());
        Role role = roleService.getOne(roleWrapper);
        //登录成功返回Token
        return new Result<>(200, "成功响应", JwtUtils.generateToken(user.getUsername(), role.getRoleKey()));
    }
    @Override
    public String registerHandel(LoginUserDTO loginUserDTO, String isAuth){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUserDTO.getUsername());//.eq(User::getPassword, loginUserDTO.getPassword());
        User user = this.getOne(wrapper);
        if(user != null){
            return "该用户已存在";
        }
        //记录数据库
        User userSave = new User();
        BeanUtils.copyProperties(loginUserDTO, userSave);
        //加密 密文存储
        String encodedPassword = BCrypt.hashpw(userSave.getPassword());
        userSave.setPassword(encodedPassword);
        this.save(userSave);
        //注册角色
        UserRole userRole = new UserRole();
        if(Objects.equals(isAuth, "Auth")){
            userRole.setRoleId(2);
        }
        else {
            userRole.setRoleId(3);
        }
        userRole.setUserId(userSave.getId());
        userRoleService.save(userRole);

        return "注册成功";
    }
    @Override
    public String logoutHandel(String username){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername,username);//.eq(User::getPassword, loginUserDTO.getPassword());
        User user = this.getOne(wrapper);
        if(user == null){
            return "没有该用户";
        }
        //更新用户数据
        user.setLoginDate(LocalDateTime.now());
        user.setStatus("0");
        this.updateById(user);

        return "登出成功";
    }
    @Override
    public String deleteUser(LoginUserDTO loginUserDTO){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUserDTO.getUsername());//.eq(User::getPassword, loginUserDTO.getPassword());
        User user = this.getOne(wrapper);
        if(user == null){
            return "没有该用户";
        }
        //逻辑注解使is_deleted在删除时自动设为1达到逻辑删除的效果
        this.removeById(user.getId());
        return "删除用户成功";
    }
    @Override
    public UserVO getUserDetail(String username){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<UserRole> userRoleWrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        UserVO userVO = new UserVO();
        User user = this.getOne(wrapper);
        //复制详细信息
        BeanUtils.copyProperties(user, userVO);
        //获取角色
        userRoleWrapper.eq(UserRole::getUserId, user.getId());
        long roleId = userRoleService.getOne(userRoleWrapper).getRoleId();
        roleWrapper.eq(Role::getId, roleId);
        Role role = roleService.getOne(roleWrapper);
        userVO.setRoleName(role.getRoleName());
        userVO.setRoleKey(role.getRoleKey());
        return userVO;
    }
    @Override
    public String updateUserInfo(UserDTO userDTO){
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, userDTO.getUsername());
        User user = this.getOne(wrapper);
        String userName = user.getUsername();
        //更新字段
        BeanUtils.copyProperties(userDTO, user);
        user.setUsername(userName);
        this.updateById(user);
        return "更新用户信息成功";
    }

    @Override
    public String updatePassWord(String passWord, String oldPassWord){
        UserContext.LoginUser loginUser = UserContext.get();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, loginUser.getUsername());
        User user = this.getOne(wrapper);
        if(user == null) return "错误操作";
        if(!BCrypt.checkpw(oldPassWord, user.getPassword())) return "错误操作";
        if(passWord == null) return "错误操作";
        //更新密文密码
        String encodedPassword = BCrypt.hashpw(passWord);
        user.setPassword(encodedPassword);
        this.updateById(user);
        return "修改成功";
    }

    @Override
    public UserVO getUserDetailById(Long id){
        UserContext.LoginUser loginUser = UserContext.get();
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) return new UserVO();
        User user = this.getById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        //查用户角色关联
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, user.getId());
        UserRole userRole = userRoleService.getOne(wrapper);
        //查角色
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getId, userRole.getRoleId());
        Role role = roleService.getOne(roleWrapper);
        //复制给VO
        userVO.setRoleName(role.getRoleName());
        userVO.setRoleKey(role.getRoleKey());
        return userVO;
    }

    @Override
    public String deleteUserById(Long id){
        UserContext.LoginUser loginUser = UserContext.get();
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) return "错误操作";
        User user = this.getById(id);
        user.setIsDeleted(1);
        this.updateById(user);
        return "删除成功";
    }

    @Override
    public IPage<UserVO> getUserList(int pageNum, int pageSize){
        //分页
        Page<User> pageInfo = new Page<>(pageNum, pageSize);
        UserContext.LoginUser loginUser = UserContext.get();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) {
            return new Page<>();
        }
        wrapper.eq(User::getIsDeleted, 0);
        //按浏览量降序
        IPage<User> result = this.page(pageInfo, wrapper);
        IPage<UserVO> userVOList = new Page<>();

        BeanUtils.copyProperties(result, userVOList);
        return  userVOList.setRecords(EntityListToVOList.userListToVOList(pageInfo.getRecords()));
    }

    @Override
    public IPage<UserVO> searchUserByKeyWord(int pageNum, int pageSize, String keyword){
        //分页
        Page<User> pageInfo = new Page<>(pageNum, pageSize);
        UserContext.LoginUser loginUser = UserContext.get();
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if(!Objects.equals(loginUser.getRoleKey(), "admin")) {
            return new Page<>();
        }
        wrapper.eq(User::getIsDeleted, 0);
        wrapper.like(User::getUsername, keyword);
        //按浏览量降序
        IPage<User> result = this.page(pageInfo, wrapper);
        IPage<UserVO> userVOList = new Page<>();

        BeanUtils.copyProperties(result, userVOList);
        return  userVOList.setRecords(EntityListToVOList.userListToVOList(pageInfo.getRecords()));
    }
}
