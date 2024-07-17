# Tool Conformance

This document is an attempt to clarify how to describe what the JSpecify
specification means for real Java code, so that we can talk about it in
documentation, specifications, issues, and discussions. JSpecify publishes
specific annotations whose presence in Java code has precise semantics, and this
semantics is relevant to tools that analyze Java code and report things to their
users, but *JSpecify does not require tools to emit any particular errors or
messages* to their users in any particular cases.

It's important not to talk about JSpecify in ways that imply that tools must
report specific errors, but we do have to talk about how JSpecify's rules affect
the meaning of Java code, including in ways that will lead *some* tools to
report errors in some cases.

More generally, JSpecify's specification adds information about Java code beyond
what the language specification does. We'd like a way to describe the
information that JSpecify adds to code without forcing us to make judgments
about whether that information represents problems or how that information
should be communicated to users.

JSpecify's [specification](spec) consists of an augmented type system including
subtyping rules, as well as a set of rules for determining the augmented type of
a type usage based on the annotations surrounding that type usage. The
specification uses terms like "UNION_NULL", "null-marked scope", "multiple
worlds", and "nullness-subtype-establishing paths"; it mostly doesn't talk
explicitly about what that means for examples of Java code. However, the point
of the specification is to imply things about real Java code.

Here is an example of the problem: Let's say that, according to JSpecify, the
method `a(...)`'s parameter has a non-nullable type and the method `b()` has a
nullable return type. A natural way to describe the situation is to say that
tools should report that there is a problem with the code `a(b())`. However,
there are many good reasons that tools might not report such a problem,
including by using non-JSpecify annotations or other information that indicates
that this particular call to `b()` does not return null.

> **NOTE:** Since nullness is the initial domain for JSpecify, all of the
> examples in this document are phrased in terms of that domain. However, this
> document is intended to clarify the ways in which we will describe JSpecify
> conformance for all domains.

The proposal is that we should talk about JSpecify's implications on examples of
Java code by creating an enumerated list of questions about actual Java code
that are answerable by consulting the specification. The set of questions will
grow as the specification grows.

The questions are as follows:

> **NOTE:** The examples for each question are not meant to be exhaustive.

1.  Is a given JSpecify annotation recognized or unrecognized
    ([type-use][spec-locations], [declaration][spec-locations]) in its context?

    *   Unrecognized: `@Nullable int`
    *   Unrecognized: `class Foo<@NonNull T extends Foo>`
    *   Recognized: `void foo(@Nullable Bar bar)`

1.  What is the [null-augmented type][spec-locations] of a given type usage,
    based only on JSpecify annotations on it or surrounding scopes?

    ```java
    @NullMarked
    interface A {
      Foo method1();

      @NullUnmarked
      void method2(Foo arg);
    }
    ```

    *   `method1` returns type `Foo!`
    *   `method2` accepts type `Foo*`

1.  What is the null-augmented type of an [expression] or [expression context],
    derived from the null-augmented types of the symbols it refers to, based
    only on JSpecify annotations on those symbols or surrounding scopes?

    *   `Optional.ofNullable(nullableString)` accepts `String?` and returns
        `Optional!<String!>`

1.  Does a given expression represent a dereference of a nullable expression in
    [some or all worlds](spec#multiple-worlds), as derived from the
    null-augmented type of its reference, based only on JSpecify annotations on
    those symbols or surrounding scopes?

    *   The call to `hashCode()` in `map.get(key).hashCode()` is a dereference
        of a nullable expression in all worlds (as `Map.get()` is
        [annotated to return `@Nullable`][Map.get])
    *   The call to `hashCode()` in `returnsUnspecified().hashCode()` is a
        dereference of a nullable expression in some world (if
        `returnsUnspecified()` returns `null`)

1.  Is a given expression or type argument not a
    [nullness subtype](spec#nullness-subtyping) of its context in some or all
    worlds, as derived from the null-augmented types of their referenced
    symbols, based only on JSpecify annotations on those symbols or surrounding
    scopes?

    *   `acceptsNonNull(returnsUnspecified())`: the argument
        `returnsUnspecified` is not a nullness subtype for `acceptsNonNull` in
        some world (if `returnsUnspecified()` returns `null`)
    *   `acceptsNonNull(returnsNullable())`: the argument `returnsNullable` is
        not a nullness subtype for `acceptsNonNull` in all worlds

1.  Is a given parameter's type a proper nullness subtype of the corresponding
    parameter of the overridden method (nullness-covariant) in some or all
    worlds?

    *   `foo(@NonNull String)` overrides `foo(@Nullable String)`: the parameter
        of `foo` in the overriding method is a nullness subtype in all worlds

1.  Is a given parameter's type a proper nullness supertype of the corresponding
    parameter of the overridden method (nullness-contravariant) in some or all
    worlds?

    *   `bar(@Nullable String)` overrides `bar(@NonNull String)`: the parameter
        of `bar` in the overriding method is a nullness supertype in all worlds

1.  Is a given method's return type a proper nullness subtype of the return type
    of the overridden method (nullness-covariant) in some or all worlds?

    *   `@NonNull String foo()` overrides `@Nullable String foo()`: the return
        type of `foo` in the overriding method is a nullness subtype in all
        worlds

1.  Is a given method's return type a proper nullness supertype of the return
    type of the overridden method (nullness-contravariant) in some or all
    worlds?

    *   `@Nullable String bar()` overrides `@NonNull String bar()`: the return
        type of `bar` in the overriding method is a nullness supertype in all
        worlds

1.  [More questions TBD]

Note that these questions are not phrased in a way that requires tools to report
errors.

## Conformance Tests

JSpecify publishes conformance tests that tools can use to measure their
consistency with these questions. The conformance tests consist of assertions
that represent answers to the questions above about specific examples of Java
code and symbols. Running the conformance tests for a tool consists of making
sure that the tool can answer these questions in the way the JSpecify
specification requires.

Note that a tool that passes these conformance tests can still behave however it
wants to for its users. For example, a tool that can answer correctly that a
parameter is a proper nullness supertype of its corresponding overridden
method's parameter may not ever report that fact to its users. For another, a
tool can use extra information to infer that a reference that is nullable
according to JSpecify is in fact non-null and so report its dereference as safe,
all without affecting its conformance to JSpecify.

[expression context]: https://docs.google.com/document/d/1nbTnJ0-HubLnQPKSjK5CDZyoe6Al64vdlkoJxaYX9XY/preview?resourcekey=0-ADjPZnp8LN3dRX_ptjlagw&tab=t.0#bookmark=kix.w6xfjhkftb9r
[expression]: https://docs.google.com/document/d/1nbTnJ0-HubLnQPKSjK5CDZyoe6Al64vdlkoJxaYX9XY/preview?resourcekey=0-ADjPZnp8LN3dRX_ptjlagw&tab=t.0#bookmark=kix.2r97mw74ac6r
[Map.get]: https://github.com/jspecify/jdk/blob/b7435cff373c527aad82a062c5605f6f9c1bb0de/src/java.base/share/classes/java/util/Map.java#L255
[spec-locations]: spec#recognized-locations-for-declaration-annotations
