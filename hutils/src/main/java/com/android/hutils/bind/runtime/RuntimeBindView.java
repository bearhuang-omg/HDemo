package com.android.hutils.bind.runtime;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by huangbei on 20-1-15.
 */

@Retention(RetentionPolicy.RUNTIME)//运行时生效
@Target({ElementType.FIELD,ElementType.TYPE})//描述变量和类
public @interface RuntimeBindView {
    int value() default View.NO_ID;
}
