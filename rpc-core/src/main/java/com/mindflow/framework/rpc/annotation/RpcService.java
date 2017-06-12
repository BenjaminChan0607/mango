package com.mindflow.framework.rpc.annotation;

import org.springframework.stereotype.Component;
import java.lang.annotation.*;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RpcService {
    /**
     * 服务实现的接口
     * @return
     */
    Class<?> value();

    /**
     * 当前服务版本号
     * @return
     */
    String version() default "";

    /**
     * 服务名
     * @return
     */
    String name() default "";
}
