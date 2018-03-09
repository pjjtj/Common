package com.core.common.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 李乐
 * @Date 2017/11/14 16:30.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestError {
    private String name;
    private String code;
    private String message;
}
