---
sidebar_position: 3
---

# Should you use JSpecify annotations in your Java code?

With the release of JSpecify 1.0.0, we guarantee backwards compatibility: we
will not rename the annotations or move them or make other changes that would
cause your compilation to fail when you update.

However, there are other things to think about when deciding whether to start
using JSpecify annotations in your code, including at least:

*   If your Java code doesn’t use nullness annotations already, it should!
*   How well does the current version of your nullness checker support JSpecify
    annotations?
*   Do you have Kotlin users? Which compiler version do they use?
*   Do you use whole-program annotation processors, such as [Dagger]?

## If your Java code doesn’t use nullness annotations yet

If your Java code doesn’t already use nullness annotations, we recommend that
you [start using JSpecify annotations](/docs/using).

Even if you are not currently using a nullness analyzer, applying JSpecify
annotations can still provide benefits.

*   The annotations are useful documentation to communicate your intent to users
    and future maintainers of your code.
*   Additionally, projects that directly depend on your library will be able to
    run *their* nullness analyzers on it.
*   Finally, annotating your code will make the eventual process of applying
    nullness analysis to your codebase easier.

In particular, if your project includes Kotlin code, or Kotlin users depend on
your code, JSpecify annotations will improve the null-safety of Kotlin code
using your Java library. New versions of the Kotlin compiler
[recognize and correctly interpret](#kotlin) the annotations, so JSpecify
annotated Java code will have correct nullness typing in Kotlin.

## Nullness checker support for JSpecify

Several major Java nullness analyzers already support JSpecify annotations to
some degree. Consult the official documentation for your checker to see what
they claim about their current and planned JSpecify support.

*   The [EISOP Framework](https://eisop.github.io/) has good conformance except
    for its interpretation of unspecified-nullness code (unannotated code
    outside of `@NullMarked` scope).

*   [NullAway](https://github.com/uber/NullAway) supports JSpecify annotations
    but does not yet analyze generics.

*   [IntelliJ IDEA](https://www.jetbrains.com/idea/) supports JSpecify
    annotations but has incomplete support for generics
    ([example](https://youtrack.jetbrains.com/issue/IDEA-356144)).

*   The [Checker Framework](https://checkerframework.org/) understands
    `@Nullable` and `@NonNull`, but not `@NullMarked` or `@NullUnmarked`.

*   JSpecify’s
    [reference checker](https://github.com/jspecify/jspecify-reference-checker)
    is mostly correct except for a few edge cases. While you can try it out, it
    is not intended for production use and is not intended to be user-friendly
    or performant.

We are working on a
[conformance test suite](https://github.com/jspecify/jspecify/tree/main/conformance-tests)
that will allow any nullness analyzer to measure and publish how well they
conform to the JSpecify specification. At this time, only JSpecify’s reference
checker and the EISOP Framework have integrated into our prerelease test suite.

## Kotlin

Kotlin has a null-safe type system that’s similar to JSpecify’s model. But the
Kotlin compiler interprets unannotated Java dependencies as having
[platform types](https://kotlinlang.org/docs/java-interop.html#null-safety-and-platform-types),
relaxing some of the nullness checks Kotlin otherwise performs. However, the
compiler understands JSpecify annotations and is able to use them to make Kotlin
code see null-safe types when calling into Java code. The Kotlin compiler
correctly interprets `@Nullable` and `@NullMarked` starting at version 1.8.20,
`@NonNull` starting at 2.0.0, and `@NullUnmarked` coming in 2.0.20.

Note that the Kotlin compiler currently emits only warnings by default for
problems found using JSpecify nullness. To change those to errors, pass the
[`-Xjspecify-annotations=strict` and
`-Xtype-enhancement-improvements-strict-mode`
flags](https://kotlinlang.org/docs/whatsnew1520.html#support-for-jspecify-nullness-annotations).
The Kotlin team plans to emit errors by default in Kotlin 2.1.0.

## Annotation processors

If your project relies on annotation processors, like [Dagger], that interpret
nullness annotations on symbols in the classpath, then you may need to wait to
adopt JSpecify annotations until you can
[build with JDK 22](https://github.com/jspecify/jspecify/issues/537). There was
a bug in `javac` versions before JDK 22
([JDK-8225377](https://bugs.openjdk.org/browse/JDK-8225377)) where
[type-use](https://www.oracle.com/technical-resources/articles/java/ma14-architect-annotations.html)
annotations (including JSpecify’s `@Nullable` and `@NonNull`) were not properly
read from class files. In contrast, JSR-305 and AndroidX nullness annotations
are "declaration" annotations and hence are not impacted by this issue; however,
because of that, they can’t be applied to type arguments or components of
generic or array types the way JSpecify’s can.

The issue is fixed starting in JDK 22, but the fix has not yet been backported
to older versions of `javac`. If you cannot build with JDK 22+ `javac` and you
rely on Dagger or other similar annotation processors, you may run into issues
switching to JSpecify’s annotations at the moment. Please check out
[JSpecify issue 365](https://github.com/jspecify/jspecify/issues/365) for
further discussion, and you can track the effort to backport this fix to older
versions of `javac` in
[JDK-8323093](https://bugs.openjdk.org/browse/JDK-8323093).

[Dagger]: http://dagger.dev
