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

/// Indicates that the annotated element and the code transitively
/// [enclosed][javax.lang.model.element.Element#getEnclosedElements()] within it are **null-marked
/// code**: there, type usages are generally considered to exclude `null` as a value unless
/// specified otherwise. Using this annotation avoids the need to write [`@NonNull`][NonNull] many
/// times throughout your code.
///
/// For a comprehensive introduction to JSpecify, please see [jspecify.org](http://jspecify.dev).
///
/// # Effects of being null-marked <span id="effects"></span>
///
/// Within null-marked code, as a *general* rule, a type usage is considered non-null (to exclude
/// `null` as a value) unless explicitly annotated as [Nullable]. However, there are several special
/// cases to address.
///
/// ## Special cases <span id="#effects-special-cases"></span>
///
/// Within null-marked code:
/// - We might expect the type represented by a **wildcard** (like the `?` in `List<?>`) to be
///   non-null, but it isn't necessarily. It's non-null only if it `extends` a non-null type (like
///   in `List<? extends String>`), or if the *class* in use accepts only non-null type arguments
///   (such as if `List` were declared as `class List<E extends String>`). But if `List` does accept
///   nullable type arguments, then the wildcards seen in `List<?>` and `List<? super String>` must
///   include `null`, because they have no "upper bound". ([Why?](https://bit.ly/3ppb8ZC))
/// - Conversely, a **type parameter** is always considered to have an upper bound; when none is
///   given explicitly, `Object` is filled in by the compiler. The example `class MyList<E>` is
///   interpreted identically to `class MyList<E extends Object>`: in both cases the type argument
///   in `MyList<@Nullable Foo>` is out-of-bounds, so the list elements are always non-null.
///   ([Why?](https://bit.ly/3ppb8ZC))
/// - Otherwise, being null-marked has no consequence for any type usage where `@Nullable` and
///   `@NonNull` are [**not applicable**][Nullable##applicability], such as the root type of a local
///   variable declaration.
/// - When a type variable has a nullable upper bound, such as the `E` in `class Foo<E
///   extends @Nullable Bar>`), an unannotated usage of this type variable is not considered
///   nullable, non-null, or even of "unspecified" nullness. Rather it has **parametric nullness**.
///   In order to support both nullable and non-null type arguments safely, the `E` type itself must
///   be handled *strictly*: as if nullable when "read from", but as if non-null when "written to".
///   (Contrast with `class Foo<E extends Bar>`, where usages of `E` are simply non-null, just like
///   usages of `String` are.)
/// - By using [NullUnmarked], an element within null-marked code can be excluded and made
///   null-unmarked, exactly as if there were no enclosing `@NullMarked` element at all.
///
/// # Where it can be used <span id="where"></span>
///
/// `@NullMarked` and [`@NullUnmarked`][NullUnmarked] can be used on any package, class, method, or
/// constructor declaration; `@NullMarked` can be used on a module declaration as well. Special
/// considerations:
/// - To apply this annotation to an entire (single) **package**, create a
///   [`package-info.java`](https://docs.oracle.com/javase/specs/jls/se26/html/jls-7.html#jls-7.4.1)
///   file there. This is recommended so that newly-created classes will be null-marked by default.
///   This annotation has no effect on "subpackages". **Warning**: if the package does not belong to
///   a module, be very careful: it can easily happen that different versions of the package-info
///   file are seen and used in different circumstances, causing the same classes to be interpreted
///   inconsistently. For example, a package-info file from a `test` source path might hide the
///   corresponding one from the `main` source path, or generated code might be compiled without
///   seeing a package-info file at all.
/// - Although Java permits it to be applied to a **record component** declaration (as in `record
///   Foo(@NullMarked String bar) {...}`), this annotation has no meaning when used in that way.
/// - Applying this annotation to an instance **method** of a *generic* class is acceptable, but is
///   not recommended because it can lead to some confusing situations.
/// - An advantage of Java **modules** is that you can make a lot of code null-marked with just a
///   single annotation (before the `module` keyword). [NullUnmarked] is not supported on modules,
///   since it's already the default.
/// - If both `@NullMarked` and `@NullUnmarked` appear together on the same element, *neither* one
///   is recognized.
@Documented
@Target({MODULE, PACKAGE, TYPE, METHOD, CONSTRUCTOR})
@Retention(RUNTIME)
public @interface NullMarked {}
