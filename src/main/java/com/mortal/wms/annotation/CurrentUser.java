package com.mortal.wms.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})// 表示该注解只能用于方法参数
@Retention(RetentionPolicy.RUNTIME)// 表示该注解在运行时保留，可以通过反射读取
@Documented// 表示该注解会包含在 Javadoc 中
public @interface CurrentUser {

    /**
     * 当前用户在request中的名字
     *
     * @return
     */
    String value() default "user";

}