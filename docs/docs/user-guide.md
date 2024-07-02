---
sidebar_position: 2
---

# Nullness User Guide (draft)

In Java code, whether an expression may evaluate to `null` is often documented
only in natural language, if at all. JSpecify's nullness annotations let
programmers express nullness of Java symbols in a consistent and well-defined
way.

JSpecify defines annotations that describe whether a Java type contains the
value `null`. Such annotations are useful to (for example):

*   programmers reading the code,
*   tools that help developers avoid `NullPointerException`s,
*   tools that perform run-time checking and test generation, and
*   documentation systems.

## Java variables are references

In Java, all non-primitive variables are either `null` or a reference to an
object. We often think of a declaration like `String x` as meaning that `x` is a
`String`, but it really means that `x` is *either* `null` *or* a reference to an
actual `String` object. JSpecify gives you a way to make it clear whether you
really mean that, or you really mean that `x` is definitely a reference to a
`String` object and not `null`.

## Types and nullness

JSpecify gives you rules that determine, for each type usage, which of four
kinds of nullness it has:

1.  It can include `null` (it is "nullable").
2.  It will not include `null` (it is "non-nullable").
3.  For type variables only: it includes `null` if the type argument that is
    substituted for it does (it has "parametric nullness").
4.  We don't know whether it can include `null` (it has "unspecified nullness").
    This is equivalent to the state of the world without JSpecify annotations.

For a given variable `x`, if `x` can be `null` then `x.getClass()` is unsafe
because it could produce a `NullPointerException`. If `x` can't be `null`,
`x.getClass()` can never produce a `NullPointerException`. If we don't know
whether `x` can be `null` or not, we don't know whether `x.getClass()` is safe
(at least as far as JSpecify is concerned).

The notion of "can't be `null`" should really be read with a footnote that says
"if none of the code in question involves unspecified nullness". For example, if
you have some code that passes types with unspecified nulless to a method that
accepts only `@NonNull` arguments, then tools might allow it to pass a
possibly-`null` value to a method that is expecting a "can't be `null`"
parameter.

There are four JSpecify annotations that are used together to indicate the
nullness of all symbols:

*   two type use annotations that indicate whether a specific type usage
    includes `null` or not: [`@Nullable` and `@NonNull`](#nullable-and-nonnull)
*   a scope annotation that lets you avoid typing `@NonNull` most of the time:
    [`@NullMarked`](#nullmarked)
*   another scope annotation that undoes the effects of `@NullMarked` so you can
    adopt annotations incrementally: [`@NullUnmarked`](#nullunmarked)

## `@Nullable` and `@NonNull`

When a type is annotated with [`@Nullable`], it means that a value of the type
can be `null`. `@Nullable String x` means that `x` might be `null`. Code that
uses those values must be able to deal with the `null` case, and it's okay to
assign `null` to such variables or pass `null` to those parameters.

When a type is annotated with [`@NonNull`], it means that no value of the type
should be `null`. `@NonNull String x` means
that `x` should never be `null`. Code that uses those values can assume they're
not `null`, and it's a bad idea to assign `null` to those values or pass `null`
to those parameters. (See [below](#nullmarked) for how to avoid having to spell
out `@NonNull` most of the time.)

```java
static @Nullable String emptyToNull(@NonNull String x) {
  return x.isEmpty() ? null : x;
}

static @NonNull String nullToEmpty(@Nullable String x) {
  return x == null ? "" : x;
}
```

In this example, the parameter to `emptyToNull` is annotated with `@NonNull`, so
it cannot be `null`; `emptyToNull(null)` is not a valid method call. The body of
the `emptyToNull` method relies on that assumption and immediately calls
`x.isEmpty()`, which would throw `NullPointerException` if `x` were actually
`null`. Conversely, `emptyToNull` may return `null`, so its return type is
annotated with `@Nullable`.

On the other hand, `nullToEmpty` promises to handle `null` arguments, so its
parameter is annotated with `@Nullable` to indicate that `nullToEmpty(null)` is
a valid method call. Its body considers the case where the argument is `null`
and won't throw `NullPointerException`. It cannot return `null`, so its return
type is annotated with `@NonNull`.

```java
void doSomething() {
  // OK: nullToEmpty accepts null but won't return it
  int length1 = nullToEmpty(null).length();

  // Not OK: emptyToNull doesn't accept null; also, it might return null!
  int length2 = emptyToNull(null).length();
}
```

Tools can use the `@Nullable` and `@NonNull` annotations to warn users about
calls that are unsafe.

As far as JSpecify is concerned, `@NonNull String` and `@Nullable String` are
*different types*. A variable of type `@NonNull String` can reference any
`String` object. A variable of type `@Nullable String` can too, but it can also
be `null`. This means that `@NonNull String` is a *subtype* of `@Nullable
String`, in the same way that `Integer` is a subtype of `Number`. One way to
look at this is that a subtype narrows the range of possible values. A `Number`
variable can be assigned from an `Integer` but it can also be assigned from a
`Long`. Meanwhile an `Integer` variable can't be assigned from a `Number` (since
that `Number` might be a `Long` or some other subtype). Likewise, a `@Nullable
String` can be assigned from a `@NonNull String` but a `@NonNull String` can't
be assigned from a `@Nullable String` (since that might be `null`).

```java
class Example {
  void useNullable(@Nullable String x) {...}
  void useNonNull(@NonNull String x) {...}

  void example(@Nullable String nullable, @NonNull String nonNull) {
    useNullable(nonNull); // JSpecify allows this
    useNonNull(nullable); // JSpecify doesn't allow this
  }
}
```

## What about unannotated types?

A type like `String` that isn't annotated with either `@Nullable` or `@NonNull`
means what it always used to mean: its values might be intended to include
`null` or might not, depending on whatever documentation you can find (but see
[below](#nullmarked) for help!). JSpecify calls this "unspecified nullness".

```java
class Unannotated {
  void whoKnows(String x) {...}

  void example(@Nullable String ) {
    whoKnows(nullable); // ¯\_(ツ)_/¯
  }
}
```

## `@NullMarked`

It would be annoying to have to annotate each and every type usage in your Java
code with either `@Nullable` or `@NonNull` to avoid unspecified nullness
(especially once you add [generics](#generics)!).

So JSpecify gives you the [`@NullMarked`] annotation. When you apply
`@NullMarked` to a module, package, class, or method, it means that unannotated
types in that scope are treated as if they were annotated with `@NonNull`.
(Below we will see that there are some exceptions to this for
[local variables](#local-variables) and [type variables](#declaring-generics).)
In code covered by `@NullMarked`, `String x` means the same as `@NonNull String
x`.

If applied to a module then its scope is all the code in the module. If applied
to a package then its scope is all the code in the package. (Note that packages
are *not* hierarchical; applying `@NullMarked` to package `com.foo` does not
make package `com.foo.bar` `@NullMarked`.) If applied to a class, interface, or
method, then its scope is all the code in that class, interface, or method.

```java
@NullMarked
class Strings {
  static @Nullable String emptyToNull(String x) {
    return x.isEmpty() ? null : x;
  }

  static String nullToEmpty(@Nullable String x) {
    return x == null ? "" : x;
  }
}
```

Here's the example from above, where the class containing the methods is
annotated with `@NullMarked`. The nullness of the types is the same as before:
`emptyToNull` does not accept `null` arguments, but it might return `null`;
`nullToEmpty` does accept `null` arguments, but it won't return `null`. But we
were able to do that with fewer annotations. In general, using `@NullMarked`
will give you correct nullness semantics with fewer annotations. In
`@NullMarked` code, you'll get used to thinking about plain, unannotated types
like `String` as meaning a real reference to a `String` object and never `null`.

As mentioned above, there are some exceptions to this interpretation for
[local variables](#local-variables) and [type variables](#declaring-generics).

### `@NullUnmarked`

If you're applying JSpecify annotations to your code, you might not be able to
annnotate it all at once. If you can apply `@NullMarked` to some of your code
now, and do the rest later, that's better than waiting until you have time to
annotate everything. But that means you may have to null-mark a module, package
or class *except for some classes or methods*. To do that, apply
[`@NullUnmarked`] to a package, class, or method that's already inside a
`@NullMarked` span. `@NullUnmarked` simply undoes the effects of the surrounding
`@NullMarked`, so that unannotated types have unspecified nullness unless they
are annotated with `@Nullable` or `@NonNull`, as if there were no enclosing
`@NullMarked` at all. A `@NullUnmarked` span may in turn contain nested
`@NullMarked` spans to reapply those rules.

## Local variables

`@Nullable` and `@NonNull` aren't applied to local variables—at least not their
root types. (They should be applied to type arguments and array components.) The
reason is that it is possible to *infer* whether a variable can be `null` based
on the values that are assigned to the variable. For example:

```java
@NullMarked
class MyClass {
  void myMethod(@Nullable String one, String two) {
    String anotherOne = one;
    String anotherTwo = two;
    String oneOrTwo = random() ? one : two;
    String twoOrNull = Strings.emptyToNull(two);
    ...
  }
}
```

Analysis can tell that all of these variables except `anotherTwo` can be `null`.
`anotherTwo` can't be `null` since `two` can't be `null`: it is not `@Nullable`
and it is inside the scope of `@NullMarked`. `anotherOne` can be `null` since it
is assigned from a `@Nullable` parameter. `oneOrTwo` can be `null` because it
may assigned from a `@Nullable` parameter. And `twoOrNull` can be `null` because
its value comes from a method that returns `@Nullable String`.

## Generics

When you are using a generic type, the rules about `@Nullable`, `@NonNull`, and
`@NullMarked` are as you would expect from what we have seen. For example,
within a `@NullMarked` context, `List<@Nullable String>` means a reference to a
`List` (not `null`) where each element is either a reference to a `String`
object or `null`; but `List<String>` means a list (not `null`) where each
element is a reference to a `String` object and *can't* be `null`.

### Declaring generic types {#declaring-generics}

Things are a bit more complicated when you are *declaring* a generic type.
Consider this:

```java
@NullMarked
public class NumberList<E extends Number> implements List<E> {...}
```

The `extends Number` defines a *bound* for the type variable `E`. It means that
you can write `NumberList<Integer>`, since `Integer` can be assigned to
`Number`, but you can't write `NumberList<String>`, since `String` can't be
assigned to `Number`. This is standard Java behavior.

But now let's think about that bound as it relates to `@NullMarked`. Can we
write `NumberList<@Nullable Integer>`?

Within `@NullMarked`, remember, unannotated types are the same as if they were
annotated with `@NonNull`. Since the bound of `E` is the same as `@NonNull
Number`, and not `@Nullable Number`, that means the type argument for `E` can't
be a type that includes `null`. `@Nullable Integer` can't be the type argument,
then, since that *can* include `null`. (In other words: `@Nullable Integer` *is
not a subtype* of `Number`.)

Inside `@NullMarked`, if you want to be able to substitute nullable type
arguments for a type parameter, you must explicitly provide a `@Nullable` bound
on your type variable:

```java
@NullMarked
public class NumberList<E extends @Nullable Number> implements List<E> {...}
```

Now it *is* legal to write `NumberList<@Nullable Integer`>, since `@Nullable
Integer` is assignable to the bound `@Nullable Number`. It's *also* legal to
write `NumberList<Integer>`, since plain `Integer` is assignable to `@Nullable
Number`. Inside `@NullMarked`, plain `Integer` means the same thing as `@NonNull
Integer`: a reference to an actual `Integer` value, never `null`. That just
means that the values represented by `E` can be `null` on some other
parameterization of `NumberList`, but not in an instance of
`NumberList<Integer>`.

Of course this assumes that `List` itself is written in a way that allows
nullable type arguments:

```java
@NullMarked
public interface List<E extends @Nullable Object> {...}
```

If it were `interface List<E>` rather than `interface List<E extends @Nullable
Object>` then `NumberList<E extends @Nullable Number> implements List<E>` would
not be legal. That's because `interface List<E>` is short for `interface List<E
extends Object>`. Inside `@NullMarked`, plain `Object` means "`Object` reference
that can't be `null`". The `<E extends @Nullable Number>` from `NumberList`
would not be compatible with `<E extends Object>`.

The implication of all this is that every time you define a type variable like
`E` you need to decide whether it can be substituted with a `@Nullable` type. If
it can, then it must have a `@Nullable` bound. Often this will be `<E extends
@Nullable Object>`. On the other hand, if it *can't* represent a `@Nullable`
type, that is expressed by not having `@Nullable` in its bound (including the
case of not having an explicit bound at all). Here's another example:

```java
@NullMarked
public class ImmutableList<E> implements List<E> {...}
```

Here, because it is `ImmutableList<E>` and not `ImmutableList<E extends
@Nullable Object>`, it is not legal to write `ImmutableList<@Nullable String>`.
You can only write `ImmutableList<String>`, which is a list of non-null `String`
references.

### Using type variables in generic types

Let's look at what the methods in the `List` interface might look like:

```java
@NullMarked
public interface List<E extends @Nullable Object> {
  boolean add(E element);
  E get(int index);
  @Nullable E getFirst();
  Optional<@NonNull E> maybeFirst();
  ...
}
```

The parameter type `E` of `add` means a reference that is compatible with the
actual type of the `List` elements. Just as you can't add an `Integer` to a
`List<String>`, you also can't add a `@Nullable String` to a `List<String>`, but
you *can* add it to a `List<@Nullable String>`.

Similarly, the return type `E` of `get` means that it returns a reference with
the actual type of the list elements. If the list is a `List<@Nullable String>`
then that reference is a `@Nullable String`. If the list is a `List<String>`
then the reference is a `String`.

On the other hand, the return type `@Nullable E` of the (fictitious) `getFirst`
method is always `@Nullable`. It will be `@Nullable String` whether called on a
`List<@Nullable String>` or a `List<String>`. The idea is that the method
returns the first element of the list, or `null` if the list is empty.
Similarly, the real methods `@Nullable V get(Object key)` in `Map` and
`@Nullable E peek()` in `Queue` can return `null` even when `V` and `E` can't be
`null`.

The distinction here is an important one that is worth repeating. A use of a
type variable like `E` should only be `@Nullable E` if it means a reference that
can be `null` *even if* `E` itself can't be `null`. Otherwise, plain `E` means a
reference that can only be `null` if `E` is a `@Nullable` type, like `@Nullable
String` in this example. (And, as we've seen, `E` can only be a `@Nullable` type
if the definition of `E` has a `@Nullable` bound like `<E extends @Nullable
Object>`.)

Similarly, you can use `@NonNull E` to indicate a type that is non-nullable
*even when `E` is nullable*. The fictitious `maybeFirst()` method returns a
non-nullable `Optional`. An `Optional` object can hold only non-null values, so
it's reasonable to define it as `class Optional<T>`; that is, its type argument
must not be nullable. So even for a `List<@Nullable String>`, `maybeFirst()` has
to return `Optional<@NonNull String>`. The way to declare that is to declare the
return type of `maybeFirst()` as `Optional<@NonNull E>`.

We saw earlier that `@NullMarked` usually means "references can't be `null`
unless they are marked `@Nullable`", and also that that doesn't apply to local
variables. Here we see that it doesn't apply to unannotated type variable uses
either, since an unannotated type variable usage whose bound is `@Nullable` may
be substituted with a `@Nullable` type argument.

### Using type variables in generic methods

Essentially the same considerations that we just saw with generic types apply to
generic methods too. Here's an example:

```java
@NullMarked
public class Methods {
  public static <T> @Nullable T
      firstOrNull(List<T> list) {
    return list.isEmpty() ? null : list.get(0);
  }

  public static <T> T
      firstOrNonNullDefault(List<T> list, T defaultValue) {
    return list.isEmpty() ? defaultValue : list.get(0);
  }

  public static <T extends @Nullable Object> T
      firstOrDefault(List<T> list, T defaultValue) {
    return list.isEmpty() ? defaultValue : list.get(0);
  }

  public static <T extends @Nullable Object> @Nullable T
      firstOrNullableDefault(List<T> list, @Nullable T defaultValue) {
    return list.isEmpty() ? defaultValue : list.get(0);
  }
}
```

The `firstOrNull` method will accept a `List<String>` but not a `List<@Nullable
String>`. When given an argument of type `List<String>`, `T` is `String`, so the
return type `@Nullable T` is `@Nullable String`. The input list can't contain
`null` elements, but the return value can be `null`.

The `firstOrNonNullDefault` method again does not allow `T` to be a `@Nullable`
type, so `List<@Nullable String>` is not allowed. Now the return value is not
`@Nullable` either which means it will never be `null`.

The `firstOrDefault` method will accept both `List<String>` and `List<@Nullable
String>`. In the first case, `T` is `String`, so the type of the `defaultValue`
parameter and of the return value is `String`, meaning neither can be `null`. In
the second case, `T` is `@Nullable String`, so the type of `defaultValue` and of
the return value is `@Nullable String`, meaning either can be `null`.

The `firstOrNullableDefault` method again accepts both `List<String>` and
`List<@Nullable String>`, but now the `defaultValue` parameter is marked
`@Nullable` so it can be `null` even in the `List<String>` case. Likewise the
return value is `@Nullable T` so it can be `null` even when `T` can't.

Here's another example:

```java
@NullMarked
public static <T> List<@Nullable T> nullOutMatches(List<T> list, T toRemove) {
  List<@Nullable T> copy = new ArrayList<>(list);
  for (int i = 0; i < copy.size(); i++) {
    if (copy.get(i).equals(toRemove)) {
      copy.set(i, null);
    }
  }
  return copy;
}
```

This takes a `List<T>`, which by definition does not contain `null` elements,
and produces a `List<@Nullable T>`, with `null` in place of every element that
matched `toRemove`. The output is a `List<@Nullable T>` because it *can* contain
`null` elements, even if `T` itself can't be `null`.

## Some subtler details

The previous sections cover 99% of everything you need to know to be able to use
JSpecify annotations effectively. Here we'll cover a few details you probably
won't need to know.

### Type-use annotation syntax

There are a couple of places where the syntax of type-use annotations like
`@Nullable` and `@NonNull` may be surprising.

1.  For a nested static type like `Map.Entry`, if you want to say that the value
    can be `null` then the syntax is `Map.@Nullable Entry`. You can often avoid
    dealing with this by importing the nested type directly, but in this case
    `import java.util.Map.Entry` might be undesirable because `Entry` is such a
    common type name.

1.  For an array type, if you want to say that the *elements* of the array can
    be `null` then the syntax is `@Nullable String[]`. If you want to say that
    the *array itself* can be `null` then the syntax is `String @Nullable []`.
    And if both the elements and the array itself can be `null`, the syntax is
    `@Nullable String @Nullable []`.

A good way to remember this is that it is the thing right after `@Nullable` that
can be `null`. It is the `Entry` that can be `null` in `Map.@Nullable Entry`,
not the `Map`. It is the `String` that can be `null` in `@Nullable String[]` and
it is the `[]`, meaning the array, that can be `null` in `String @Nullable []`.

### Wildcard bounds

Inside `@NullMarked`, wildcard bounds work almost exactly the same as type
variable bounds. We saw that `<E extends @Nullable Number>` means that E can be
a `@Nullable` type and `<E extends Number>` means it can't. Likewise, `List<?
extends @Nullable Number>` means a list where the elements can be `null`, and
`List<? extends Number>` means they can't.

However, there's a difference when there is no explicit bound. We saw that a
type variable definition like `<E>` means `<E extends Object>` and that means it
is not `@Nullable`. But `<?>` actually means `<? extends B>`, where `B` is the
bound of the corresponding type variable. So if we have

```java
interface List<E extends @Nullable Object> {...}
```

then `List<?>` means the same as `List<? extends @Nullable Object>`. If we have

```java
class ImmutableList<E> implements List<E> {...}
```

then we saw that that means the same as

```java
class ImmutableList<E extends Object> implements List<E>
```

so `ImmutableList<?>` means the same as `ImmutableList<? extends Object>`. And
here, `@NullMarked` means that `Object` excludes `null`. The `get(int)` method
of `List<?>` can return `null` but the same method of `ImmutableList<?>` can't.

[`@NonNull`]: https://jspecify.dev/docs/api/org/jspecify/annotations/NonNull.html
[`@Nullable`]: https://jspecify.dev/docs/api/org/jspecify/annotations/Nullable.html
[`@NullMarked`]: https://jspecify.dev/docs/api/org/jspecify/annotations/NullMarked.html
[`@NullUnmarked`]: https://jspecify.dev/docs/api/org/jspecify/annotations/NullUnmarked.html
