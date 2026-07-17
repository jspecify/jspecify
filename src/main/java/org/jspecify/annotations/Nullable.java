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

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated <a href="https://github.com/jspecify/jspecify/wiki/type-usages">type
 * usage</a> (commonly a parameter type or return type) is considered to include {@code null} as a
 * value.
 *
 * <p>Example usages:
 *
 * <pre>{@code
 * @Nullable String field;
 *
 * @Nullable String getField() { return field; }
 *
 * void setField(@Nullable String value) { field = value; }
 *
 * List<@Nullable String> getList() { … }
 * }</pre>
 *
 * <p>For important information common to all four nullness annotations, see {@link
 * org.jspecify.annotations}. To learn more about JSpecify, see <a
 * href="https://jspecify.dev">jspecify.dev</a>.
 *
 * <h2>Meaning for each kind of type usage</h2>
 *
 * <p>Despite the itemized list here, the essential meaning of this annotation is always the same in
 * every case: the type it annotates is considered to include {@code null} as a value. A good way to
 * read {@code @Nullable String} in any context is <b>"string-or-null"</b>. That's usually all you
 * need to think about, but this section offers some additional explanation for each kind of
 * context.
 *
 * <ul>
 *   <li>On a <b>parameter type</b>: See the {@code setField} example above. It permissively accepts
 *       a "string-or-null", meaning that it is okay to pass an actual string, or to pass {@code
 *       null}. (This does not guarantee that passing {@code null} won't produce an exception at
 *       runtime.) Lambda expression parameters work in the same way <i>if</i> their types are
 *       written explicitly.
 *   <li>On a <b>method return type</b>: See the {@code getField} example above. It returns a
 *       "string-or-null", so while the caller might get a real string back, it might get {@code
 *       null} as well, and should be prepared for that possibility.
 *   <li>On a <b>field type</b>: See the {@code field} example above. It has the type
 *       "string-or-null", so it might hold a string and it might hold {@code null}. (If you are
 *       confident the containing object can't be observed in its uninitialized state, the field
 *       need not be annotated solely because it is null during initialization.)
 *   <li>On a <b>type argument</b>: Within the compound type {@code MyList<@Nullable String>}, we
 *       can see a usage of the type "string-or-null". To understand what {@code @Nullable} means
 *       here, read the {@code MyList<E>} API; every appearance you see of the {@code E} type will
 *       be treated as nullable (unless it has its own nullness annotation, which takes precedence).
 *       For a typical container type like this example, this means it can contain null
 *       <i>elements</i>; if your list itself might be null as well, we can write {@code @Nullable
 *       MyList<@Nullable String>}: a nullable list of nullable strings.
 *   <li>On the upper bound of a <b>type parameter</b>: For example, consider {@code interface
 *       List<E extends @Nullable String>}. This means that type arguments in that position
 *       <i>may</i> be nullable (as in {@code List<@Nullable String>}). A non-null type argument, as
 *       in {@code List<String>}, is permitted either way.
 *   <li><span id="projection">On a usage of a <b>type variable</b>:</span> A type parameter, like
 *       the {@code T} declared in {@code class Optional<T>}, introduces a <i>type variable</i> of
 *       the same name, usable only within the scope of the declaring API element. All the preceding
 *       examples using {@code String} are equally valid for a type variable like {@code E}.
 *       {@code @Nullable} continues to mean "or null" as always, regardless of whether the type
 *       argument "already" includes {@code null}. For example, suppose that {@code class Foo<E
 *       extends @Nullable Object>} has a method {@code @Nullable E eOrNull()}. Then, whether a
 *       variable {@code foo} is of type {@code Foo<String>} or {@code Foo<@Nullable String>}, the
 *       expression {@code foo.eOrNull()} is nullable either way. Using {@code @Nullable E} in this
 *       way is called <i>nullable projection</i>.
 *   <li>On an <b>array type</b>: The nullness of the array and of its components can each be
 *       indicated separately. For example, in {@linkplain NullMarked null-marked} context,
 *       {@code @Nullable String[]} is a non-null array of nullable strings, and {@code
 *       String @Nullable []} is a nullable array of non-null strings.
 *   <li>On a <b>varargs parameter</b>: Use {@code @Nullable String...} to indicate that the
 *       individual strings may be null. To let {@code null} be passed as the entire array, use
 *       {@code String @Nullable ...}.
 *   <li>On a <b>nested type</b>: In most examples above, in place of {@code String} we might use a
 *       nested type such as {@code Map.Entry}. The Java syntax for annotating such a type as
 *       nullable looks like {@code Map.@Nullable Entry}.
 *   <li>On a <b>record component</b>: Java propagates all type-use annotations (including
 *       {@code @Nullable}) from a record component type to the type of the generated field,
 *       accessor method, and/or constructor parameter. If the constructor parameter list or
 *       accessor method is provided explicitly, it must still be annotated explicitly.
 * </ul>
 *
 * <h2 id="applicability">Where it is not applicable</h2>
 *
 * <p>This annotation and {@link NonNull} are applicable to any <a
 * href="https://github.com/jspecify/jspecify/wiki/type-usages">type usage</a> <b>except</b> the
 * following cases, where they have no defined meaning and should not be used:
 *
 * <ul>
 *   <li>On any <b>intrinsically non-null type usage</b>: one that is incapable of including {@code
 *       null} by the rules of the Java language, like the type following {@code throws}. A nullness
 *       annotation in this location could only be either contradictory or redundant.
 *   <li>On a <b>local variable</b> declaration (root type only). While the cases listed above
 *       benefit from <i>declarative</i> nullness, the nullness of a local variable is more properly
 *       determined through flow analysis (<a href="https://bit.ly/3ppb8ZC">Why?</a>). This rule
 *       applies only to the <i>root type</i> of the variable; type components, such as type
 *       arguments, follow the usual rules.
 *   <li>In a <b>cast expression</b> (root type only). To inform an analyzer that an expression it
 *       sees as nullable is truly non-null, use an assertion or a method like {@link
 *       java.util.Objects#requireNonNull}. (<a href="https://bit.ly/3ppb8ZC">Why?</a>) Type
 *       components, such as type arguments, follow the usual rules.
 *   <li>On any part of the argument to <b>{@code instanceof}</b>. The root type is intrinsically
 *       non-null, as discussed above, and nothing else about nullness is checked at runtime.
 *   <li>On any part of a <b>receiver parameter</b> type (<a
 *       href="https://docs.oracle.com/javase/specs/jls/se26/html/jls-8.html#jls-8.4">JLS 8.4</a>).
 *   <li>If both {@code @Nullable} and {@code @NonNull} appear on the same type usage,
 *       <i>neither</i> one is recognized.
 * </ul>
 *
 * Whether the surrounding code is in null-marked context also has no consequence in the above
 * locations.
 *
 * <h2>Unannotated type usages</h2>
 *
 * <p>For a type usage where nullness annotations are {@linkplain ##applicability applicable} but
 * not present, its nullness depends on whether it appears within null-marked context; see {@link
 * NullMarked} for details. Note in particular that nullness information from a superclass is never
 * automatically "inherited".
 */
@Documented
@Target(TYPE_USE)
@Retention(RUNTIME)
public @interface Nullable {}
