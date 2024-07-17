---
sidebar_position: 4
---

# Using JSpecify Annotations

## Depending on the annotations

The annotations are available from Maven Central as
[`org.jspecify:jspecify:1.0.0`](https://repo1.maven.org/maven2/org/jspecify/jspecify/1.0.0/).
The annotations themselves are in the `org.jspecify.annotations` package.

Below are snippets you can use to add a dependency in Maven, Gradle, or Bazel.

Regardless of the build system you use, avoid configuring your build to hide
JSpecify annotations from your users. We also recommend including the
annotations at runtime, and we have kept the JSpecify jar small to reduce the
cost of doing so. Each build tool supports different mechanisms that will hide
the annotation declarations, and we recommend against them in the tool-specific
guidance below.

#### Maven

```xml
<dependency>
  <groupId>org.jspecify</groupId>
  <artifactId>jspecify</artifactId>
  <version>1.0.0</version>
</dependency>
```

Avoid using [`provided`] or [`optional`] scope.

#### Gradle

For projects that use the `java-library` or
[`com.android.library`](https://developer.android.com/studio/projects/android-library)
plugins, avoid using [`implementation`] or [`compileOnlyApi`] configurations:

```groovy
dependencies {
  api("org.jspecify:jspecify:1.0.0")
}
```

Or, if you're using a plugin that doesn't support the `api` configuration, such
 as the `java` plugin:

```groovy
dependencies {
  implementation("org.jspecify:jspecify:1.0.0")
}
```

#### Bazel

```python
maven_jar(
    name = "jspecify",
    artifact = "org.jspecify:jspecify:1.0.0",
    sha256 = "1fad6e6be7557781e4d33729d49ae1cdc8fdda6fe477bb0cc68ce351eafdfbab",
)
```

## Switching to JSpecify nullness annotations

### If your code has no nullness annotations

If your code doesn't use nullness annotations yet, there's no time like the
present! We have a high-level [strategy](applying) we recommend.

### If your code already uses JSR-305 annotations

Before migrating from the JSR-305 annotations to JSpecify annotations, see if
the caveats about [Kotlin][Kotlin-caveats] or
[annotation processors](whether#annotation-processors) apply to your situation.

Migrating from JSR-305 annotations primarily entails changing imports, updating
annotation names and locations, and addressing build errors. JSpecify's
annotations are type-use annotations, which impose additional restrictions on
where they are placed. In some cases, these restrictions can make their
placement incompatible with the placement of existing JSR-305 annotations. We
recommend taking the following migration steps:

1.  Update imports to use JSpecify annotations.

1.  Check *all* annotations on array types. Any code that currently has a type
    like `@Nullable Object[]` must change to `Object @Nullable []`. This change
    is required by the syntax of [type-use annotations]. If you do *not* make
    this change, your code will change from meaning "a nullable array of
    objects" to "an array of nullable objects."

1.  Rebuild your project, making changes as needed to correct build errors.
    Build errors can arise from restrictions on placing type-use annotations. If
    you see an error about an annotation on a "scoping construct," you must move
    the annotation to be immediately before the simple type name. For example,
    change code like `@Nullable Map.Entry<K, V>` to `Map.@Nullable Entry<K, V>`.

1.  Optionally,
    [move other annotations to comply with style guidelines](https://google.github.io/styleguide/javaguide.html#s4.8.5-annotations),
    but this is not required by the language.

#### Defaulting annotations

In addition to adopting JSpecify's `@Nullable` and `@NonNull` annotations, you
may also wish to adopt `@NullMarked`. If your code already annotates all its
`@Nullable` types, then you can (with rare
[exceptions](https://jspecify.dev/docs/api/org/jspecify/annotations/NonNull.html#projection))
remove any `@NonNull` annotations in favor of putting a `@NullMarked` annotation
on the whole class, package, or module.

`@NullMarked` is similar to JSR-305's [`@ParametersAreNonnullByDefault`] and
custom [`@TypeQualifierDefault`] annotations. Still, `@NullMarked` differs from
those, including in [its effect on generics](user-guide#generics), so you may
need to take advantage of your new ability to annotate locations like type
arguments (as in `Future<@Nullable Credentials>`). `@NullMarked` is likely to
have an effect closer to what you want, but it may require additional work on
your part.

### If your code already uses Checker Framework annotations

Before migrating from the Checker Framework annotations to JSpecify annotations,
see if the above caveats about [Kotlin][Kotlin-caveats] apply to your situation.

Migrating from the Checker Framework `@Nullable` and `@NonNull` to the JSpecify
equivalents is as simple as switching imports.

Note, however, that both JSpecify and the Checker Framework offer annotations
that the other does not. If you are using the Checker Framework Nullness Checker
on your project, then you may end up using a mix of annotations:

*   The JSpecify annotations are designed to cover features that are commonly
    needed in public APIs and widely supported by tools. In particular, you can
    use the `@NullMarked` annotation to indicate to tools like Kotlin that
    [most of your APIs' types are non-nullable](https://jspecify.dev/docs/api/org/jspecify/annotations/NullMarked.html#effects),
    with full support for generics and without needing to run the Checker
    Framework's bytecode rewriting during your build.

    *   If your code already passes the Checker Framework's checking, then you
        can normally annotate it as `@NullMarked` with only one further change:
        If you declare any type parameters without declaring a bound (as in
        `class Foo<T>`), you must change them to declare a nullable bound
        (`class Foo<T extends @Nullable Object>`) if you want to continue to be
        able to use them with nullable type arguments.

*   The Checker Framework provides additional annotations that have mostly not
    been recognized by other tools. A project can benefit from these annotations
    if that project is checked by the Checker Framework or if it has users that
    are checked by the Checker Framework. In those cases, the annotations can
    allow you to express more complex contracts, such as which methods are safe
    to call during object initialization.

In short: If all you need is `@Nullable`, `@NonNull`, and `@NullMarked`, then
you might prefer to switch entirely to JSpecify. Otherwise, you might choose to
use both the Checker Framework annotations (for annotations like
`@MonotonicNonNull`) and JSpecify (for annotations like `@NullMarked`).

[`@ParametersAreNonnullByDefault`]: https://www.javadoc.io/doc/com.google.code.findbugs/jsr305/3.0.1/javax/annotation/ParametersAreNonnullByDefault.html
[`@TypeQualifierDefault`]: https://github.com/Kotlin/KEEP/blob/master/proposals/jsr-305-custom-nullability-qualifiers.md#type-qualifier-default
[`compileOnlyApi`]: https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_configurations_graph
[`implementation`]: https://docs.gradle.org/current/userguide/java_library_plugin.html#sec:java_library_configurations_graph
[`optional`]: https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html
[`provided`]: https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#dependency-scope
[Kotlin-caveats]: whether#kotlin
[type-use annotations]: https://www.oracle.com/technical-resources/articles/java/ma14-architect-annotations.html#:~:text=Applying%20Type%20Annotations
