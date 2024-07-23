---
sidebar_position: 5
---

# Applying JSpecify Nullness Annotations

If your code has no nullness annotations, we recommend starting with one class
or package at a time, ideally one that depends on as few others as possible.
Then move outward from called code to its callers.

In the future, we'll have suggestions for
[tools that can automate some of this process](https://github.com/jspecify/jspecify/issues/553).

## 1. Look for nullable type usages

Look for fields and method return types and parameter types where some part of
the type component might reasonably be `null`, and add `@Nullable` there.

Read the documentation of the method or field to see if it documents that `null`
is valid.

Look for clues in the implementation. (Note that these are *clues* and not a
guaranteed recipe for knowing exactly what annotations to put where. There are
lots of borderline cases and places where static analysis is necessarily
incomplete.)

*   A `return null;` statement in a method is a strong signal that the method's
    return type is nullable!
*   A test like `if (parameter == null)` in a method is a strong signal that the
    parameter is nullable.
*   If a field is ever assigned `null` directly, or assigned a value that might
    be `null`, that's a signal that the field is nullable.
*   If your code passes `null` or nullable values to its own methods, then those
    parameters are likely nullable too.

### Generics

Generic classes with type parameters are a little more complex, because you have
to consider whether users should be allowed to apply a nullable type argument or
not, and then whether individual occurrences of that type variable inside the
generic class should have different nullness from the type argument.

For example, if you have `class Foo<T>` with a method `Optional<T> bar(T t)`,
you have to think about whether it makes sense for your users to have a
`Foo<@Nullable String>`. If that's reasonable, then annotate that type
parameter's bound with `@Nullable`. If the type parameter has no apparent bound
(as in `class Foo<T>`), then remember that `<T>` really means `<T extends
Object>`, and change it to `class Foo<T extends @Nullable Object>`. If not,
leave the bound non-nullable, and then users won't be able to declare
`Foo<@Nullable String>` without their nullness checker reporting it to them.

Then look for individual usages of each type variable that must be nullable even
when the type argument isn't. Maybe `bar` should accept `null` even for
`Foo<@NonNull String>`. If so, add `@Nullable` to that type variable usage:
`bar(@Nullable T t)`.

If the type parameter's bound is nullable, but you find a specific usage of that
type variable should *not* be nullable, then add `@NonNull` to the type variable
usage itself. For example, you know that `Optional`'s type parameter can never
be nullable, so you would declare `bar` to return `Optional<@NonNull T>` to
indicate that it returns `Optional<String>` even for `Foo<@Nullable String>`.

(There are other edge cases, such as wildcards. Consult the
[User Guide](user-guide) for more information.)

## 2. Add `@NullMarked`

Add `@NullMarked` to the class or package you're annotating to indicate that the
remaining unannotated type usages are not nullable.

If there are parts of the class or package that you're not ready to annotate
yet, you can use `@NullUnmarked` to leave their unannotated type usages
unspecified.

## 3. Run nullness analysis on the annotated code

Then run a JSpecify-aware nullness analyzer on the code you just annotated. If
it finds any nullness problems, resolve them and rerun:

*   Maybe you missed an annotation: something should be `@Nullable` or
    `@NonNull` that isn't?
*   Maybe your implementation code needs some annotations (type arguments in
    local variable declarations or cast expressions)?
*   Maybe you have a case where static analysis won't be able to tell that
    something is null-safe, but you know it is. For example, if you call a
    method that you know won't return `null` *for these arguments*, then you
    might just dereference the returned value—but your analyzer might still
    complain.
    *   In this case, it makes sense to suppress the nullness error from your
        analyzer with a `@SuppressWarnings` annotation and document why your
        logic is safe anyway.
*   Maybe your code has an actual nullness bug? If so, annotating your code
    helped you find it!
    *   If you can't fix the bug right now, your nullness analysis tool should
        allow you to suppress the error with a `@SuppressWarnings` annotation,
        or some other mechanism that lets you keep track of these as TODOs.
*   Maybe your nullness analyzer has a bug? (This will eventually be unlikely,
    but it's possible, especially before we've completed our conformance test
    suite.)
    *   You should be able to suppress the error with a `@SuppressWarnings`
        annotation. Then file a bug with your nullness analyzer so they know
        about it.

If possible, turn on nullness analysis on this class or package as part of your
regular build or CI setup in order to prevent regressions.

## 4. Run nullness analysis on calling code

Now that you've got the class or package you're looking at consistently
annotated and checked, you have to make sure that those new annotations haven't
uncovered nullness problems in the code that *uses* that code. That means Java
code that directly depends on the classes that you've newly annotated and is
already covered by your nullness analysis, and Kotlin code that directly depends
on the newly annotated classes (if you've turned on Kotlin's JSpecify support).
Run your nullness analysis on that code again and fix any errors you see
reported there. You might be able to make changes only in the calling code, or
you might find you have to change an annotation in the newly annotated code.

## 5. Repeat for new code

Find the next class or package you want to annotate, and repeat the process,
until your entire codebase is analyzed and covered by `@NullMarked`.
