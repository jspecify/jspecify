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

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated type usage includes {@code null} as a value. To understand the
 * nullness of <i>unannotated</i> type usages, check for {@link NullMarked} on the enclosing class,
 * package, or module. See the <a href="http://jspecify.org/user-guide">JSpecify User Guide</a> for
 * details.
 *
 * <p><b>WARNING:</b> This annotation is under
 * development, and <i>any</i> aspect of its naming, location, or design may change before 1.0.
 * <b>Do not release libraries using this annotation at this time.</b>
 */
@Documented
@Target(TYPE_USE)
@Retention(RUNTIME)
public @interface Nullable {}
