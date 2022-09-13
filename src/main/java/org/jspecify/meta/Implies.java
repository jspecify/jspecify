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
package org.jspecify.meta;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that an annotation type conveys all of the same meaning as the given annotation
 * type(s); for example, {@code @Implies(One.class) @interface Two {}} says that any usage of
 * {@code @Two} implicitly conveys all the same meaning as {@code @One} (with no attributes) would
 * in the same location. Note that the {@code Two} annotation type <i>may</i> add additional meaning
 * of its own as well, and may be applicable in locations where {@code One} is not.
 *
 * <p>We will refer to this example throughout this documentation:
 *
 * <pre>{@code
 * public @interface One {}
 *
 * // Additional meaning of `@Two`, if any, specified here.
 * &commat;Implies(One.class)
 * public @interface Two {}
 *
 * // These might be classes, fields, methods, etc.
 * &commat;One declaration_a
 * &commat;Two declaration_b
 * &commat;One @Two declaration_c
 * }</pre>
 *
 * <p>In this example, the meaning conveyed by the annotations is exactly the same for {@code
 * declaration_b} and {@code declaration_c}; there, {@code @One} is considered redundant (but
 * harmless). A JSpecify-compatible tool will treat the two identically (apart from optionally
 * reporting the redundancy itself).
 *
 * <p><b>Warning:</b> These annotations are under development, and <b>any</b> aspect of their
 * naming, locations, or design is subject to change until the JSpecify 1.0 release. Moreover,
 * supporting analysis tools will be tracking the changes on varying schedules. Releasing a library
 * using these annotations in its API is <b>strongly discouraged</b> at this time.
 *
 * <h2>Details</h2>
 *
 * <p>An array of other annotation types may be provided, indicating that this annotation type (the
 * one that is itself annotated with {@code @Implies}) carries (at least) the combined meanings of
 * <i>all</i> of them.
 *
 * <p>In the example above, if {@code Two} does not define any additional semantics, and lists only
 * one implied annotation, we call it an <i>alias</i> for {@code One}. In this case, the annotations
 * on <i>all</i> three example declarations above convey identical meaning, and should be treated
 * identically by tools.
 *
 * <p>Implication is naturally transitive: if in addition to the example code above we also have
 * {@code @Implies(Two.class) @interface Three {}}, it is understood that {@code Three} also implies
 * {@code One}. Listing {@code One.class} explicitly is redundant but harmless.
 *
 * <p>It is not possible to supply custom attribute values for an implied annotation, such as
 * declaring that {@code @Two} implies {@code @One("arg")} or that {@code @Two(key = VALUE)} implies
 * {@code @One(key = VALUE)}.
 *
 * <p>The target types of {@code One} and {@code Two} may differ. {@code @Two} may therefore appear
 * in a location where {@code @One} cannot; for these usages {@code @Implies} has no effect. The
 * target types of {@code One} and {@code Two} might even be disjoint. For example if they are
 * {@code {FIELD}} and {@code {TYPE_USE}}, there are still source locations where either can appear
 * (before the type of a field), so the implication still has meaning.
 *
 * <p>If {@code Two} has wider retention than {@code One}, then in any situation where {@code @One}
 * would not be visible, the effect of {@code @Implies} is undefined.
 */
@Documented
@Retention(RUNTIME)
@Target(ANNOTATION_TYPE)
public @interface Implies {
  /**
   * The annotation types implied by this annotation type (the one that is itself annotated with
   * {@code Implies}). May not include any annotation type that declares an attribute with no
   * default value. Listing this annotation type itself in the array, or any annotation type that is
   * already transitively implied by another listed, is redundant but harmless. (A larger cycle of
   * implication is acceptable.) Any number of types can be given. Their order is irrelevant. A
   * duplicate type in the array is redundant but harmless. If the array is empty (e.g.,
   * {@code @Implies({}) @interface One}), this entire annotation is redundant but harmless.
   *
   * @return the annotation types implied by this annotation type
   */
  Class<? extends Annotation>[] value();
}
