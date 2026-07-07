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

/// Indicates that the annotated element and the code transitively
/// [enclosed][javax.lang.model.element.Element#getEnclosedElements()] within it is **null-unmarked
/// code**: there, type usages generally have **unspecified nullness** unless explicitly annotated
/// otherwise.
///
/// This annotation's purpose is to ease migration of a large existing codebase to null-marked
/// status. It makes it possible to "flip the default" for new code added to a class or package even
/// before that class or package has been fully migrated. Since new code is the most important code
/// to analyze, this is strongly recommended as a temporary measure whenever necessary. However,
/// once a codebase has been fully migrated it would be appropriate to ban use of this annotation.
///
/// For a comprehensive introduction to JSpecify, please see [jspecify.org](http://jspecify.dev).
///
/// # Null-marked and null-unmarked code
///
/// [NullMarked] and this annotation work as a pair to include and exclude sections of code from
/// null-marked status (respectively). Specifically, code is considered null-marked if the most
/// narrowly enclosing element annotated with either of these two annotations exists and is
/// annotated with `@NullMarked`.
///
/// Otherwise it is considered null-unmarked. This can happen in two ways: either it is more
/// narrowly enclosed by a `@NullUnmarked`-annotated element than by any `@NullMarked`-annotated
/// element, or neither annotation is present on any enclosing element. No distinction is made
/// between these cases.
///
/// The effects of being null-marked are described in the [Effects][NullMarked##effects] section of
/// `NullMarked`.
///
/// # Unspecified nullness
///
/// Within null-unmarked code, a type usage with no nullness annotation has **unspecified nullness**
/// ([Why?](https://bit.ly/3ppb8ZC)). This means that, while there is always *some* correct way to
/// annotate it for nullness, that information is missing: we *do not know* whether it includes or
/// excludes `null` as a value. In such a case, tools can vary widely in how strict or lenient their
/// enforcement is, or might make it configurable.
///
/// For more, please see this more [comprehensive
/// discussion](https://github.com/jspecify/jspecify/wiki/nullness-unspecified) of unspecified
/// nullness.
///
/// There is no way for an individual type usage within null-marked code to have unspecified
/// nullness. ([Why?](https://bit.ly/3ppb8ZC))
///
/// # Where it can be used
///
/// The information in the [Where it can be used][NullMarked##where] section of `NullMarked` applies
/// as well to this annotation.
// TODO(kevinb9n): word the middle section better with good words
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({PACKAGE, TYPE, METHOD, CONSTRUCTOR})
public @interface NullUnmarked {}
