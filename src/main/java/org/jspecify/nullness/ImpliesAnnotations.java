package org.jspecify.nullness;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Container annotation for the repeatable {@link Implies @Implies} annotation.
 *
 * <p><b>WARNING:</b> This annotation is under development, and <i>any</i> aspect of its naming,
 * location, or design may change before 1.0. <b>Do not release libraries using this annotation at
 * this time.</b>
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface ImpliesAnnotations {
  Implies[] value();
}
