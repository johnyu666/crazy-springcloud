package com.crazymaker.springcloud.message.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MqSubscriber
{

    String topic();
}
