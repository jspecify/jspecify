/*
 * Copyright 2022 The JSpecify Authors.
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
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated element and the code transitively {@linkplain
 * javax.lang.model.element.Element#getEnclosedElements() enclosed} within it are in
 * <b>null-unmarked context</b>: there, type usages generally have {@linkplain ##unspecified
 * unspecified nullness} unless explicitly annotated otherwise.
 *
 * <p>This annotation's purpose is to ease progressive migration of a large codebase. If some class
 * or package can't be fully migrated yet, you can still make it {@linkplain NullMarked null-marked}
 * by using this annotation on the portions that still need work. This practice is useful because it
 * flips the default for <i>new</i> code added later, which is the most important code to analyze.
 *
 * <p>Once a codebase has been fully migrated it would be reasonable to ban use of this annotation.
 *
 * <p>The {@linkplain org.jspecify.annotations package documentation} has some important general
 * information common to all four nullness annotations. For a comprehensive introduction to
 * JSpecify, please see <a href="https://jspecify.dev">jspecify.dev</a>.
 *
 * <h2>Null-marked and null-unmarked contexts</h2>
 *
 * <p>{@link NullMarked} puts enclosed code in null-marked context; this annotation puts it in
 * null-unmarked context instead. These annotations work as an inclusion-exclusion pair: of the
 * annotations of <i>either</i> type on all enclosing elements (methods, classes, etc.), only the
 * <i>nearest</i> (most narrowly enclosing) one is in effect.
 *
 * <p>Otherwise, code is in null-unmarked context. This can happen in two ways: either it is more
 * narrowly enclosed by a {@code @NullUnmarked}-annotated element than by any
 * {@code @NullMarked}-annotated element, or neither annotation is present on any enclosing element.
 * No distinction is made between these cases.
 *
 * <p>The effects of being null-marked are described in the {@linkplain NullMarked##effects Effects}
 * section of {@code NullMarked}.
 *
 * <h2 id="unspecified">Unspecified nullness</h2>
 *
 * <p>Within null-unmarked context, a type usage that is {@linkplain Nullable##applicability
 * nullness-applicable} but has no nullness annotation generally has <b>unspecified nullness</b> (<a
 * href="https://bit.ly/3ppb8ZC">Why?</a>). This means we <i>do not know</i> whether it includes or
 * excludes {@code null} as a value. In such a case, tools can vary widely in how strict or lenient
 * their enforcement is, or might even be configurable.
 *
 * <p>For more, please see this more <a
 * href="https://github.com/jspecify/jspecify/wiki/nullness-unspecified">comprehensive
 * discussion</a> of unspecified nullness.
 *
 * <p>There is no way for an individual type usage within null-marked context to have unspecified
 * nullness. (<a href="https://bit.ly/3ppb8ZC">Why?</a>)
 *
 * <h2>Where it can be used</h2>
 *
 * The information in the {@linkplain NullMarked##where Where it can be used} section of {@code
 * NullMarked} applies as well to this annotation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({PACKAGE, TYPE, METHOD, CONSTRUCTOR})
public @interface NullUnmarked {}
