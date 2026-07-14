/*
 * Copyright 2018-2020 The JSpecify Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jspecify.annotations;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element and the code transitively {@linkplain
 * javax.lang.model.element.Element#getEnclosedElements() enclosed} within it are <b>null-marked
 * code</b>: there, type usages are generally considered to exclude {@code null} as a value unless
 * specified otherwise. Using this annotation avoids the need to write {@link NonNull @NonNull} many
 * times throughout your code.
 *
 * <p>This meaning does not apply to any enclosed element annotated with {@link
 * NullUnmarked @NullUnmarked}, nor any code enclosed within it. That is, the null-marked status of
 * a span of code depends only on the <i>nearest enclosing</i> element having <i>either</i> of these
 * two annotations.
 *
 * <p>The {@linkplain org.jspecify.annotations package documentation} has some important general
 * information common to all four nullness annotations. For a comprehensive introduction to
 * JSpecify, please see <a href="https://jspecify.dev">jspecify.dev</a>.
 *
 * <h2 id="effects">Effects of being null-marked</h2>
 *
 * <p>Within null-marked code, a type usage is generally considered to be non-null unless explicitly
 * annotated as {@link Nullable Nullable}. However, there are a few special cases to address.
 *
 * <h3 id="effects-special-cases">Special cases</h3>
 *
 * <p>Within null-marked code:
 *
 * <ul>
 *   <li>Any type usage where {@code @Nullable} and {@code @NonNull} are <a
 *       href="Nullable.html#applicability"><b>not applicable</b></a>, such as the root type of a
 *       local variable declaration, is unaffected.
 *   <li>A <b>wildcard</b> with no upper bound generally represents a nullable type (unless the
 *       corresponding type parameter has a non-null upper bound itself). (<a
 *       href="https://bit.ly/3ppb8ZC">Why?</a>) This wildcard might be either unbounded, as in
 *       {@code List<?>}, or with only a lower bound, as in {@code List<? super Integer>}.
 *   <li>A <b>type parameter</b> itself is a different case. It is never really "unbounded"; if no
 *       upper bound is given explicitly, then {@code Object} is filled in by the compiler. This
 *       means the example {@code class MyList<E>} is interpreted identically to {@code class
 *       MyList<E extends Object>}, making the upper bound <i>non-null</i> {@code Object}. (<a
 *       href="https://bit.ly/3ppb8ZC">Why?</a>)
 *   <li>When a type parameter has a nullable upper bound, such as the {@code E} in {@code class
 *       Foo<E extends @Nullable Bar>}, an unannotated usage of the associated type variable (within
 *       that class) must be handled conservatively: it is nullable when read, yet cannot have
 *       {@code null} assigned to it.
 * </ul>
 *
 * <h2 id="where">Where it can be used</h2>
 *
 * {@code @NullMarked} and {@link NullUnmarked @NullUnmarked} can be used on any package, class,
 * method, or constructor declaration; {@code @NullMarked} can be used on a module declaration as
 * well. ({@link NullUnmarked @NullUnmarked} is not supported on modules, since it's already the
 * default.) Special considerations:
 *
 * <ul>
 *   <li>To apply these annotations to an entire (single) <b>package</b>, create a <a
 *       href="https://docs.oracle.com/javase/specs/jls/se26/html/jls-7.html#jls-7.4.1">{@code
 *       package-info.java}</a> file and annotate the package declaration there. This annotation has
 *       no effect on "subpackages". <b>Warning</b>: if the package does not belong to a module, be
 *       very careful: it can easily happen that different versions of the package-info file are
 *       seen and used in different circumstances, causing the same classes to be interpreted
 *       inconsistently.
 *   <li>An advantage of Java <b>modules</b> is that you can make a lot of code null-marked with
 *       just a single annotation (before the {@code module} keyword). <b>Warning</b>: Even if you
 *       annotate the module for a library as {@code @NullMarked}, this has no effect for users who
 *       place the library on the class path; it affects only those who place the library on the <a
 *       href="https://openjdk.org/projects/jigsaw/quick-start">module path</a>, which is less
 *       commonly used.
 *   <li>Applying this annotation to an annotation interface does <i>not</i> express that
 *       annotations of that type are synonymous with {@code @NullMarked}. It has the same meaning
 *       as it does anywhere else.
 *   <li>These annotations have no meaning when applied to a <b>record component</b> declaration (as
 *       in {@code record Foo(@NullMarked String bar) {}}). ({@link Nullable} and {@link NonNull}
 *       are still meaningful for record components.)
 *   <li>If both {@code @NullMarked} and {@code @NullUnmarked} appear together on the same element,
 *       <i>neither</i> one is recognized.
 * </ul>
 */
@Documented
@Target({MODULE, PACKAGE, TYPE, METHOD, CONSTRUCTOR})
@Retention(RUNTIME)
public @interface NullMarked {}
