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
package org.jspecify.nullness;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that one annotation conveys (at least) all of the same meaning as some other
 * annotation; for example, {@code @Implies(One.class) @interface Two {}} says that any occurrence
 * of {@code @Two} implicitly conveys all of the meaning that {@code @One} would in that same
 * position. Note that the {@code Two} annotation type may define additional meaning of its own as
 * well, perhaps even adding additional usages of {@code @Implies}.
 *
 * <p><b>WARNING:</b> This annotation is under development, and <i>any</i> aspect of its naming,
 * location, or design may change before 1.0. <b>Do not release libraries using this annotation at
 * this time.</b>
 */
@Documented
@Repeatable(ImpliesAnnotations.class)
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Implies {
  Class<? extends Annotation> value();
}
