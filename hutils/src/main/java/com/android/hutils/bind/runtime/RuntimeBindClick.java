package com.android.hutils.bind.runtime;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huangbei on 20-1-15.
 */

@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Target(ElementType.METHOD)//描述方法
public @interface RuntimeBindClick {
    int[] value();
}
