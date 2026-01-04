package com.polo.Blog.Controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.polo.Blog.Domain.DTO.LoginUserDTO;
import com.polo.Blog.Domain.DTO.UserDTO;
import com.polo.Blog.Domain.OV.UserVO;
import com.polo.Blog.Service.UserService;
import com.polo.Blog.Utils.Result;
import com.polo.Blog.Utils.UserContext;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Data
@RequestMapping("/user")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/detail")
    public Result<UserVO> getUserDetail(){
        return Result.success(userService.getUserDetail(UserContext.get().getUsername()));
    }
    @GetMapping("/delete")
    public Result<String> deleteUser(@RequestBody LoginUserDTO loginUserDTO){
        return Result.success(userService.deleteUser(loginUserDTO));
    }
    @PostMapping("/update")
    public Result<String> updateUserInfo(@RequestBody UserDTO userDTO){
        return Result.success(userService.updateUserInfo(userDTO));
    }

    @PostMapping("/updatePassWord")
    public Result<String> updatePassWord(@RequestParam String passWord, @RequestParam String oldPassWord){
        return Result.success(userService.updatePassWord(passWord, oldPassWord));
    }
    //=============管理员==========================//

    @GetMapping("/list")
    public Result<IPage<UserVO>> getUserList(@RequestParam (defaultValue = "1") int page, @RequestParam (defaultValue = "20") int size){
        return Result.success(userService.getUserList(page, size));
    }

    @GetMapping("/detailById")
    public Result<UserVO> getUserDetailById(@RequestParam Long id){
        return Result.success(userService.getUserDetailById(id));
    }

    @PostMapping("/deleteById")
    public Result<String> deleteUserById(@RequestParam Long id){
        return Result.success(userService.deleteUserById(id));
    }

    @GetMapping("/search")
    public Result<IPage<UserVO>> searchUserByKeyWord(@RequestParam (defaultValue = "1") int page, @RequestParam (defaultValue = "20") int size, @RequestParam String keyword){
        return Result.success(userService.searchUserByKeyWord(page, size, keyword));
    }
}

