package org.jspecify.nullness;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that one annotation conveys (at least) all of the same meaning as some other
 * annotation; for example, {@code @Implies(One.class) @interface Two {}} says that any occurrence
 * of {@code @Two} implicitly conveys all of the meaning that {@code @One} would in that same
 * position. Note that the {@code Two} annotation type may define additional meaning of its own as
 * well, perhaps even adding additional usages of {@code @Implies}.
 *
 * <p><b>WARNING:</b> This annotation is under development, and <i>any</i> aspect of its naming,
 * location, or design may change before 1.0. <b>Do not release libraries using this annotation at
 * this time.</b>
 */
@Documented
@Repeatable(ImpliesAnnotations.class)
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Implies {
  Class<? extends Annotation> value();
}
