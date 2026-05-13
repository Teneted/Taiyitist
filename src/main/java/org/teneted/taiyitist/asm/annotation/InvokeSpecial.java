package org.teneted.taiyitist.asm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * WARN: Use with caution! <br/>
 * Used to implement binary compatible method invocation involving super method call. <br/>
 * Only applying to methods annotated with @Invoker takes effect.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface InvokeSpecial {}
