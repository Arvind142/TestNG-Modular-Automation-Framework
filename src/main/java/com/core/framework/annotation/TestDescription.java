package com.core.framework.annotation;

import com.core.framework.constant.FrameworkConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestDescription {
	String author() default FrameworkConstants.NOT_APPLICABLE_CONST;
	String testDescription() default FrameworkConstants.NOT_APPLICABLE_CONST;
}
