package com.polo.Blog.Controller;

import com.polo.Blog.Domain.DTO.LoginUserDTO;
import com.polo.Blog.Service.UserService;
import com.polo.Blog.Utils.Result;
import com.polo.Blog.Utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Result> login(@RequestBody LoginUserDTO loginUserDTO){
        return Result.success(userService.loginHandel(loginUserDTO));
    }
    @PostMapping("/register")
    public Result<String> register(@RequestBody LoginUserDTO loginUserDTO, @RequestParam (defaultValue = "user") String isAuth){
        return Result.success(userService.registerHandel(loginUserDTO, isAuth));
    }
    @PostMapping("/logout")
    public Result<String> logout(){
        return Result.success(userService.logoutHandel(UserContext.get().getUsername()));
    }
}
