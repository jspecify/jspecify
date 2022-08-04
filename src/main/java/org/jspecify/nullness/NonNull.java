package org.jspecify.nullness;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated type usage is considered to exclude {@code null} as a value (used
 * infrequently, as we typically apply {@link NullMarked @NullMarked} to some enclosing element
 * instead).
 *
 * <p>For a guided introduction to JSpecify nullness annotations, please see the <a
 * href="https://jspecify.dev/user-guide.html">user guide</a>.
 *
 * <p><b>WARNING:</b> This annotation is under development, and <i>any</i> aspect of its naming,
 * location, or design may change before 1.0. <b>Do not release libraries using this annotation at
 * this time.</b>
 */
@Documented
@Target(TYPE_USE)
@Retention(RUNTIME)
public @interface NonNull {}
