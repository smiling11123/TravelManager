package com.polo.Blog.Domain.OV;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private long id;
    private String username;
    private String nickname;
    private String email;
    private String avatar;
    private String intro;
    private String loginType;
    @JsonIgnore
    private String status;
    private String statusText;
    private String roleName;
    private String roleKey;
    private LocalDateTime createTime;

    public String getStatusText() {
        return Objects.equals(this.status, "0") ? "停用" : "正常";
    }

}
