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

/// JSpecify annotations. See [jspecify.org](http://jspecify.dev) for general information.
///
/// ## What's here?
///
/// This package will contain annotations supporting a variety of static analyses. For now it
/// supports just nullness analysis.
///
/// ### Nullness {#nullness}
///
/// The primary annotations of interest are [NullMarked] and [Nullable]. Together they
/// provide **declarative, use-site nullness** for Java types. Less frequently, their negations
/// may be useful: [NullUnmarked] and [NonNull], respectively.
///
/// For a comprehensive introduction to JSpecify, please see [jspecify.org](http://jspecify.dev).
///
/// ## Note on tool behavior {#tool-behavior}
///
/// Each of these annotations defines a single meaning shared by all compatible tools (and
/// libraries). JSpecify documentation aims to provide unambiguous, tool-independent answers for how
/// to properly annotate your APIs in all circumstances. However, tools are permitted to
/// *respond* to the information you provide however they see fit (or not at all). JSpecify
/// compatibility does not require that any particular finding must or must not be issued to the
/// user, let alone its message or severity.
///
/// In fact, it's important to remember that declarative annotations are merely *one* source
/// of information an analyzer may consult in concluding an expression is safely non-null. Just like
/// one analyzer might determine that an `int` expression can take on only positive values,
/// another might likewise determine that a declaratively nullable expression can take on only
/// non-null values. In both cases the declarative knowledge is *correct*, but the inferred
/// knowledge is both correct and more specific.
///
/// On the other end, the tools might even enforce *nothing at all*. In particular, your
/// annotated code (or other code dependent on its annotated APIs) might be compiled and run without
/// any appropriate tool even in use. Therefore adopting JSpecify annotations is not a replacement
/// for **explicitly checking arguments** at runtime.
package org.jspecify.annotations;
