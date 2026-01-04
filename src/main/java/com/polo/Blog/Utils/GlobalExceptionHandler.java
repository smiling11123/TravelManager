package com.polo.Blog.Utils;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

/**
 * 全局异常处理
 */
@Slf4j // 自动生成 log 对象，方便打印日志
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统报错啦：", e); // 在后台打印错误堆栈
        return Result.error("系统繁忙，请稍后重试"); // 给前端返回提示
    }

    // 2. 捕获自定义的业务异常
    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException e) {
        log.error("业务异常：{}", e.getMessage());
        return Result.error(e.getMessage()); // 直接把报错信息返给前端
    }
}
