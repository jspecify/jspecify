# JSpecify design overview

## About this document

This document summarizes our current thinking for proposing a set of nullness
annotations. **Nothing here is "set in stone" yet.**

It will be very helpful to read or refer to the
[core concepts/glossary][glossary] document in order to understand this material
correctly. There are numerous other documents in our [shared folder].

## Overview

We propose a type annotation to specify the nullness of individual type usages.
At a high level, our proposed semantics is similar to existing tools' treatment
of nullness type annotations. We treat nullness annotations as purely
compile-time type refinements without effect on runtime semantics. Besides the
typical "[nullable]" and "[non-nullable]" refinements, we distinguish values of
"[unspecified nullness]", which typically arise from unannotated code, and allow
for (with optional warnings if desired) type checks involving "unspecified
nullness" to succeed even when they are unsound.

To avoid excessive annotation overhead in hand-written Java code while clearly
distinguishing annotated from legacy code, we also propose a declaration
annotation, with semantics similar to existing tools' semantics of "non-null by
default." In the absence of that annotation, type usages will be considered of
"unspecified nullness."

We give [semantics] to these annotations independent of particular tools. We
don't specify what tools must do with that semantic information, nor do we
forbid them from adding to it (e.g., with flow typing in implementation code).
We merely define how to interpret our annotations in method, field, and class
declarations. This approach gives documented meaning to annotations appearing in
declarations while allowing Java code authors to use their normal toolchain to
compile code (though we do recommend code authors use additional tools to
minimize the chance of incorrect annotations). Crucially, this approach also
allows any tool to interpret annotations that appear in Java bytecode produced
by other tools.

**Note:** we have adopted precise meanings for the terms used in this document;
it will be necessary to read (or refer to) the [glossary] document in order to
interpret this information correctly.

## Semantics

In this section, we discuss how type checking should play out.

### Type hierarchy

Our nullability annotations produce the following apparent type hierarchy
\[[#80]\]:

`Foo` (written in the scope of `@NullMarked`) \
`⋖ Foo` (written outside the scope of `@NullMarked`) \
`⋖ @Nullable Foo`

It can be useful to conceptualize these similarly to [3-valued logic] \[[#33]\].
While that's workable, it can be useful to instead define "unspecified nullness"
as an existential quantification over the other two (which logically still
validates the above hierarchy). Two unrelated occurrences of unspecified
nullness can sometimes be represented as two different variables, similar to how
the Java type system handles wildcards.

The above rules make `@Nullable Object` the top (least precise) type. (Note that
`null` is _not_ the bottom type.) Here are some more examples of subtyping, with
types written in the scope of `@NullMarked`:

-   `String ⋖ Object ⋖ @Nullable Object`
-   `String ⋖ @Nullable String ⋖ @Nullable Object`
-   `null ⋖ @Nullable String ⋖ @Nullable Object`

For a given set of types, we can define their **glb** (_greatest lower bound_)
as a type from the given set that is at least as specific as all others.
Similarly, the **lub** (_least upper bound_) of a set of types is a type from
the set that is at most as specific as all others.

Finally, a **type check** (e.g., to determine assignability) for a pair of
[augmented types] includes validating **both** (a) a type check of the
[base types], handled by the off-the-shelf compiler, and (b) a type check for
nullability.

-   This specification additionally encourages allowing type checks involving
    unspecified nullness to succeed even when they are **unsound** (optionally
    with warnings, similar to "unchecked conversions" in Java generics), such as
    the following \[[#33]\]:

    -   a nullable type where a subtype of unspecified nullness is required
    -   a type of unspecified nullness where a subtype of a non-null type is
        required
    -   a type of unspecified nullness where a subtype of unspecified nullness
        is required

-   For usability reasons, many tools will not generate generate warnings/errors
    when applying unsound rules like those above. Others may generate them
    optionally, likely with the warnings/errors off by default. Even when a tool
    does report these warnings/errors, we **strongly** encourage the tool to
    permit users to suppress these warnings without suppressing other soundness
    violations.

-   When converting type components of parameterized types, their nullabilities
    should be considered invariant where their base types are.

    -   This means that "list of nullable string" and "list of non-null string"
        are not convertible to each other, but "list of non-null string" is
        convertible to `List<? extends @Nullable String>`.
    -   In addition, we encourage unsoundly allowing unspecified nullness to be
        "the same type" as any nullness, even for invariant type components
        \[[#33]\]. In particular, we encourage allowing the following type
        checks to succeed unsoundly (similar to raw type conversions) \[[#69]\]:

        -   "a list of `Bar` instances that are not null" is "the same type as"
            "a list of `Bar` instances that have unspecified nullness"
        -   "a list of `Bar` instances that are nullable" is "the same type as"
            "a list of `Bar` instances that have unspecified nullness"

### Defaulting annotations in effect

For a given type usage, we define the **defaulting annotation in effect** to be
the one located at the _nearest containing scope_ surrounding the type usage.

-   Class members are contained by classes, which may be contained by other
    class members or classes, and top-level classes are contained by packages,
    which may be contained by modules.
-   If no such defaulting annotation exists, then no defaulting annotation is in
    effect.

We call any type usage that itself carries a [recognized] type-use annotation
**explicitly annotated**.

### Parameterized types

This section directly builds on [JLS 4.5] to extend nullability to parameterized
types.

#### Parametric nullability

If a type parameter's bound is nullable, then unannotated usages of that type
parameter (inside the scope of `@NullMarked`) have _parametric nullability_.

#### Type arguments of parameterized types

Tools may wish to check that type arguments are subtypes of the corresponding
bounds' types (in addition to base type well-formedness, see [JLS 4.5]). As
usual, tools may still wish to allow (with warning if desired) unsound type
arguments involving unspecified nullness.

Specifically, a tool might reject a parameterized type with an explicit
annotation, such as `ImmutableList<@Nullable String>`, if `ImmutableList`'s type
parameter is bounded to be non-null. The tool might report an error when
encountering this case in source code and otherwise ignore the explicit
`@Nullable` annotation.

### Overriding

If a method overrides other methods according to Java language rules (see
[JLS 8.4.8.1], also cf. [JVMS 5.4.5]), then tools may wish to check that:

-   The overriding method's augmented return type is be
    return-type-substitutable for the [supermethods]' return types (corresponds
    to covariant return types for base types, an uncontroversial Java feature).

As usual, tools may wish to allow (with warning if desired) unsound declarations
involving unspecified nullness. Note that [supermethods] and [superparameters]
may be defined by members of parameterized supertypes.

### Concerns around uninitialized objects

The broad intention of nullability is that type usages specified as non-null
should at runtime only represent non-null values. Since fields have to be
initialized before they can be non-null, this turns out to be impractical in
edge cases, however. Further, this problem can affect method return values,
e.g., when methods return field values. We therefore likewise only expect
non-null guarantees to hold for instance (static, respectively) fields and
method results by the time their declaring class's constructor (static
initializer, respectively) has finished (similar to when final fields are
guaranteed to be initialized).

Note this semantics does allow for situations in which null values may be
observable in "non-null" fields and method returns, namely while objects are
under construction. We simply encourage API owners to minimize these cases for
non-private (both static and instance) fields and methods, which typically
involves not "leaking" object references outside an API until they're fully
constructed. Some tools may attempt to identify such "leaks" and may attempt to
ensure proper field initialization as defined here during object construction.

### Examples

As an example, let's consider a fragment of Guava's `ImmutableMap`:

```java
@NullMarked
public class ImmutableMap<K, V> implements Map<K, V> {
  public static <K, V> ImmutableMap<K, V> of(K key, V value);
  public @Nullable V get(@Nullable Object key);
}
```

Because of the use of `@NullMarked`, every type use in this class's declaration
is fixed to either nullable or non-null (including type-variable uses, since
their type parameters are considered implicitly bounded by non-null `Object`).

-   Can the parameters to `of()`'s be null? No, from `K` and `V`'s bounds, which
    are determined implicitly by `@NullMarked`.
-   Can `get()`'s return `null`? Yes, from its explicit annotation.
-   It is a mismatch to refer to `ImmutableMap<@Nullable String, Object>`
    because `@Nullable String` is outside of `K`'s bounds.

To illustrate wildcards, consider a method return type `ImmutableMap<? extends
@Nullable String, ?>` with no defaulting annotation in scope:

-   Can the method return a null map? That is unspecified, since no defaulting
    annotation is in scope.
-   Can the map's keys or values be null? No, because the wildcards inherit that
    bound from the bounds of `K` and `V` in `ImmutableMap`.

As another example, Guava's `Function` would be declared as follows to allow
functions that accept and/or return `null`:

```java
@NullMarked
public interface Function<F extends @Nullable Object, T extends @Nullable Object> {
  T apply(F in);
}
```

Note `F`'s and `T`'s admittedly verbose but very explicit `extends @Nullable
Object` bounds, which mean that `apply`'s parameter and result are of parametric
nullability.

Concrete `Function` implementations can still choose not to support nulls:

```java
@NullMarked
class Foo implements Function<String, Integer> {
}
```

## Discussion: Expression types

It is not the purpose of this proposal to dictate precise behavior that checkers
must follow. But we expect Java source code analyzers to want to extend our
semantics from type usages as defined above to expression types (including
expression type components).

As an example, consider a hypothetical annotated version of `java.util.List`:

```java
@NullMarked
public interface List<E extends @Nullable Object> extends Collection<E> {
  public boolean add(E element);
  public E get(int index);
}
```

Now, in client code like this:

```java
@NullMarked
public String foo(List<String> xs) {
  xs.add(null); // mismatch: add() expects non-null String
  return xs.get(0); // compatible: get() returns non-null String
}
```

Note that (because of the defaulting annotation in effect) both `foo`'s return
type's and `xs`'s `String` type argument's are non-null types. That means that,
considering `xs`'s type argument, `xs.add()`'s expected parameter type is
likewise non-null `String`, as is `xs.get()`'s return type.

Note that unlike with base types, a `null` reference is _no longer_
automatically assignable to any type:

-   It clearly isn't usable where a non-null value is required (as in the
    example above).
-   It also isn't assignable to types with parametric nullability (since their
    type parameters permit non-null instantiations).

For the latter point, consider the following example:

```java
@NullMarked
class Box<T extends @Nullable Object> {
  private final T value;

  public Box(T value) {
    this.value = value;
  }

  public T get() {
    return null;  // mismatch: T can be instantiated with a non-null qualifier.
  }
}
```

Again we do not prescribe how tools handle any of these scenarios, so tools may
be silent or issue lower-priority warnings on source lines marked "mismatch"
here. They're purely illustrative of how we imagine tools will apply semantics
to expression typing.

[#100]: https://github.com/jspecify/jspecify/issues/100
[#157]: https://github.com/jspecify/jspecify/issues/157
[#17]: https://github.com/jspecify/jspecify/issues/17
[#181]: https://github.com/jspecify/jspecify/issues/181
[#19]: https://github.com/jspecify/jspecify/issues/19
[#1]: https://github.com/jspecify/jspecify/issues/1
[#28]: https://github.com/jspecify/jspecify/issues/28
[#31]: https://github.com/jspecify/jspecify/issues/31
[#33]: https://github.com/jspecify/jspecify/issues/33
[#34]: https://github.com/jspecify/jspecify/issues/34
[#43]: https://github.com/jspecify/jspecify/issues/43
[#50]: https://github.com/jspecify/jspecify/issues/50
[#5]: https://github.com/jspecify/jspecify/issues/5
[#60]: https://github.com/jspecify/jspecify/issues/60
[#69]: https://github.com/jspecify/jspecify/issues/69
[#7]: https://github.com/jspecify/jspecify/issues/7
[#80]: https://github.com/jspecify/jspecify/issues/80
[#87]: https://github.com/jspecify/jspecify/issues/87
[3-valued logic]: https://en.wikipedia.org/wiki/Three-valued_logic
[Containment]: #containment
[JLS 1.3]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-1.html#jls-1.3
[JLS 4.10.4]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.10.4
[JLS 4.10]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.10
[JLS 4.4]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.4
[JLS 4.5.1]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.5.1
[JLS 4.5.2]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.5.2
[JLS 4.5]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.5
[JLS 4.9]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.9
[JLS 5.1.10]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-5.html#jls-5.1.10
[JLS 8.4.8.1]: https://docs.oracle.com/javase/specs/jls/se14/html/jls-8.html#jls-8.4.8.1
[JVMS 5.4.5]: https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-5.html#jvms-5.4.5
[applying operator]: #applying-operator
[augmented type]: #augmented-type
[augmented types]: #augmented-type
[base type]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i
[base types]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i
[capture conversion]: #capture-conversion
[containment]: #containment
[glossary]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit
[implementation code]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.cjuxrgo7keqs
[intersection type]: #intersection-types
[intersection types]: #intersection-types
[least convenient world]: #multiple-worlds
[most convenient world]: #multiple-worlds
[non-nullable]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.8wgyiwyvi49f
[non-root]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.j1ewrpknx869
[null-marked scope]: #null-marked-scope
[nullable]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.ejpb5ee0msjt
[nullness operator]: #nullness-operator
[nullness subtype]: #nullness-subtyping
[nullness subtyping]: #nullness-subtyping
[nullness-delegating subtyping rules for Java]: #nullness-delegating-subtyping
[nullness-delegating subtyping]: #nullness-delegating-subtyping
[nullness-subtype-establishing direct-supertype edges]: #nullness-subtype-establishing-direct-supertype-edges
[nullness-subtype-establishing path]: #nullness-subtype-establishing-path
[recognized]: #recognized-locations-for-type-use-annotations
[repeatable]: https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/annotation/Repeatable.html
[same type]: #same-type
[semantics]: #semantics
[shared folder]: https://drive.google.com/drive/folders/1vZl1odNCBncVaN7EwlwfqI05T_CHIqN-
[substitution]: #substitution
[subtype]: #subtyping
[subtyping]: #subtyping
[supermethods]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.5nvbughni6vx
[superparameters]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.m2gxs1ddzqwp
[trusted null-exclusive under every parameterization]: #trusted-null-exclusive-under-every-parameterization
[trusted null-inclusive under every parameterization]: #trusted-null-inclusive-under-every-parameterization
[type argument]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.3gm7aajjj46m
[type component]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.g7gl9fwq1tt5
[type components]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.g7gl9fwq1tt5
[type context]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.pfoww2aic35t
[type variable]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.uxek2gfsybvo
[unspecified nullness]: https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.xb9w6p3ilsq3
