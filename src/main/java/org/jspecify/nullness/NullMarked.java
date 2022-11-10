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
package org.jspecify.nullness;

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
 * <p>For a guided introduction to JSpecify nullness annotations, please see the <a
 * href="http://jspecify.org/docs/user-guide">User Guide</a>.
 *
 * <p><b>Warning:</b> These annotations are under development, and <b>any</b> aspect of their
 * naming, locations, or design is subject to change until the JSpecify 1.0 release. Moreover,
 * supporting analysis tools will be tracking the changes on varying schedules. Releasing a library
 * using these annotations in its API is <b>strongly discouraged</b> at this time.
 *
 * <h2>Effects of being null-marked</h2>
 *
 * <p>Within null-marked code, as a <i>general</i> rule, a type usage is considered non-null (to
 * exclude {@code null} as a value) unless explicitly annotated as {@link Nullable}. However, there
 * are several special cases to address.
 *
 * <h3 id="special-cases">Special cases</h3>
 *
 * <p>Within null-marked code:
 *
 * <ul>
 *   <li>Being null-marked has no consequence for any type usage where {@code @Nullable} and
 *       {@code @NonNull} are <a href="Nullable.html#applicability"><b>not applicable</b></a>, such
 *       as the root type of a local variable declaration.
 *   <li>An <b>unbounded wildcard</b> (the {@code ?} in a type like {@code List<?>}, not followed by
 *       {@code extends} or {@code super}) represents <i>any</i> type within the type parameter's
 *       bounds with no further restriction. A <b>lower-bounded wildcard</b> ({@code List<? super
 *       String>}) similarly has no <i>upper</i> bound of its own. Therefore, since <i>nullable</i>
 *       {@code Object} is the new "<a href="Nullable.html#subtypes">top type</a>", either list
 *       might have null elements, even though the word {@code @Nullable} is nowhere in sight. On
 *       the other hand, in {@code List<? extends Object>} a bound <i>is</i> provided, and as usual,
 *       being unannotated it is considered non-null. This means that while {@code List<?>} and
 *       {@code List<? extends Object>} have always been identical as base types, they are no longer
 *       identical as <a href="Nullable.html#augmented-types">augmented types</a>. (<a
 *       href="https://bit.ly/3ppb8ZC">Why?</a>)
 *       <ul>
 *         <li>Conversely, a <b>type parameter</b> is always bounded: when none is given explicitly,
 *             {@code Object} is filled in by the compiler. The example {@code class MyList<E>} is
 *             interpreted identically to {@code class MyList<E extends Object>}: in both cases the
 *             type argument in {@code MyList<@Nullable Foo>} is out-of-bounds, so the list elements
 *             are always non-null. (<a href="https://bit.ly/3ppb8ZC">Why?</a>)
 *       </ul>
 *   <li>When a type variable has a nullable upper bound, such as the {@code E} in {@code class Foo<E
 *       extends @Nullable Bar>}), an unannotated usage of this type variable is not considered
 *       nullable, non-null, or even of "unspecified" nullness. Rather it has <b>parametric
 *       nullness</b>. In order to support both nullable and non-null type arguments safely, the
 *       {@code E} type itself must be handled <i>strictly</i>: as if nullable when "read from", but
 *       as if non-null when "written to". (Contrast with {@code class Foo<E extends Bar>}, where
 *       usages of {@code E} are simply non-null, just like usages of {@code String} are.)
 *   <li>By using {@link NullUnmarked}, an element within null-marked code can be excluded and made
 *       null-unmarked, exactly as if there were no enclosing {@code @NullMarked} element at all.
 * </ul>
 *
 * <h2>Warning about package annotations</h2>
 *
 * <p>Use caution when applying this annotation to a package (via {@code package-info}) that does
 * not belong to a module. It is possible for different versions of {@code package-info} to be
 * visible in different analysis scenarios, which could cause the same single class to be
 * interpreted inconsistently. As one example, a {@code package-info} file from a "test" source root
 * might hide the one from the "main" source root.
 */
@Documented
@Target({TYPE, METHOD, CONSTRUCTOR, PACKAGE, MODULE})
@Retention(RUNTIME)
public @interface NullMarked {}
