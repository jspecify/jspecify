---
sidebar_position: 6
---

# Nullness Specification (draft)

This document is a draft specification for the precise semantics of our set of
annotations for nullness analysis.

:::note Advice to readers (non-normative)

The primary audience for this document is the authors of analysis tools. Some
very advanced users might find it interesting. But it would make a very poor
introduction for anyone else; instead see our **[Start Here](/docs/start-here)
page**.
:::

--------------------------------------------------------------------------------

### The word "nullable"

In this doc, we aim not to refer to whether a type "is nullable." Instead, we
draw some distinctions, creating at least four kinds of "Is it nullable?"
questions we can ask for any given type usage:

1.  Does `@Nullable` appear directly on that type usage?
2.  What is the [nullness operator] of that type usage?
3.  Is it reasonable to assume that `null` will not come "out" of it?
4.  Is it reasonable to assume that `null` cannot be put "in" to it?

### The scope of this spec

Currently, this spec does not address *when* tools must apply any part of the
spec. For example, it does not state when tools must check that the [subtyping]
relation holds.

We anticipate that tools will typically apply parts of this spec in the same
cases that they (or `javac`) already apply the corresponding parts of the Java
Language Specification. For example, if code contains the parameterized type
`List<@Nullable Foo>`, we anticipate that tools will check that `@Nullable Foo`
is a subtype of the bound of the type parameter of `List`.

However, this is up to tool authors, who may have reasons to take a different
approach. For example:

-   Java [places some restrictions that are not necessary for soundness][#49],
    and it
    [is lenient in at least one way that can lead to runtime errors][#65].

-   JSpecify annotations can be used even by tools that are not "nullness
    checkers" at all. For example, a tool that lists the members of an API could
    show the nullness of each type in the API, without any checking that those
    types are "correct."

-   Even when a tool is a "nullness checker," it might be written for another
    language, like Kotlin, with its own rules for when to perform type checks.
    Or the tool might target a future version of Java whose language features
    would not be covered by this version of this spec.

Note also that this spec covers only nullness information *from JSpecify
annotations*. Tools may have additional sources of information. For example, a
tool may recognize additional annotations. Or a tool may omit the concept of
`UNSPECIFIED` and apply a policy that type usages like `Object` are always
non-nullable.

### That's all!

On to the spec.

--------------------------------------------------------------------------------

## Normative and non-normative sections

This document contains some non-normative comments to emphasize points or to
anticipate likely questions. Those comments are set off as block quotes.

> This is an example of a non-normative comment.

This document also links to other documents. Those documents are non-normative,
except for when we link to the Java Language Specification to defer to its
rules.

## Relationship between this spec and JLS {#concept-references}

When a rule in this spec refers to any concept that is defined in this spec (for
example, [substitution] or [containment]), apply this spec's definition (as
opposed to other definitions, such as the ones in the Java Language
Specification (JLS)).

Additionally, when a rule in this spec refers to a JLS rule that in turn refers
to a concept that is defined in this spec, likewise apply this spec's
definition.

In particular, when a JLS rule refers to types, apply this spec's definition of
[augmented types] \(as opposed to [base types]).

This specification covers all JLS constructs up to [Java SE 23].

## Base type

A *base type* is a type as defined in [JLS 4].

> JLS 4 does not consider a type-use annotation to be a part of the type it
> annotates, so neither does our concept of "base type."

## Type components

A *type component* of a given type is a type that transitively forms some part
of that type. Specifically, a type component is one of the following:

-   a non-wildcard type argument
-   a wildcard bound
-   an array component type
-   an enclosing type
-   an element of an intersection type
-   the entire type

## Nullness operator

A nullness operator is one of four values:

-   `UNION_NULL`
-   `NO_CHANGE`
-   `UNSPECIFIED`
-   `MINUS_NULL`

> The informal meaning of the operators is:
>
> -   `UNION_NULL`: This is the operator produced by putting `@Nullable` on a
>     type usage.
>     -   The type usage `String UNION_NULL` includes `"a"`, `"b"`, `"ab"`,
>         etc., plus `null`.
>     -   The type-variable usage `T UNION_NULL` includes all members of the
>         type argument substituted in for `T`, plus `null` if it was not
>         already included.
> -   `MINUS_NULL`: This is the operator produced by putting `@NonNull` on a
>     type usage.
>     -   The type usage `String MINUS_NULL` includes `"a"`, `"b"`, `"ab"`,
>         etc., without including `null`.
>     -   The type-variable usage `T MINUS_NULL` includes all members of the
>         type argument substituted in for `T` except that it does not include
>         `null` even when the type argument does.
> -   `NO_CHANGE`: This operator is important on type-variable usages, where it
>     means that the nullness of the type comes from the type argument.
>     -   The type usage `String NO_CHANGE` includes `"a"`, `"b"`, `"ab"`, etc.,
>         without including `null`. (This is equivalent to `String MINUS_NULL`.)
>     -   The type-variable usage `T NO_CHANGE` includes exactly the members of
>         the type argument substituted in for `T`: If `null` was a member of
>         the type argument, then it is a member of `T NO_CHANGE`. If it was not
>         a member of the type argument, then it is not a member of `T
>         NO_CHANGE`.
> -   `UNSPECIFIED`: This is the operator produced by "completely unannotated
>     code"—outside a [null-marked scope] and with no annotation on the type.
>     -   The type usage `String UNSPECIFIED` includes `"a"`, `"b"`, `"ab"`,
>         etc., but whether `null` should be included is not specified.
>     -   The type-variable usage `T UNSPECIFIED` includes all members of `T`,
>         except that there is no specification of whether `null` should be
>         added to the set (if it is not already a member), removed (if it is
>         already a member), or included only when the substituted type argument
>         includes it.

## Augmented type

An augmented type consists of a [base type] and a [nullness operator]
corresponding to *each* of its [type components].

> Arguably, an augmented type with nullness operator `UNSPECIFIED` is better
> understood not as representing "a type" but as representing a *lack* of the
> nullness portion of the type.

For our purposes, base types (and thus augmented types) include not just class
and interface types, array types, and type variables but also
[intersection types] and the null type.

> This spec aims to define rules for augmented types compatible with those that
> the JLS defines for base types.
>
> Accordingly, in almost all cases, this spec agrees with the JLS's rules when
> specifying what *base* types appear in a piece of code. It makes an exception
> for ["Bound of an unbounded wildcard,"](#unbounded-wildcard) for which it
> specifies a bound of `Object` that the JLS does not specify.

When this spec uses single capital letters in `code font`, they refer to
augmented types (unless otherwise noted). This is in contrast to the JLS, which
typically uses them to refer to base types.

When this spec refers to "the nullness operator of" a type `T`, it refers
specifically to the nullness operator of the type component that is the entire
type `T`, without reference to the nullness operator of any other type that is a
component of `T` or has `T` as a component.

> For example, "the nullness operator of `List<Object>`" refers to whether the
> list itself may be `null`, not whether its elements may be.

## Details common to all annotations

For all named annotations referred to by this spec:

-   The Java package name is `org.jspecify.annotations`.
-   The Java module name is `org.jspecify`.
-   The Maven artifact is `org.jspecify:jspecify`.

All annotations have runtime retention. None of the annotations are marked
[repeatable].

## The type-use annotations

We provide two parameterless type-use annotations: `@Nullable` and `@NonNull`.

### Recognized locations for type-use annotations {#recognized-type-use}

A location is a *recognized* location for our type-use annotations in the
circumstances detailed below. If our type-use annotations appear in any other
location, they have no meaning.

> When analyzing source code, tools are encouraged to offer an option to issue
> an error for an annotation in an unrecognized location. When reading
> *bytecode*, however, tools may be best off ignoring an annotation in an
> unrecognized location.

The following locations are recognized except when overruled by one of the
exceptions in the subsequent sections:

-   the return type of a method

-   a formal parameter type of a method or constructor, as defined in
    [JLS 8.4.1]

    > This excludes the receiver parameter but includes variadic parameters.
    > Specifically, you can add `@Nullable` before the `...` token to indicate
    > that a variadic method accepts `null` arrays: `void foo(String @Nullable
    > ... strings)`.

-   a field type

-   a record component type

-   a type parameter upper bound

-   a non-wildcard type argument

-   a wildcard bound

-   an array component type

-   an array creation expression

However, the type-use annotation is unrecognized in any of the following cases:

-   a type usage of a primitive type, since those are intrinsically non-nullable

-   any component of a return type in an annnotation interface, since those are
    intrinsically non-nullable

-   type arguments of a receiver parameter's type

-   any component of the reference type(s) in a
    [cast expression][JSL 15.16]

-   any component of the type after the `instanceof`
    [type comparison operator][JLS 15.20.2]

-   any component in a [pattern]

    > We are likely to revisit this rule in the future.

All locations that are not explicitly listed as recognized are unrecognized.

> Other notable unrecognized annotations include:
>
> -   class declaration
>
>     > For example, the annotation in `public @Nullable class Foo {}` is in an
>     > unrecognized location.
>
> -   type-parameter declaration or a wildcard *itself*
>
> -   local variable's root type
>
>     > For example, `@Nullable List<String> strings = ...` or `String @Nullable
>     > [] strings = ...` have unrecognized annotations.
>
> -   some additional intrinsically non-nullable locations:
>
>     -   supertype in a class declaration
>
>     -   thrown exception type
>
>     -   exception parameter type
>
>     -   enum constant declaration
>
>     -   receiver parameter type
>
>     -   object creation expression
>
>         > For example, `new @Nullable ArrayList<String>()` has an unrecognized
>         > annotation.
>
>     -   outer type qualifying an inner type
>
>         > For example, the annotation in `@Nullable Foo.Bar` is unrecognized
>         > because it is attached to the outer type `Foo`.
>         >
>         > (Note that `@Nullable Foo.Bar` is a *Java* syntax error when `Bar`
>         > is a *static* type. If `Bar` is a non-static type, then Java permits
>         > the code. So JSpecify tools have the oppotunity to reject it, given
>         > that the author probably intended `Foo.@Nullable Bar`.)
>         >
>         > Every outer type is intrinsically non-nullable because every
>         > instance of an inner class has an associated instance of the outer
>         > class.
>
> But note that types "inside" some of these locations can still be recognized,
> such as a *type argument* of a supertype.

## The declaration annotations

We provide two parameterless declaration annotations: `@NullMarked` and
`@NullUnmarked`.

### Recognized locations for declaration annotations {#recognized-declaration}

Our declaration annotations are specified to be *recognized* when applied to the
locations listed below:

-   A *named* class.
-   A package.
-   A module (for `@NullMarked` only, not `@NullUnmarked`).
-   A method or constructor.

> *Not* a field or a record component.

If our declaration annotations appear in any other location, they have no
meaning.

## Null-marked scope

To determine whether a type usage appears in a null-marked scope:

Iterate over all the declarations that enclose the type usage, starting from the
innermost.

"Enclosing" is defined as follows:

-   Each class member is enclosed by a class.
-   Each non-top-level class is enclosed by a class or class member.
-   Each top-level class is enclosed by a package.
-   Each package may be enclosed by a module.
-   Modules are not enclosed by anything.

> Packages are *not* enclosed by "parent" packages.

> This definition of "enclosing" largely matches
> [the definition in the Java compiler API](https://docs.oracle.com/en/java/javase/23/docs/api/java.compiler/javax/lang/model/element/Element.html#getEnclosingElement\(\)).
> The JSpecify definition differs slightly by skipping type-parameter
> declarations (which cannot be annotated with declaration annotations) and by
> defining that there exists a series of enclosing declarations for any type
> usage, not just for a declaration.

At each declaration that is a [recognized](#recognized-declaration) location,
check the following rules in order:

-   If the declaration is annotated with `@NullMarked` and *not* with
    `@NullUnmarked`, the type usage is in a null-marked scope.
-   If the declaration is annotated with `@NullUnmarked` and *not* with
    `@NullMarked`, the type usage is *not* in a null-marked scope.
-   If the declaration is a top-level class annotated with `@kotlin.Metadata`,
    then the type usage is *not* in a null-marked scope.

> If a given declaration is annotated with both `@NullMarked` and
> `@NullUnmarked`, these rules behave as if neither annotation is present.

If none of the enclosing declarations meet either rule, then the type usage is
*not* in a null-marked scope.

## Augmented type of a type usage appearing in code {#augmented-type-of-usage}

This section defines how to determine the [augmented types] of most type usages
in source code or bytecode where JSpecify nullness annotations are [recognized].

> The rules here should be sufficient for most tools that care about nullness
> information, from build-time nullness checkers to runtime dependency-injection
> tools. However, tools that wish to examine class files in greater detail, such
> as to insert runtime null checks by rewriting bytecode, may encounter some
> edge cases. For example, `synthetic` methods may not have accurate annotations
> in their signatures. The same goes for information about implementation code,
> such as local-variable types.

Because the JLS already has rules for determining the [base type] for a type
usage, this section covers only how to determine its [nullness operator].

To determine the nullness operator, apply the following rules in order. Once one
condition is met, skip the remaining conditions.

-   If the type usage is the type of the field corresponding to an enum
    constant, its nullness operator is `MINUS_NULL`.

    > In source code, there is nowhere in the Java grammar for the type of an
    > enum constant to be written. Still, enum constants have a type, which is
    > made explicitly visible in the compiled class file.

-   If the type usage is a component of a return type in an annnotation
    interface, its nullness operator is `MINUS_NULL`.

-   If the type usage is annotated with `@Nullable` and *not* with `@NonNull`,
    its nullness operator is `UNION_NULL`.

-   If the type usage is annotated with `@NonNull` and *not* with `@Nullable`,
    its nullness operator is `MINUS_NULL`.

    > If the type usage is annotated with both `@Nullable` and `@NonNull`, these
    > rules behave as if neither annotation is present.

-   If the type usage is the parameter of `equals(Object)` in a subclass of
    `java.lang.Record`, then its nullness operator is `UNION_NULL`.

    > This special case handles the fact that the Java compiler automatically
    > generates an implementation of `equals` in `Record` but does not include a
    > `@Nullable` annotation on its parameter, even when the class is
    > `@NullMarked`.

-   If the type usage appears in a [null-marked scope], its nullness operator is
    `NO_CHANGE`.

-   Its nullness operator is `UNSPECIFIED`.

> The choice of nullness operator is *not* affected by any nullness operator
> that appears in a corresponding location in a supertype. For example, if one
> type declares a method whose return type is annotated `@Nullable`, and if
> another type overrides that method but does not declare the return type as
> `@Nullable`, then the override's return type will *not* have nullness operator
> `UNION_NULL`.

> If tool authors prefer, they can safely produce `MINUS_NULL` in any case in
> which it is equivalent to `NO_CHANGE`. For example, there is no difference
> between `Foo NO_CHANGE` and `Foo MINUS_NULL` for any class type `Foo` (nor for
> any array type or the null type). The difference *is* significant for
> intersection types, type variables, and union types.

## Augmented type of an intersection type {#intersection-types}

> Technically speaking, the JLS does not define syntax for an intersection type.
> Instead, it defines a syntax for type parameters and casts that supports
> multiple types. Then the intersection type is derived from those. Intersection
> types can also arise from operations like [capture conversion]. See [JLS 4.9].
>
> One result of this is that it is never possible for a programmer to write an
> annotation "on an intersection type."

This spec assigns a [nullness operator] to each individual element of an
intersection type, following our normal rules for type usages. It also assigns a
nullness operator to the intersection type as a whole. The nullness operator of
the type as a whole is always `NO_CHANGE`.

> This lets us provide, for every [base type], a rule for computing its
> [augmented type]. But we require `NO_CHANGE` so as to avoid questions like
> whether "a `UNION_NULL` intersection type whose members are `Foo UNION_NULL`
> and `Bar UNION_NULL`" is a subtype of "a `NO_CHANGE` intersection type with
> those same members." Plus, it would be difficult for tools to output the
> nullness operator of an intersection type in a human-readable way.

> To avoid ever creating an intersection type with a nullness operator other
> than `NO_CHANGE`, we define special handling for intersection types under
> ["Applying a nullness operator to an augmented type."][applying operator]

## Bound of an "unbounded" wildcard {#unbounded-wildcard}

In source, an unbounded wildcard is written as `<?>`. This section does *not*
apply to `<? extends Object>`, even though that is often equivalent to `<?>`.

> See [JLS 4.5.1].

In bytecode, such a wildcard is represented as a wildcard type with an empty
list of upper bounds and an empty list of lower bounds. This section does *not*
apply to a wildcard with any bounds in either list, even a sole upper bound of
`Object`.

> For a wildcard with an explicit bound of `Object` (that is, `<? extends
> Object>`, perhaps with an annotation on `Object`), instead apply
> [the normal rules](#augmented-type-of-usage) for the explicit bound type.

If an unbounded wildcard appears in a [null-marked scope], then it has a single
upper bound whose [base type] is `Object` and whose [nullness operator] is
`UNION_NULL`.

If an unbounded wildcard appears outside a null-marked scope, then it has a
single upper bound whose base type is `Object` and whose nullness operator is
`UNSPECIFIED`.

> In both cases, we specify a bound that does not exist in the source or
> bytecode, deviating from the JLS. Because the base type of the bound is
> `Object`, this should produce no user-visible differences except to tools that
> implement JSpecify nullness analysis.

Whenever a JLS rule refers specifically to `<?>`, disregard it, and instead
apply the rules for `<? extends T>`, where `T` has a base type of `Object` and
the nullness operator defined by this section.

## Bound of an `Object`-bounded type parameter {#object-bounded-type-parameter}

In source, an `Object`-bounded type parameter can be writen in either of two
ways:

-   `<T>`
-   `<T extends Object>` with no JSpecify nullness type annotations on the bound

> See [JLS 4.4].

In bytecode, `<T>` and `<T extends Object>` are both represented as a type
parameter with a single upper bound, `Object`, and no JSpecify nullness type
annotations on the bound.

If an `Object`-bounded type parameter appears in a [null-marked scope], then its
bound has a [base type] of `Object` and a [nullness operator] of `NO_CHANGE`.

> Note that this gives `<T>` a different bound than `<?>` (though only in a
> null-marked scope).

If an `Object`-bounded type parameter appears outside a null-marked scope, then
its bound has a base type of `Object` and a nullness operator of `UNSPECIFIED`.

> All these rules match the behavior of
> [our normal rules](#augmented-type-of-usage) for determining the
> [augmented type] of the bound `Object`. The only "special" part is that we
> consider the source code `<T>` to have a bound of `Object`, just as it does
> when compiled to bytecode.

## Augmented null types {#null-types}

The JLS refers to "the null type." In this spec, we assign a [nullness operator]
to all types, including the null type. This produces multiple null types:

-   the null [base type] with nullness operator `NO_CHANGE`: the
    "bottom"/"nothing" type used in [capture conversion]

    > No value has this type, not even `null` itself.

-   the null base type with nullness operator `MINUS_NULL`

    > This is equivalent to the previous type. Tools may use the two
    > interchangeably.

-   the null base type with nullness operator `UNION_NULL`: the type of the null
    reference

-   the null base type with nullness operator `UNSPECIFIED`

    > This may be relevant only in implementation code.

## Multiple "worlds" {#multiple-worlds}

Some of the rules in this spec come in two versions: One version requires a
property to hold "in all worlds," and the other requires it to hold only "in
some world."

Tool authors may choose to implement neither, either, or both versions of the
rules.

> Our goal is to allow tools and their users to choose their desired level of
> strictness in the presence of `UNSPECIFIED`. The basic idea is that, every
> time a tool encounters a type component with the nullness operator
> `UNSPECIFIED`, it has the option to fork off two "worlds": one in which the
> operator is `UNION_NULL` and one in which it is `NO_CHANGE`.
>
> In more detail: When tools lack a nullness specification for a type, they may
> choose to assume that either of the resulting worlds may be the "correct"
> specification. The all-worlds version of a rule, by requiring types to be
> compatible in all possible worlds, holds that types are incompatible unless it
> has enough information to prove they are compatible. The some-world version,
> by requiring types to be compatible only in *some* world, holds that types are
> compatible unless it has enough information to prove they are incompatible.
> (By behaving "optimistically," the some-world version is much like Kotlin's
> rules for "platform types.")
>
> Thus, a strict tool might choose to implement the all-worlds version of rules,
> and a lenient tool might choose to implement the some-world version. Yet
> another tool might implement both and let users select which rules to apply.
>
> Still another possibility is for a tool to implement both versions and to use
> that to distinguish between "errors" and "warnings." Such a tool might always
> first process code with the all-worlds version and then with the some-world
> version. If the tool detects, say, an out-of-bounds type argument in both
> cases, the tool would produce an error. But, if the tool detects such a
> problem with the all-worlds version but not with the some-world version, the
> tool would produce a warning. Under this scheme, a warning means roughly that
> "There is some way that the code could be annotated that would produce an
> error here."

Rules behave identically under both versions except where otherwise specified.

> A small warning: To implement the full some-world rules, a tool must also
> implement at least part of the all-worlds rules. Those rules are required as
> part of [substitution].

### Propagating how many worlds a relation must hold in {#propagating-multiple-worlds}

When one rule in this spec refers to another, it refers to the same version of
the rule. For example, when the rules for [containment] refer to the rules for
[subtyping], the some-world containment relation refers to the some-world
subtyping relation, and the all-worlds containment relation refers to the
all-worlds subtyping relation.

This meta-rule applies except when a rule refers explicitly to a particular
version of another rule.

## Comfortable with a given nullness operator {#comfortable}

There is *reason to be comfortable treating a given [nullness operator] `g` like
a target nullness operator `t`* if either of the following conditions holds:

-   `g` is `t`.

-   `g` is `UNSPECIFIED`, *and* this is the [some-world] version of the rule.

> The purpose of "comfortable" (and "[worried]") is to offer tools the option to
> treat null-unmarked code either optimistically or pessimistically. Tool
> authors make this choice by choosing how to handle "[multiple worlds]."
>
> Suppose that a tool wants to determine whether it will allow `null` to be
> assigned to a field of a given type. To do so, it can ask whether it is
> "comfortable" treating the field type's nullness operator like `UNION_NULL`.
>
> -   If the nullness operator *is* `UNION_NULL`, then the assignment should
>     clearly be allowed.
> -   If the nullness operator is `UNSPECIFIED`, then it is possible that the
>     operator "ought to be" `UNION_NULL`. A lenient tool might allow the
>     assignment anyway, while a strict tool might not.

## Worried about a given nullness operator {#worried}

There is *reason to be worried that a given [nullness operator] `g` is a target
nullness operator `t`* if either of the following conditions holds:

-   `g` is `t`.

-   `g` is `UNSPECIFIED`, *and* this is the [all-worlds] version of the rule.

> "Worried" is the complementary attitude to "[comfortable]" above.
>
> Suppose that a tool wants to determine whether to allow an expression of a
> given type to be dereferenced. To do so, it can ask whether it should be
> "worried" that the type's nullness operator is `UNION_NULL`.
>
> -   If the nullness operator *is* `UNION_NULL`, then the dereference clearly
>     should not be allowed.
> -   If the nullness operator is `UNSPECIFIED`, then it is possible that the
>     operator "ought to be" `UNION_NULL`. A lenient tool might allow the
>     dereference anyway, while a strict tool might not.

## Same type

`S` and `T` are the same type if `S` is a [subtype] of `T` and `T` is a subtype
of `S`.

The same-type relation is *not* defined to be reflexive or transitive.

> For more discussion of reflexive and transitive relations, see the comments
> under [nullness subtyping].

> Compare [JLS 4.3.4]. Note that our definition of "same type" applies to all
> kinds of augmented types, including cases like the augmented null types.

## Subtyping

`A` is a subtype of `F` if all of the following conditions are met:

-   The base type of `A` is a subtype of the base type of `F` according to
    Java's subtyping rules.

    > This condition is rarely of direct interest to tools' JSpecify support: If
    > code does not pass Java's subtyping rules, then most JSpecify tools will
    > never see it.

-   `A` is a [nullness subtype] of `F`.

    > This condition suffices to establish subtyping for most cases.

-   `A` is a subtype of `F` according to the
    [nullness-delegating subtyping rules for Java].

    > This condition is necessary only for types that have subcomponents—namely,
    > parameterized types and arrays. And it essentially says "Check the
    > nullness-subtype condition on subcomponents as appropriate."

## Nullness subtyping

`A` is a nullness subtype of `F` if any of the following conditions are met:

> Nullness subtyping asks the question: If `A` includes `null`, does `F` also
> include `null`? There are four cases in which this is true, two easy and two
> hard:

-   `F` is [null-inclusive under every parameterization].

    > This is the first easy case: `F` always includes `null`.

-   `A` is [null-exclusive under every parameterization].

    > This is the second easy case: `A` never includes `null`.

-   `A` has a [nullness-subtype-establishing path] to any type whose base type
    is the same as the base type of `F`, and there is *not* reason to be
    [worried] that `F` has [nullness operator] `MINUS_NULL`.

    > This is the first hard case: A given type-variable usage does not
    > necessarily always include `null`, nor does it necessarily always exclude
    > `null`. (For example, consider a usage of `E` inside `ArrayList<E>`.
    > `ArrayList` may be instantiated as either an `ArrayList<@Nullable String>`
    > or an `ArrayList<String>`.)
    >
    > Subtyping questions for type-variable usages are more complex. For
    > example:
    >
    > -   `E` is a nullness subtype of `E`; `@Nullable E` is not.
    > -   Similarly, if `<F extends E>` (in null-marked code), then `F` is a
    >     nullness subtype of `E`. But if `<F extends @Nullable E>`, it is not.
    > -   `E` is a nullness subtype of `E` but not of `@NonNull E`.
    >
    > When some types have unspecified nullness, the rules become more complex
    > still:
    >
    > -   A declaration like `<F extends E>` might or might not be "intended" to
    >     be `<F extends @Nullable E>`. Depending on what was indended, `F`
    >     *might* be intended to be a nullness subtype of `E`.
    > -   Or that declaration might be "intended" to be `<F extends @NonNull
    >     E>`. In that [world], `F` would be not only a nullness subtype of `E`
    >     but a nullness subtype of *all* types, since it would be
    >     null-exclusive under every parameterization.

-   `F` is a type-variable usage that meets *both* of the following conditions:

    -   There is *not* reason to be worried that it has nullness operator
        `MINUS_NULL`.

    -   `A` is a nullness subtype of its lower bound.

    > This is the second hard case: It covers type variables that are introduced
    > by capture conversion of `? super` wildcards.
    >
    > In short, whether you have a `Predicate<? super String>`, a `Predicate<?
    > super @Nullable String>`, or unannotated code that does not specify the
    > nullness operator for the bound, you can always pass its `test` method a
    > `String`. (If you want to pass a `@Nullable String`, then you will need
    > for the bound to be [null-inclusive under every parameterization]. The
    > existence of the null-inclusiveness rule frees this current rule from
    > having to cover that case.)

> A further level of complexity in all this is `UNSPECIFIED`. For example, in
> the [some-world] version of the following rules, a type with nullness operator
> `UNSPECIFIED` can be both null-_inclusive_ under every parameterization and
> null-_exclusive_ under every parameterization, albeit in different [worlds].

Nullness subtyping (and thus subtyping itself) is *not* defined to be reflexive
or transitive.

> If we defined nullness subtyping to be reflexive, then `String UNSPECIFIED`
> would be a subtype of `String UNSPECIFIED`, even under the [all-worlds] rules.
> In other words, we would be saying that unannotated code is always free from
> nullness errors. That is clearly false. (Nevertheless, lenient tools will
> choose not to issue errors for such code. They can do this by implementing the
> [some-world] rules.)
>
> If we defined nullness subtyping to be transitive, then `String UNION_NULL`
> would be a subtype of `String NO_CHANGE` under the some-world rules. That
> would happen because of a chain of subtyping rules:
>
> -   `String UNION_NULL` is a subtype of `String UNSPECIFIED`.
>
> -   `String UNSPECIFIED` is a subtype of `String NO_CHANGE`.
>
> Therefore, `String UNION_NULL` is a subtype of `String NO_CHANGE`.
>
> Yes, it is pretty terrible for something called "subtyping" not to be
> reflexive or transitive. A more accurate name for this concept would be
> "consistent," a term used in gradual typing. However, we use "subtyping"
> anyway. In our defense, we need to name multiple concepts, including not just
> subtyping but also the [same-type] relation and [containment]. If we were to
> coin a new term for each, tool authors would need to mentally map between
> those terms and the analogous Java terms. (Still, yes: Feel free to read terms
> like "subtyping" as if they have scare quotes around them.)
>
> Subtyping does end up being transitive when the relation is required to hold
> in all worlds. And it does end up being reflexive when the relation is
> required to hold only in [some world]. We do not state those properties as
> rules for two reasons: First, they arise naturally from the definitions.
> Second, we do not want to suggest that subtyping is reflexive and transitive
> under both versions of the rule.

Contrast this with our [nullness-delegating subtyping] rules and [containment]
rules: Each of those is defined as a transitive closure. However, this is
incorrect, and we should fix it: Transitivity causes the same problem there as
it does here: `List<? extends @Nullable String>` ends up as a subtype of `List<?
extends String>` because of a chain of subtyping rules that uses `String
UNSPECIFIED` as part of the intermediate step. Luckily, tool authors that set
out to implement transitivity for these two rules are very unlikely to write
code that "notices" this chain. So, in practice, users are likely to see the
"mostly transitive" behavior that we intend, even if we have not found a way to
formally specify it yet.

## Null-inclusive under every parameterization

A type is null-inclusive under every parameterization if it meets any of the
following conditions:

-   There is reason to be [comfortable] treating its [nullness operator] like
    `UNION_NULL`.

-   It is an [intersection type] whose elements all are null-inclusive under
    every parameterization.

-   It is a type variable that meets *both* of the following conditions:

    -   There is *not* reason to be [worried] that it has nullness operator
        `MINUS_NULL`.

    -   Its lower bound is null-inclusive under every parameterization.

    > This third case is probably irrelevant in practice: It covers `? super
    > @Nullable Foo`, which is already covered by the rules for
    > [nullness subtyping]. It is included here in case some tool has reason to
    > check whether a type is null-inclusive under every parameterization
    > *outside* of a check for nullness subtyping.

## Null-exclusive under every parameterization

> This is a straightforward concept ("never includes `null`"), but it is not as
> simple to implement as the null-_inclusive_ rule was. This null-_exclusive_
> rule has to cover cases like `String`, `E` (where `<E extends Object>`), and
> `E` (where `<E extends @Nullable Object>` but nearby code has performed a null
> check on the expression). The case of `<E extends Object>` is an example of
> why the following rule requires looking for a "path."

A type is null-exclusive under every parameterization if it has a
[nullness-subtype-establishing path] to either of the following:

-   any type whose [nullness operator] there is reason to be [comfortable]
    treating as `MINUS_NULL`

    > This covers an easy case: A type usage never includes `null` if it is
    > annotated with `@NonNull`.

-   any augmented class or array type

    > This rule refers specifically to a "class or array type," as distinct from
    > other types like type variables and [intersection types].

> When code dereferences an expression, we anticipate that tools will check
> whether the expression is null-exclusive under every parameterization.

## Nullness-subtype-establishing path

> Note that this definition is used both by the definition of
> [null-exclusive under every parameterization] and by the third condition in
> the definition [nullness subtyping] itself (the "type-variable case").

`A` has a nullness-subtype-establishing path to `F` if both of the following
hold:

-   There is *not* reason to be [worried] that the nullness operator of `A` is
    `UNION_NULL`.
-   There is a path from `A` to `F` through
    [nullness-subtype-establishing direct-supertype edges].

    > The path may be empty. That is, `A` has a nullness-subtype-establishing
    > path to itself—as long as it has one of the required nullness operators.

## Nullness-subtype-establishing direct-supertype edges

> This section defines the supertypes for a given type—but limited to those that
> fill the gaps in our nullness checking of "top-level" types. For example,
> there is no need for the rules to reflect that `String NO_CHANGE` extends
> `Object NO_CHANGE`: If we have established that a type has a path to `String
> NO_CHANGE`, then we already know that it is
> [null-exclusive under every parameterization], based on the rules above, and
> that is enough to prove subtyping. And if we *have not* established that, then
> the `String`-`Object` edge is not going to change that.
>
> Thus, the rules here are restricted to type variables and intersection types,
> whose supertypes may have nullness annotations.

`T` has nullness-subtype-establishing direct-supertype edges to all the
following types, subject to the exception given below:

-   if `T` is an augmented [intersection type]: all the intersection type's
    elements

-   if `T` is an augmented type variable: all the corresponding type parameter's
    upper bounds

-   otherwise: no nodes

Exception: `T` does *not* have edges to any types whose [nullness operator]
there is reason to be [worried] is `UNION_NULL`.

## Nullness-delegating subtyping rules for Java {#nullness-delegating-subtyping}

> Recall that this rule exists to handle subcomponents of types—namely, type
> arguments and array component types. It essentially says "Check nullness
> subtyping for subcomponents as appropriate."

The Java subtyping rules are defined in [JLS 4.10]. (Each rule takes a type as
input and produces zero or more direct supertypes as outputs.) We add to them as
follows:

-   [As always](#concept-references), interpret the Java rules as operating on
    [augmented types], not [base types]. This raises the question of *how* to
    extend these particular rules to operate on augmented types. The answer has
    two parts:

    -   The first part of the answer applies only to the nullness operator *"of
        the type."* ([As always](#augmented-type), this means the nullness
        operator of the type component that is the entire type.)

        And the first part of the answer is: No matter what nullness operator
        the input augmented type has, the rules still apply, and they still
        produce the same direct supertypes.

        > Thanks to that rule, the nullness operator of any *output* type is
        > never read by the subtyping rules.

        So, when computing output types, tools may produce them with *any*
        nullness operator.

        > Essentially, this rule says that the top-level types do not matter:
        > They have already been checked by the [nullness subtyping] check.
        >
        > For simplicity, we recommend producing a nullness operator of
        > `NO_CHANGE`: That operator is valid for all types, including
        > [intersection types].

    -   The nullness operators of *subcomponents* of the augmented types *do*
        matter. For example, some Java rules produce subtypes only if
        subcomponents meet certain requirements.
        [As always](#concept-references), check those requirements by applying
        *this spec's* definitions.

        > Those definitions (like [containment]) refer back to definitions (like
        > [nullness subtyping]) that use the nullness operators of the
        > subcomponents in question.

-   When the Java array rules require one type to be a *direct* supertype of
    another, consider the direct supertypes of `T` to be *every* type that `T`
    is a [subtype] of.

## Containment

The Java rules are defined in [JLS 4.5.1]. We add to them as follows:

-   Disregard the two rules that refer to a bare `?`. Instead, treat `?` like `?
    extends Object`, where the [nullness operator] of the `Object` bound is
    specified by ["Bound of an unbounded wildcard."](#unbounded-wildcard)

    > This is just a part of our universal rule to treat a bare `?` like `?
    > extends Object`.

-   The rule written specifically for `? extends Object` applies only if there
    is reason to be [comfortable] treating the nullness operator of the `Object`
    bound as `UNION_NULL`.

-   When the JLS refers to the same type `T` on both sides of a rule, the rule
    applies if and only if this spec defines the two types to be the
    [same type].

## Substitution

> Substitution on Java base types barely requires an explanation: See [JLS 1.3].
> Substitution on [augmented types], however, is trickier: If `Map.get` returns
> `V UNION_NULL`, and if a user has a map whose value type is `String
> UNSPECIFIED`, then what does its `get` method return? Naive substitution would
> produce `String UNSPECIFIED UNION_NULL`. To reduce that to a proper augmented
> type with a single nullness operator, we define this process.

To substitute each type argument `Aᵢ` for each corresponding type parameter
`Pᵢ`:

For every type-variable usage `V` whose [base type] is `Pᵢ`, replace `V` with
the output of the following operation:

-   If `V` is [null-exclusive under every parameterization] in [all worlds],
    then replace it with the output of [applying][applying operator]
    `MINUS_NULL` to `Aᵢ`.

    > This is the one instance in which a rule specifically refers to the
    > [all-worlds] version of another rule. Normally,
    > [a rule "propagates" its version to other rules](#propagating-multiple-worlds).
    > But in this instance, the null-exclusivity rule (and all rules that it in
    > turn applies) are the [all-worlds] versions.
    >
    > We may someday have another such rule for computing least upper bounds, as
    > demonstrated in
    > https://github.com/jspecify/jspecify-reference-checker/pull/197.

    > The purpose of this special case is to improve behavior in "the
    > `ImmutableList.Builder` case": Because `ImmutableList.Builder.add` always
    > throws `NullPointerException` for a null argument, we would like for
    > `add(null)` to be a compile error, even under lenient tools.
    > Unfortunately, without this special case, lenient tools could permit
    > `add(null)` in unannotated code. For an example, read on.
    >
    > Consider an unannotated user of `ImmutableList.Builder<Foo> builder`. Its
    > type argument `Foo` will have a [nullness operator] of `UNSPECIFIED`.
    > Without this special case, the parameter of `builder.add` would have a
    > nullness operator of `UNSPECIFIED`, too. Then, when a lenient tool would
    > check whether the [some-world] subtyping relation holds for
    > `builder.add(null)`, it would find that it does.
    >
    > To solve this, we need a special case for substitution for null-exclusive
    > type parameters like the one on `ImmutableList.Builder`. That special case
    > needs to produce a type with a nullness operator other than `UNSPECIFIED`.
    > One valid option is to produce `NO_CHANGE`; we happened to choose
    > `MINUS_NULL`.
    >
    > The choice between `NO_CHANGE` and `MINUS_NULL` makes little difference
    > for the parameter types of `ImmutableList.Builder`, but it can matter more
    > for other APIs' *return types*. For example, consider `@NullMarked class
    > Foo<E extends @Nullable Object>`, which somewhere uses the type
    > [`FluentIterable<E>`]. `FluentIterable` has a method `Optional<E>
    > first()`. Even when `E` is a type like `String UNION_NULL` (or `String
    > UNSPECIFIED`), we know that `first().get()` will never return `null`. To
    > surface that information to tools, we need to define our substitution rule
    > to return `E MINUS_NULL`: If we instead used `E NO_CHANGE`, then the
    > return type would look like it might include `null`.

-   Otherwise, replace `V` with the output of applying the nullness operator of
    `V` to `Aᵢ`.

## Applying a nullness operator to an augmented type {#applying-operator}

The process of applying a [nullness operator] requires two inputs:

-   the nullness operator to apply
-   the [augmented type] \(which, as always, includes a nullness operator for
    that type) to apply it to

The output of the process is an augmented type.

To determine the output, apply the following rules in order.

-   If the nullness operator to apply is `NO_CHANGE`, then the output augmented
    type is the input augmented type.

-   Otherwise, if the input augmented type is an [intersection type], then the
    output is also an intersection type. For every element `Tᵢ` of the input
    type, the output type has an element that is the output of applying the
    desired nullness operator to `Tᵢ`.

-   Otherwise, the output is a type that is the same as the input augmented type
    except with its nullness operator set to the nullness operator to apply.

## Capture conversion

The Java rules are defined in [JLS 5.1.10]. We add to them as follows:

-   The parameterized type that is the output of the conversion has the same
    [nullness operator] as the parameterized type that is the input type.

-   Disregard the JLS rule about `<?>`. Instead, treat `?` like `? extends
    Object`, where the [nullness operator] of the `Object` bound is specified by
    ["Bound of an unbounded wildcard."](#unbounded-wildcard)

    > This is just a part of our universal rule to treat a bare `?` like `?
    > extends Object`.

-   Whenever the rules generate a usage of a fresh type variable, that usage has
    nullness operator `NO_CHANGE`.

-   When a rule generates a lower bound that is the null type, we specify that
    its nullness operator is `NO_CHANGE`.

    > See ["Augmented null types."](#null-types)

## Expected annotations on record classes' `equals` methods

> Because of the special case [above](#augmented-type-of-usage) that makes
> parameters of record classes' `equals` methods always nullable, we include
> this rule so that tools can produce expected errors in some cases when the
> parameter is not annotated with `@Nullable`.

If a type usage is the parameter of `equals(Object)` in a subclass of
`java.lang.Record`, then:

-   It is not expected to be annotated with `@NonNull`.
-   If it appears in null-marked code, or if this rule is required to hold in
    [all worlds], then it is expected to be annotated with `@Nullable`.

[#49]: https://github.com/jspecify/jspecify/issues/49
[#65]: https://github.com/jspecify/jspecify/issues/65
[Java SE 23]: https://docs.oracle.com/javase/specs/jls/se23/html/index.html
[JLS 1.3]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-1.html#jls-1.3
[JLS 15.16]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-15.html#jls-15.16
[JLS 15.20.2]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-15.html#jls-15.20.2
[JLS 4.10.4]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-4.html#jls-4.10.4
[JLS 4.10]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-4.html#jls-4.10
[JLS 4.3.4]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-4.html#jls-4.3.4
[JLS 4.4]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-4.html#jls-4.4
[JLS 4.5.1]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-4.html#jls-4.5.1
[JLS 4.5.2]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-4.html#jls-4.5.2
[JLS 4.5]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-4.html#jls-4.5
[JLS 4.9]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-4.html#jls-4.9
[JLS 4]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-4.html
[JLS 5.1.10]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-5.html#jls-5.1.10
[JLS 8.4.1]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-8.html#jls-8.4.1
[JLS 8.4.8.1]: https://docs.oracle.com/javase/specs/jls/se23/html/jls-8.html#jls-8.4.8.1
[JVMS 5.4.5]: https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-5.html#jvms-5.4.5
[`FluentIterable<E>`]: https://guava.dev/releases/snapshot-jre/api/docs/com/google/common/collect/FluentIterable.html
[all worlds]: #multiple-worlds
[all-worlds]: #multiple-worlds
[applying operator]: #applying-operator
[augmented type]: #augmented-type
[augmented types]: #augmented-type
[base type]: #base-type
[base types]: #base-type
[capture conversion]: #capture-conversion
[comfortable]: #comfortable
[containment]: #containment
[in all worlds]: #multiple-worlds
[in some world]: #multiple-worlds
[intersection type]: #intersection-types
[intersection types]: #intersection-types
[multiple worlds]: #multiple-worlds
[null-exclusive under every parameterization]: #null-exclusive-under-every-parameterization
[null-inclusive under every parameterization]: #null-inclusive-under-every-parameterization
[null-marked scope]: #null-marked-scope
[nullness operator]: #nullness-operator
[nullness subtype]: #nullness-subtyping
[nullness subtyping]: #nullness-subtyping
[nullness-delegating subtyping rules for Java]: #nullness-delegating-subtyping
[nullness-delegating subtyping]: #nullness-delegating-subtyping
[nullness-subtype-establishing direct-supertype edges]: #nullness-subtype-establishing-direct-supertype-edges
[nullness-subtype-establishing path]: #nullness-subtype-establishing-path
[pattern]: https://docs.oracle.com/en/java/javase/23/language/pattern-matching.html
[repeatable]: https://docs.oracle.com/en/java/javase/23/docs/api/java.base/java/lang/annotation/Repeatable.html
[same type]: #same-type
[same-type]: #same-type
[semantics]: #semantics
[some world]: #multiple-worlds
[some-world]: #multiple-worlds
[substitution]: #substitution
[subtype]: #subtyping
[subtyping]: #subtyping
[type component]: #type-components
[type components]: #type-components
[world]: #multiple-worlds
[worlds]: #multiple-worlds
[worried]: #worried
