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
 * Indicates that the annotated type usage (commonly a
 * variable type or return type) is considered to include
 * {@code null} as a value.
 *
 * <p>Example usages:
 *
 * <pre>{@code
 * &#64;Nullable String field;
 *
 * &#64;Nullable String getField() { return field; }
 *
 * void setField(&#64;Nullable String value) { field = value; }
 *
 * List<&#64;Nullable String> getList() { … }
 * }</pre>
 *
 * <p>For a guided introduction to JSpecify nullness
 * annotations, please see the
 * <a href="http://jspecify.org/docs/user-guide">User Guide</a>.
 *
 * <p><b>Warning:</b> These annotations are under development,
 * and <b>any</b> aspect of their naming, locations, or design
 * is subject to change until the JSpecify 1.0 release.
 * Moreover, supporting analysis tools will be tracking the
 * changes on varying schedules. Releasing a library using
 * these annotations in its API is <b>strongly discouraged</b>
 * at this time.
 *
 * <h2 id="augmented-types">Augmented types</h2>
 *
 * <p>{@code javac}‘s type system ignores this annotation: in
 * all the examples above, the type it recognizes is "string
 * (of ambiguous nullness)", as always. {@code javac} always
 * treats a variable of such type <i>leniently</i>: you can
 * freely assign {@code null} to it (as if it's nullable), yet
 * you can freely deference it as well (as if it's non-null).
 * We call this type the <b>base type</b>.
 *
 * <p>It's the function of a JSpecify-compatible <b>nullness
 * analyzer</b> to interpret {@code String} and
 * {@code &#64;Nullable String} (for example) as <i>though</i>
 * they are properly distinct types. (We will use
 * {@code String} as a convenient example type throughout this
 * documentation.) We call a base type together with its
 * nullness indicator (usually, "nullable" or "non-null") an
 * <b>augmented type</b>.
 *
 * <h2 id="notation">Important note on notation</h2>
 *
 * <p>Augmented types are indicated in prose using a
 * <b>shorthand</b>, combining a base type with a symbol
 * representing a nullness indicator.
 * (<a href="https://bit.ly/3ppb8ZC">Why?</a>)
 *
 * <ul>
 *
 * <li>{@code String!} represents the <b>non-null form</b> of
 * the base type {@code String}: a reference to an actual
 * string object. This might be expressed in Java code as
 * {@code &#64;NonNull String}, or, within null-marked code, as
 * just {@code String}.
 *
 * <li>{@code String?} represents the <b>nullable form</b> of
 * the base type {@code String}: a "string-or-null". This is
 * usually expressed in Java as {@code &#64;Nullable String}.
 *
 * <li>{@code String*} represents the <b>unspecified form</b>,
 * as we would find in fully unannotated code. This is included
 * here to round out the set; a proper explanation of
 * unspecified nullness is in the <a
 * href="http://jspecify.org/docs/user-guide">User Guide</a>.
 *
 * <li>A type variable with no suffix, like {@code T}, has <a
 * href="#special-cases">parametric nullness</a>. Any suffix
 * above would indicate a <i>projection</i> of the type
 * variable (see below).
 *
 * </ul>
 *
 * <p>For a complex example, a nullable array containing
 * non-null arrays, in turn containing strings of unspecified
 * nullness, would be written {@code String*[]![]?}. For a
 * parameterized type, the suffix appears immediately after the
 * class name ({@code Foo!<Bar?>}) rather than after the entire
 * type ({@code Foo<Bar?>!}).
 *
 * <p>A tool might use this shorthand in its error messages,
 * but be aware that it might follow its own similar but
 * different scheme instead.
 *
 * <h2 id="subtypes">Subtypes</h2>
 *
 * <p>The Java Language Specification defines the subtype
 * relation among base types. That definition is extended here
 * to cover augmented types: specifically, the non-null form of
 * any base type is a subtype of its nullable form. This
 * relation extends transitively: for a base type {@code Foo}
 * with supertype {@code SuperFoo}, the augmented type
 * {@code Foo!} is a subtype of {@code Foo?}, of
 * {@code SuperFoo!}, and of {@code SuperFoo?}. [TODO: diagram
 * this.]
 *
 * <p> (How types with unspecified nullness fit into this
 * picture requires
 * <a href="http://jspecify.org/docs/user-guide">more
 * explanation</a>.)
 *
 * <p>In <i>general</i>, for any scenario where the Java
 * Language Specification checks for subtyping, this extended
 * definition applies. For example, array covariance respects
 * it: {@code Foo![]} is a subtype of {@code Foo?[]}. Generics
 * are still invariant, so {@code List!<Foo!>} is <i>not</i> a
 * subtype of {@code List!<Foo?>}, but it is a subtype of
 * {@code List!<? extends Foo?>}.
 *
 * <p>This makes {@code Object?} the new "top type" of the
 * hierarchy: the common supertype of every reference type. One
 * consequence is that {@code List<? extends Object>} no longer
 * means "any list": it now excludes lists with null elements,
 * unlike {@code List<?>}.
 *
 * <h2>Kinds of type usages</h2>
 *
 * <p>The essential meaning of this annotation is always the
 * same: the type in use is considered to include {@code null}
 * as a value. But this may affect your code a little
 * differently based on the kind of type usage involved. Still
 * using the examples at top:
 *
 * <ul>
 *
 * <li>On a <b>parameter type:</b> The {@code setField} method
 * permissively accepts a "string-or-null", meaning that it is
 * okay to pass an actual string, or to pass {@code null}.
 * (This doesn't guarantee that passing {@code null} can't
 * produce an exception at runtime, but it should be much less
 * likely.)
 *
 * <li>On a <b>method return type:</b> The {@code getField}
 * method returns a "string-or-null", so while the caller might
 * get a string back, it should also address the possibility of
 * getting {@code null} instead. (This doesn't guarantee there
 * is any circumstance in which {@code null} <i>will</i>
 * actually be returned.)
 *
 * <li>On a <b>field type:</b> The {@code field} field has the
 * type "string-or-null", so at times it might hold a string,
 * and at times it might hold {@code null}. (Of course, every
 * field of a reference type <i>originally</i> holds
 * {@code null}, but as long as the class ensures that its
 * uninitialized states can't be observed, it's appropriate to
 * overlook that fact.)
 *
 * <li>On a non-wildcard<b> type argument:</b> A type usage of
 * "nullable string" appears <i>within</i> the compound type
 * {@code List<&#64;Nullable String>}. No matter how this type
 * is used (return type, local variable type, etc.), this tells
 * us the same thing: when using this type, every appearance of
 * {@code E} in {@code List}'s member signatures will be
 * considered nullable. For a list, this means it may contain
 * null <i>elements</i>. If the list reference itself might be
 * null as well, we can write {@code &#64;Nullable
 * List<&#64;Nullable String>}, a "nullable list of nullable
 * strings". Type arguments supplied to a generic method
 * invocation or class instance creation expression work
 * similarly.
 *
 * <li>On a<b> type parameter bound:</b> If a type parameter's
 * upper bound is annotated with {@code &#64;Nullable}, as in
 * {@code class Foo<T extends &#64;Nullable Object>}, that
 * means that {@code T} may be bound to nullable types, as in
 * {@code Foo<&#64;Nullable String>}.
 *
 * <li>On a<b> type variable</b> usage: A type parameter, like
 * the {@code E} in {@code interface List<E>}, defines a type
 * variable of the same name, usable only <i>within</i> the
 * scope of the declaring API element. In any example using
 * {@code String} above, a type variable like {@code E} might
 * appear instead. {@code &#64;Nullable} continues to mean "or
 * null" as always, but notably, this works without regard to
 * whether the type argument is <i>already</i> nullable. For
 * example, given {@code class Foo<E extends &#64;Nullable
 * Object>}, with a method {@code &#64;Nullable E eOrNull()},
 * then whether {@code foo} is of type {@code Foo<String>} or
 * {@code Foo<&#64;Nullable String>}, the expression {@code
 * foo.eOrNull()} is always nullable. Using {@code
 * &#64;Nullable E} in this way is called "nullable projection"
 * (<a href="NonNull.html#projection">non-null projection</a>
 * is likewise supported, but less commonly useful).
 *
 * <li>On a <b>nested type</b>: In most examples above, in
 * place of {@code String} we might use a nested type such as
 * {@code Map.Entry}. The Java syntax for annotating such a
 * type as nullable looks like {@code Map.&#64;Nullable Entry}.
 *
 * <li>On a <b>record component</b>: As expected,
 * {@code &#64;Nullable} here applies equally to the
 * corresponding parameter type of the canonical constructor,
 * and <i>if</i> an accessor method is generated, to that
 * method's return type as well. An explicit accessor method,
 * if present, is unaffected: it should be annotated
 * explicitly. Note that checking the expected-non-null
 * components can be done in a "compact constructor".
 *
 * </ul>
 *
 * <h2 id="applicability">Where it is not applicable</h2>
 *
 * <p>This annotation and {@link NonNull} are applicable to any
 * type usage except the following cases, where they have no
 * defined meaning:
 *
 * <ul>
 *
 * <li>On any<b> intrinsically non-null type usage</b>. Some
 * type usages are incapable of including {@code null} by the
 * rules of the Java language. Examples include any usage of a
 * primitive type, the argument to {@code instanceof}, a method
 * return type in an annotation interface, or the type
 * following {@code throws} or {@code catch}. In such
 * locations, a nullness annotation could only be contradictory
 * ({@code &#64;Nullable}) or redundant ({@code &#64;NonNull}).
 *
 * <li>On the root type of a <b>local variable</b> declaration.
 * The nullness of a local variable itself is not a fixed
 * declarative property of its <i>type</i>. Rather it should be
 * inferred from the nullness of each expression assigned to
 * the variable, possibly changing over time.
 * (<a href="https://bit.ly/3ppb8ZC">Why?</a>). Subcomponents
 * of the type (type arguments, array component types) are
 * annotatable as usual.
 *
 * <li>On the root type in a <b>cast expression</b>. To inform
 * an analyzer that an expression it sees as nullable is truly
 * non-null, use an assertion or a method like {@link
 * Objects#requireNonNull}.
 * (<a href="https://bit.ly/3ppb8ZC">Why?</a>) Subcomponents of
 * the type (type arguments, array component types) are
 * annotatable as usual.
 *
 * <li>On a <b>class declaration</b>. Java permits any type-use
 * annotation to be used as a class annotation
 * ({@code &#64;Annotation class Foo}). However, this is not a
 * type usage, and JSpecify nullness annotations have no
 * meaning in such a location.
 * (<a href="https://bit.ly/3ppb8ZC">Why?</a>)
 *
 * <li>On a <b>type parameter or wildcard</b>: Java also
 * permits any type-use annotation to be placed before a type
 * parameter declaration ({@code class Foo<&#64;Annotation T>})
 * or a wildcard type argument ({@code Foo<&#64;Annotation ?
 * extends Bar>}). However, these are not type usages, and
 * JSpecify nullness annotations have no meaning in such
 * locations. (<a href="https://bit.ly/3ppb8ZC">Why?</a>) The
 * <i>bounds</i> of a type parameter or wildcard are type
 * usages and are annotatable as usual.
 *
 * <li>On <i>any</i> part of a <b>receiver parameter</b> type
 * (<a href="https://docs.oracle.com/javase/specs/jls/se18/html/jls-8.html#jls-8.4">JLS 8.4</a>).
 *
 * </ul>
 *
 * Whether the code is {@link NullMarked} also has no
 * consequence in these locations.
 *
 * <h2>Unannotated type usages</h2>
 *
 * <p>For a type usage where nullness annotations are
 * <a href="#applicability">applicable</a> but not present, its
 * nullness depends on whether it appears within {@linkplain
 * NullMarked null-marked} code; see that class for details.
 */
@Documented
@Target(TYPE_USE)
@Retention(RUNTIME)
public @interface Nullable {}
