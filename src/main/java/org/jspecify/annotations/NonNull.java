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

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// Indicates that the annotated [type usage](https://github.com/jspecify/jspecify/wiki/type-usages)
/// (commonly a parameter type or return type) is considered to *exclude* `null` as a value; rarely
/// needed within [null-marked][NullMarked] code.
///
/// This annotation serves two primary purposes:
/// - To mark any sporadic non-null type usages inside a scope that is not ready to be fully
///   [NullMarked] yet.
/// - To perform a *non-null projection* of a type variable, explained below.
///
/// For a comprehensive introduction to JSpecify, please see [jspecify.org](http://jspecify.dev).
///
/// # Non-null projection <span id="projection"></span>
///
/// In the following example, `MyOptional`'s type parameter `T` accepts only non-null type
/// arguments, but `MyList`'s type parameter `E` will accept either a non-null or nullable type
/// argument.
///
/// ```
/// // All the below is null-marked code
///
/// class MyOptional<T> { … }
///
/// interface MyList<E extends @Nullable Object> {
///   // Returns the first non-null element, if such element exists.
///   MyOptional<E> firstNonNull() { … } // problem here!
/// }
///
/// MyList<@Nullable String> maybeNulls = …
/// MyList<String> nonNulls = …
/// ```
///
/// Because `MyOptional` accepts only non-null type arguments, we need both
/// `maybeNulls.firstNonNull()` and `nonNulls.firstNonNull()` to produce the same return type:
/// `MyOptional!<String!>` (see
/// [notation](https://github.com/jspecify/jspecify/wiki/notation#shorthand-notation)). However, as
/// specified above, they won't do that. In fact, there is a problem with the `firstNonNull`
/// signature, since the type argument `String?` would not meet the requirements of `MyOptional`'s
/// type parameter.
///
/// The solution is to **project** the type argument to its non-null counterpart:
///
/// ```
/// // Returns the first non-null element, if such element exists.
/// MyOptional<@NonNull E> firstNonNull() { … } // problem fixed!
/// ```
///
/// Here, `@NonNull E` selects the non-null form of the type argument, whether it was already
/// non-null or not, which is just what we need in this scenario.
///
/// If `E` has a non-null upper bound, then the apparent projection `@NonNull E` is redundant but
/// harmless.
///
/// [Nullable projection][Nullable##projection] serves the equivalent purpose in the opposite
/// direction, and is far more commonly useful.
///
/// If a type variable has *all* its usages being projected in one direction or the other, it should
/// be given a non-null upper bound, and any non-null projections can then be removed.
///
/// # Where it is applicable
///
/// `@NonNull` is applicable in all the [same locations][Nullable##applicability] as [Nullable].
@Documented
@Target(TYPE_USE)
@Retention(RUNTIME)
public @interface NonNull {}
