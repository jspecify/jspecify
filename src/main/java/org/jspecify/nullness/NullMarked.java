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

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that within the annotated scope (class or package), type usages <i>generally</i> do
 * <i>not</i> include {@code null} as a value, unless they are individually marked otherwise using
 * {@link Nullable} (or checker-specific other annotations). Without this annotation, unmarked type
 * usages would instead have <i>unspecified nullness</i>. Several exceptions to this rule and an
 * explanation of unspecified nullness are covered in the <a
 * href="http://jspecify.org/user-guide">JSpecify User Guide</a>.
 *
 * <p><b>WARNING:</b> This annotation is under development, and <i>any</i> aspect of its naming,
 * location, or design may change before 1.0. <b>Do not release libraries using this annotation at
 * this time.</b>
 */
@Documented
@Target({TYPE, PACKAGE})
@Retention(RUNTIME)
public @interface NullMarked {
  // note for maintainers: When you update this file, please update the file in src/java9 too.
}
