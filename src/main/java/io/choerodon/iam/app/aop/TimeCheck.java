package io.choerodon.iam.app.aop;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //定义注解使用位置
@Retention(RetentionPolicy.RUNTIME) //注解声明周期
public @interface TimeCheck {

}
