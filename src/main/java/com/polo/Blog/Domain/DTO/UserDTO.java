package com.polo.Blog.Domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    //private long id;             // 用户id
    private String username;     // 用户名
    private String password;     // 用户密码
    private String nickname;     // 用户昵称
    private String email;        // 用户邮箱地址
    private String intro;        // 个人简介
    private String avatar;       // 头像
    //private String loginType;    // 用户登录方式
}
