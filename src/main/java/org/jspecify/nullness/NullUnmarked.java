package org.jspecify.nullness;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element and the code transitively <a
 * href=”https://docs.oracle.com/en/java/javase/18/docs/api/java.compiler/javax/lang/model/element/Element.html#getEnclosedElements()”>enclosed</a>
 * within it is <b>null-unmarked code</b>: type usages generally have <b>unspecified nullness</b>
 * unless explicitly annotated otherwise.
 *
 * <h2>Null-marked and null-unmarked code</h2>
 *
 * {@link NullMarked @NullMarked} and this annotation work as a pair to include and exclude sections
 * of code from null-marked status. All code is considered either null-marked or null-unmarked
 * (never both).
 *
 * <p>Code is considered null-marked if its most narrowly enclosing element annotated with either of
 * these two annotations is annotated with {@link NullMarked @NullMarked}.
 *
 * <p>Otherwise it is considered null-unmarked: whether that’s because it is more narrowly enclosed
 * by a {@code @NullUnmarked}-annotated element than by any {@link NullMarked @NullMarked}-annotated
 * element, or because neither annotation is present on any enclosing element. There is no
 * distinction made between these cases.
 *
 * <p><b>WARNING:</b> This annotation is under development, and <i>any</i> aspect of its naming,
 * location, or design may change before 1.0. <b>Do not release libraries using this annotation at
 * this time.</b>
 *
 * @see NullMarked {@code @NullMarked} for more information.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({TYPE, METHOD, CONSTRUCTOR, MODULE, PACKAGE})
public @interface NullUnmarked {}
