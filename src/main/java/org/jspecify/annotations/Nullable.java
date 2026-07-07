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

/// Indicates that the annotated [type usage](https://github.com/jspecify/jspecify/wiki/type-usages)
/// (commonly a parameter type or return type) is considered to include `null` as a value.
///
/// Example usages:
///
/// ```
/// @Nullable String field;
///
/// @Nullable String getField() { return field; }
///
/// void setField(@Nullable String value) { field = value; }
///
/// List<@Nullable String> getList() { … }
/// ```
///
/// For a comprehensive introduction to JSpecify, please see [jspecify.org](http://jspecify.dev).
///
/// # Meaning per each kind of type usage
///
/// The essential meaning of this annotation is always the same: the type it annotates is considered
/// to include `null` as a value. But this may affect your code a little differently based on the
/// kind of type usage involved.
/// - On a **parameter type**: The `setField` method (at top) permissively accepts a
///   "string-or-null", meaning that it is okay to pass an actual string, or to pass `null`. (This
///   doesn't guarantee that passing `null` won't produce an exception at runtime, but it should be
///   much less likely.) This also applies to the type of a lambda expression parameter, if that
///   type is given explicitly (otherwise its nullness must be inferred from context).
/// - On a **method return type**: The `getField` method returns a "string-or-null", so while the
///   caller might get a string back, it should also address the possibility of getting `null`
///   instead. (This doesn't guarantee there is any circumstance in which `null` *will* actually be
///   returned.)
/// - On a **field type**: The `field` field has the type "string-or-null", so at times it might
///   hold a string, and at times it might hold `null`. (Of course, every field of a reference type
///   *originally* holds `null`, but as long as the class ensures that its uninitialized states
///   can't be observed, it's appropriate to overlook that fact.)
/// - On a **type argument**: A type usage of "nullable string" appears *within* the compound type
///   `List<@Nullable String>`. No matter how this type is used (return type, etc.), this means the
///   same thing: every appearance of `E` in `List`'s member signatures will be considered nullable.
///   For a list, this means it may contain null *elements*. If the list reference itself might be
///   null as well, we can write `@Nullable List<@Nullable String>`, a "nullable list of nullable
///   strings".
/// - On the upper bound of a **type parameter**: For example, as seen in `class List<E
///   extends @Nullable Object>`. This means that a *type argument* supplied for that type parameter
///   is permitted to be nullable if desired: `List<@Nullable String>`. (A non-null type argument,
///   as in `List<String>`, is permitted either way.)
/// - <span id="projection">On a usage of a **type variable**:</span> A type parameter, like the `E`
///   in `interface List<E>`, defines a "type variable" of the same name, usable only *within* the
///   scope of the declaring API element. In any example using `String` above, a type variable like
///   `E` might appear instead. `@Nullable` continues to mean "or null" as always, but notably, this
///   works without regard to whether the type argument is *already* nullable. For example, suppose
///   that `class Foo<E extends @Nullable Object>` has a method `@Nullable E eOrNull()`. Then,
///   whether `foo` is of type `Foo<String>` or `Foo<@Nullable String>`, the expression
///   `foo.eOrNull()` is nullable either way. Using `@Nullable E` in this way is called "nullable
///   projection" ([non-null projection][NonNull##projection] is likewise supported, but less
///   commonly useful).
/// - On a **nested type**: In most examples above, in place of `String` we might use a nested type
///   such as `Map.Entry`. The Java syntax for annotating such a type as nullable looks like
///   `Map.@Nullable Entry`.
/// - On a **record component**: As expected, `@Nullable` here applies equally to the corresponding
///   parameter type of the canonical constructor, and to the return type of a generated accessor
///   method as well. If an explicit accessor method is provided for this record component, it must
///   still be annotated explicitly. Any non-null components should be checked (for example using
///   [java.util.Objects#requireNonNull]) in a [compact
///   constructor](https://docs.oracle.com/en/java/javase/19/language/records.html).
///
/// # Where it is applicable <span id="applicability"></span>
///
/// This annotation and [NonNull] are applicable to any [type
/// usage](https://github.com/jspecify/jspecify/wiki/type-usages) **except** the following cases,
/// where they have no defined meaning:
/// - On any** intrinsically non-null type usage**. Some type usages are incapable of including
///   `null` by the rules of the Java language. Examples include any usage of a primitive type, the
///   argument to `instanceof`, a method return type in an annotation interface, or the type
///   following `throws` or `catch`. In such locations, a nullness annotation could only be
///   contradictory (`@Nullable`) or redundant (`@NonNull`).
/// - On the root type of a **local variable** declaration. The nullness of a local variable itself
///   is not a fixed declarative property of its *type*. Rather it should be inferred from the
///   nullness of each expression assigned to the variable, possibly changing over time.
///   ([Why?](https://bit.ly/3ppb8ZC)). Subcomponents of the type (type arguments, array component
///   types) are annotatable as usual.
/// - On the root type in a **cast expression**. To inform an analyzer that an expression it sees as
///   nullable is truly non-null, use an assertion or a method like
///   [java.util.Objects#requireNonNull]. ([Why?](https://bit.ly/3ppb8ZC)) Subcomponents of the type
///   (type arguments, array component types) are annotatable as usual.
/// - On any part of a **receiver parameter** type ([JLS
///   8.4](https://docs.oracle.com/javase/specs/jls/se26/html/jls-8.html#jls-8.4)).
/// - If both `@Nullable` and `@NonNull` appear on the same type usage, *neither* one is recognized.
///
/// Whether the code is [NullMarked] also has no consequence in the above locations.
///
/// # Unannotated type usages
///
/// For a type usage where nullness annotations are [applicable](#applicability) but not present,
/// its nullness depends on whether it appears within [null-marked][NullMarked] code; see that class
/// for details. Note in particular that nullness information from a superclass is never
/// automatically "inherited".
@Documented
@Target(TYPE_USE)
@Retention(RUNTIME)
public @interface Nullable {}
