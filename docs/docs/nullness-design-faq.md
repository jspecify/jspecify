---
sidebar_position: 6
---

# JSpecify nullness design FAQ

## Can’t you see that nullness annotations are ugly, and we’d be better off with a `String?` language feature like Kotlin has? \{#language-feature}

Yep.

First, projects that can and want to use Kotlin… should! Or whatever other JVM
language. But those codebases still need to interact often with Java code, and
one of our goals is to improve that interoperation story. Also, Java code that
uses JSpecify nullness annotations is that much more ready to be *migrated* to a
null-aware language if you later desire.

If you dream of a null-aware language, adopting JSpecify now defrays the costs
you would otherwise incur later.

As for getting an elegant nullable-types feature in Java itself, we observe
that:

*   There is no assurance that will ever happen.
*   It will be a *very* long time before that feature lands.
*   It will be a long time after *that* when you and the libraries you use can
    all depend on it.
*   Quite a number of tools are here to help you avoid null pointer exceptions
    *now*.
*   Although our annotations *might* not be the ultimate answer, if you adopt
    them, then upgrading to the eventual language feature when the time comes
    will be primarily mechanical (certainly *easily* handled by an agent).

OpenJDK has been participating in JSpecify, and we are in regular communication
about the prospects for a longer-term language feature.

## I’ve heard that `null` is the billion-dollar mistake. Do we really need nullness annotations when really we should just stop using `null` so much? \{#billion-dollar-mistake}

`null` does get overused in many codebases. But it does have many uses and it is
**very good** for those purposes! Without it, you must always have an instance
of the expected type. `Optional` is a very invasive alternative that leaves you
in *approximately* the same situation as before. You can benefit from the
so-called "null object pattern" only rarely, when you can contrive an instance
of the type that is actually well-behaved. Most often, you would need to use
some type that blows up whenever someone tries to actually call a method on it
(*not* the "null object pattern"!). But code could freely pass such references
around without problem… which puts you right back in `NullPointerException`
territory again anyway.

`null` is a very useful "guard rail" in your code. When you see a
`NullPointerException`, you’ve hit that guard rail, and maybe scratched your
paint job, but what it prevented from happening could have been much worse. The
problem JSpecify is addressing is to let you hit that guard rail at compile-time
instead of runtime, as well as make APIs more clearly thought-out and
documented, so that you might not hit it at all.

`null` *itself* is not the billion-dollar mistake. The billion-dollar mistake is
when a language makes `null` automatically a member of every reference type and
there’s nothing you can do about it. That’s what we’re "correcting" (in a way)
for Java.

## Why does being null-marked require us to explicitly mark our *nullable* types, rather than marking the *non-null* ones? \{#why-mark-nullable}

In our experiences,

*   Non-null types are more common in APIs.
*   To use a nullable type should be an *intentional* decision, because it calls
    for a bit of thought on what null will *mean* in that context. Using a
    non-null type, of course, doesn’t, so it should be the first thing we reach
    for before thinking.
*   There are many kinds of *intrinsically non-null* type usages. We certainly
    don’t want to annotate these, but then in an "assume nullable" world they
    would look misleading. (In our "assume non-null" world, the things that will
    appear non-null but not necessarily be are mostly just local variable root
    types.)
*   The languages with null-aware types that we know of (Kotlin, Scala, C#,
    TypeScript, and Dart) **all** do it this way, as do the majority of existing
    analysis tools.

## In base Java, all reference types include `null`. So why don’t you call unannotated types "nullable"? Why have a third "unspecified nullness" category? \{#unspecified-nullness}

This is discussed at length in
[wiki/nullness-unspecified](https://github.com/jspecify/jspecify/wiki/nullness-unspecified).

## How are we supposed to eradicate `NullPointerException`s if even a method with all nullable parameters is still allowed to throw it? \{#runtime-guarantees}

Our [javadoc](https://jspecify.dev/docs/api/org/jspecify/nullness/Nullable.html)
states that adding `@Nullable` to a parameter type "does not guarantee that
passing `null` won’t produce an exception at runtime." Why?

JSpecify doesn’t try to eliminate those exceptions completely, just make them a
whole lot less common. A *verification* tool can use additional tool-specific
annotations to achieve the soundness guarantee you’re looking for. Most other
tools don’t pursue soundness, but rather an effective balance between false
positives and false negatives. JSpecify is a common layer that can be shared by
*all* kinds of tools.

Its way of doing that is to bring nullness into the type system: making "any
string reference" and "any string reference *or null*" into distinct static
types. This single shift delivers most of the value of annotating for nullness
(arguably, by far). So expecting this arrangement to make `NPE` impossible would
be akin to expecting `ClassCastException` to be impossible already. As we know,
`CCE` can still happen even when APIs use types appropriately.

If a parameter *sometimes* accepts null, its type should be nullable, much like
a method that accepts "any `int` value *or* `0.5`" has to use `double`. (though
you do have more wiggle room in our case). But that doesn’t mean the method
can’t react to your passing that `0.5` in whatever way it will.

## Why do you define a custom shorthand syntax? That’s more stuff we have to learn. \{#why-shorthand}

We like to refer to a type like "`Foo!<Bar?, Qux!>`" rather than "`@NullMarked
Foo<@Nullable Bar, Qux>`". We defined a
[shorthand](https://github.com/jspecify/jspecify/wiki/notation#shorthand)
notation so we can do that.

Brevity is one reason. It really helps in complex discussions involving such
types, especially since `@Nullable` and `@NonNull` look so similar visually.

Another important benefit is that it eliminates the _superficial_ difference
between our example type and `@NonNull Foo<@Nullable Bar, @NonNull Qux>`, which
also helps communication.

We also hope that tools will adopt it in their error messages and other
documentation (or at least will avoid defining similar but different schemes).
We would not be surprised if IDEs had a feature (maybe via a plugin) to actually
depict the types this way. Our test framework also uses it. It’s just a much
nicer appearance.

As for why it's `Foo!<Bar?>` and not `Foo<Bar?>!` … the question really boils
down to "which is more important to keep close to `Foo` physically, the nullness
or the type arguments?" And the simple answer is: "why not both?"

## If one is `@Nullable` why is the other one not `@NonNullable`? That’s not parallel naming. \{#non-null-naming}

The nature of "-able" is that it evokes *possibility*. A nullable expression
*might* evaluate to null. But a non-null expression *assuredly* does not. Also,
these names are widely expected/understood already from the dozens of existing
annotation libraries.

## Why is `@NullMarked` not called `@NonNullByDefault`? \{#null-marked-non-null-by-default}

The special cases listed in its
[javadoc](https://jspecify.dev/docs/api/org/jspecify/annotations/NullMarked.html)
can violate general expectations for simple "default behavior". To summarize:

*   It has no effect on the
    [root types](https://github.com/jspecify/jspecify/wiki/type-usages#root-type)
    of local variables (i.e., only on their type arguments, etc.)
*   It gives usages of type variables "parametric nullness"
*   Wildcards with no upper bound (`<?>` or `<? super Foo>`) won’t become
    non-null either

## Okay, but how does `@NullMarked` communicate all that, either? \{#null-marked-defense}

Well, this is certainly a case where no name could possibly say it all. It’s at
least not actively misleading. We do have a long list of other names we
considered and why we ruled them out.

## Why not provide more configurable defaults, or at least `@DefaultNullable`? \{#configurable-defaults}

We feel it is very important that you can read and understand Java code without
needing to hold a lot of exogenous information in your head – knowledge that
comes from somewhere outside the code itself. The code should mean exactly what
it appears to mean, with as little outside context needed as humanly possible.

We’ve kept this down to just *one single bit* of such information – the code is
either null-marked or it isn’t. Even better: since that bit represents "has this
code migrated yet?", once developers are accustomed to working in a
fully-migrated codebase they can return to needing *no* exogenous knowledge
again.

For a while we had tried to drop even that! You would have to write
`@NullMarked` on every single top-level class you write. That clearly wouldn’t
have gone over well; plus, the ability to make new classes in a package/module
null-marked *by default* is extremely valuable.

## Why do I have to repeat `@Nullable` on all my `equals(Object)` parameters? The supertype parameter is `@Nullable` so I have no choice; you could just inherit it. \{#nullable-equals}

This can be inconvenient, but we have a few good reasons.

As the previous item discusses, we don’t want you to have to rely on exogenous
information to understand code. There’s one bit of such information we can’t
avoid (null-markedness). That is already one *kind* of "inheritance" (inheriting
through the enclosure hierarchy), so mixing it with inheritance along a
different axis (through the class hierarchy) would create a lot of confusion.

We are hoping that the more time users spend in "modern", null-marked code, the
more you’ll internalize the idea that "object means a real object, not null". If
we carved out special exceptions like this, we would be working directly against
that goal.

## Why are you making `List<?>` and `List<? extends Object>` mean different things? That is surprising. \{#wildcards-and-bounds}

Assuming a `List` class is defined to allow nullable type parameters, then
JSpecify says that a `List<?>` may contain null elements but a `List<? extends
Object>` may not. Why the difference?

First, it seems clear that the second type should have non-null elements: A bare
type usage (like `Object` here) is usually non-null, so it would be surprising
for this usage to be treated differently just because it appears in a wildcard
upper bound.

There are a few reasons we might want the first type to do so as well. For one,
there's the basic argument that, since `List<?>` and `List<? extends Object>`
have always meant the same thing thus far, they should continue to.

Another is the idea that we should have the "peace of mind" of knowing that
nulls aren’t creeping in or out of our code unless we see the word `@Nullable`,
broadcasting that risk in black and white. It’s fair to expect the process of
null-marking code to require marking *all* the nullable things, right? As the
JSpecify design stands, it feels like we *so nearly* have that, but then
`List<?>` is a hole where nulls can flow with no visible hint.

These are fair criticisms, but:

*   Note that `List<? super Foo>` is a "hole" like that too. It can hold a list
    with nullable elements, and for good reason.[^1] So we aren’t creating a hole
    where *none* would otherwise exist. And it would be strange if you couldn’t
    assign a `List<? super Foo>` to `List<?>`.

*   When we say "unbounded wildcard" we might mean either (a) there is no bound
    provided, or (b) no (possibly-implicit) bound actually *exists* in effect.
    Our design choice makes both of these correct. But if `?` were considered
    non-null bounded, the only way to get a *logically* unbounded wildcard is to
    physically *add a bound!* It makes these two senses of "unbounded" conflict
    with each other.

*   Within an `if (object instanceof List) {}` block, what type can we safely
    cast `object` to (i.e., with no warning or runtime failure)? Users should go
    right on assuming that is `List<?>`; that’s supposed to be the very most
    general kind of list.

*   Because we want `Object` to mean *non-null* `Object`, it means that `Object`
    is no longer the "top type" of the reference type hierarchy; `Object?` is.
    We think this is a basic fact that users will need to internalize sooner or
    later. And once they do, it should actually seem *wrong* if `List<?>` and
    `List<? extends Object>` were equivalent!

We found the sum of these arguments to be very strongly compelling.

As for that argument that we ought to see a literal `@Nullable` wherever a null
can flow… arguably, it seems a bit stuck in the "old world". It seems to take
the idea for granted that we developers bear the mental burden of keeping track
of where nulls can go. But the point of our project is to free you from that,
and let the tools take care of everything for you! If there is a (probable)
*real problem*, you’ll find out.

## So `List<?>` might admit nullable elements, but a declaration like `class List<T>` doesn’t? It’s scandalous that these would be different. \{#type-parameters-and-bounds}

Yep. Plus, writing out `class List<T extends @Nullable Object>` is horrendously
verbose, and imagine when there are two *or more* type parameters!

We expect this inconsistency to be surprising to most people when they first
come across it.

First, the arguments about `List<?>` just *above* are strong enough that we feel
that `List<?>` has to admit nullable elements, even if we make a shockingly
different choice about `class List<T>`. So, I’ll treat that decision as given,
and take this as a question about `class List<T>` only. Why in the world is that
non-null by default!?

Here’s the problem. We are helpless to make `class List<T>` mean anything
different from `class List<T extends Object>`. If the former allowed nullable
type arguments, the latter would have to as well. Why? When analyzing code that
*uses* that `List` class, we often have only the compiled class file for `List`
available, not the source code. And, unlike its behavior for wildcards, `javac`
outputs identical code for both of these signatures! Game over.[^2]

Now we are left with only bad options:

*   Type parameter bounds are the only type usages that are nullable by default.
*   Only if the type parameter bound is `Object`, then it’s nullable by default.
*   The bad option we’ve gone with.

The first two feel much more "special-casey" than even the third does. As a
minor point as well, either of them would mean we sometimes need to write `class
List<T extends @NonNull Object>`, and we’d like to keep `@NonNull` as something
very very rarely used in null-marked code.

A reasonable interpretation (that will help you remember how this works) is that
type parameters are always bounded (sometimes *implicitly* bounded by `Object`),
but wildcards are different: they can be truly unbounded. The visual similarity
between the declaration `class List<T>` and the usage `List<?>` doesn’t hint at
this difference, yet it’s there.[^3]

## Then why can’t we at least write `class MyList<@Nullable T>`? Your way is intolerably verbose. \{#nullable-type-parameter}

Having to write (and read) `class MyList<T extends @Nullable Object>` isn’t fun.

At least it is accurate and arguably illuminating: there is nothing special
about the type parameter itself; it is simply bounded by the type we’re telling
it. But, it’s long and non-pretty.

We might still add a
[shorter way](https://github.com/jspecify/jspecify/issues/289) in the future.
That issue discusses some reasons why we haven’t yet; it could still happen.

Similarly for supporting annotated wildcards, like in `List<@NonNull ?>`.

## You have three flavors of nullness, yet only two type-use annotations. Why can’t I, within null-marked code, make just a single type usage `@NullnessUnspecified`? \{#type-use-unspecified-nullness}

If you really want to do this, you usually can: Annotate the surrounding context
`@NullUnmarked`, and annotate every *other* type usage with `@Nullable` or
`@NonNull`. (You might prefer not to do this for type variables, letting them
continue to have "parametric nullness", but unfortunately this change will force
them to have unspecified nullness.) Not fun, but the specification you want is
usually *possible*, so the question here is mostly about how easy/pretty to make
it.

So why have we been holding out?

We’re concerned that the most popular use of this feature (probably by far)
would be as an "escape valve" for return types that *are clearly absolutely
nullable*, but whose callers get irritated by too many warnings. The usual
example is the return type of a call to `Map.get`, when the user *knows* the key
is present. Slapping on `@NullUnspec` would be an easy way to make those
warnings go away.

The general user complaint goes something like "in my particular case, I know
this returned value can’t be null, and I expected my tools to realize that too."
(When users see their case as something hard for tools to recognize, they are
much more likely to accept inserting `requireNonNull` or suppressing a warning.)

Obviously the *best* thing that could result from that is that someone writes
the code to teach the analyzer what to do – to equip it with one more "candidate
non-nullness proof" it can walk through looking for a hit. Notably, in that
event, it remains 100% correct for the return type to be declaratively
`@Nullable`. Analyzers are always free to "sharpen" types for any reason they
see fit.

Another proper remedy would be if the API gets redesigned or replaced. Some APIs
would not have been designed as they were if declarative nullable types had been
a thing. Frankly, `Map.get` is one of them! The prevalence of callers who "know
the key is in the map" shows that there should have been a version of `get` that
throws an unchecked exception for a missing key. Obviously an API change will
not always happen (and probably won’t for `Map`!), but the fact that something
is *motivating* it to happen is not always a bad thing.

But what if that return type had been annotated `@NullnessUnspecified` instead?
It’s a nice instant reprieve, but it gets a lot less likely that anyone will
ever bother with any of those remedies above. Instead, some bugs will just slip
by unnoticed.

In general, we want most code to be able to trend toward becoming null-marked,
and whenever you’re using null-marked code that calls only null-marked code, we
want you to not have to think about unspecified nullness at all. We want
unspecified nullness to be like raw types, and eventually become a relic of the
past. Allowing it to be sprinkled in easily at any individual usage would seem
to undermine that.

But note that we could still decide to support this use case we’ve been
discussing, directly (imagine a `@Nullable(enforce = false)`, not that it would
necessarily look like that). This would still capture *correct* information
about the nullness of that return type.

## Within null-marked code, you ask that we train our eyes to see unannotated types as non-null… but then that falls apart for local variables? What gives? \{#local-variables}

This inconsistency is a bit unfortunate, but here are some reasons for it.

While a local variable type may contain nullness annotations on its component
types (e.g. `List<@Nullable String> list`), JSpecify says that its entire type
or "root type", the type of the variable *itself*, is not to be annotated (e.g.,
no `@Nullable String str`).

Overall, the need to annotate types is a burden. That burden is well-justified
for the types that appear in your APIs, because the information can’t reliably
be inferred by analysis alone. And a single annotation will benefit many callers
or implementors. It’s a necessary evil.

But if we were to handle local variables the same way, you would end up having
to declare many of them as `@Nullable`, when that’s information that could
easily have been inferred. This is hard to justify.

The key is that analysis tools are already good at local analysis. They can
already do a fine job figuring out which of your variables might be null. And
where you need to give them hints, just annotating the variable wouldn’t be the
best way to do that anyway.

We hope it will be a relief to not have to annotate your local variable root
types at all!

It *might* be okay to just let this be tool-specific behavior. We’ll see what
the owners of those tools tell us. While it is crucial for tools to agree on API
annotations, it’s not nearly *as* important for implementation code, because at
least a single project in isolation *could* pick a single tool to adopt.

## Why can’t I cast an expression to `(@NonNull Foo)`? \{#casts}

The reason to cast would be to avoid some warning… yet the cast would then
deserve a warning of its own anyway. Casts are expected to be checked at
runtime! When it can’t be (such as a cast to `(T)`, or in our case), we get an
unchecked warning from `javac`.

Given that you’re just trading one warning for another, you might as well just
suppress the original one. But a better approach is to pass the expression
through a call to a method like `Objects.requireNonNull()`, so it *will* be
checked at runtime.

## Why can’t I write `@NonNull class Foo {}`? \{#non-null-class}

The usual desire here is to mark a class so that any nullable usage of that type
will always give a warning. (We all hate nullable `Optional`s, don’t we?)

While we currently prohibit this usage, we could still compatibly change to
allow it in the future. However, it would seem clearer to support that use case
via a different annotation like `@NoNullableUsages`, anyway.

## What about more complex kinds of nullness than the simple kinds you are thinking of? \{#complex}

Everything can be sorted into nullable or non-null, though there are
[borderline cases](https://github.com/jspecify/jspecify/wiki/borderline-cases).
It’s just that sometimes there is more fine-grained information you’d like to
capture as well (like "polynull", next section). TODO

## Why don’t you have `@PolyNull`? \{#polynull}

`@PolyNull` can certainly be helpful in some cases, and it might appear in a
future JSpecify release. Right now it would distract us from finalizing the more
critically needed features. Your checker might still support it, though. See
[jspecify/wiki/polynull](https://github.com/jspecify/jspecify/wiki/polynull) for
a fuller explanation.

[^1]: The use for this type is to be "a list *into which I can store* non-null
    `Foo` references"; since I won’t be able to store `null`, I can hardly
    care whether or not the list *would accept* `null`. Who cares?
[^2]: Then how do some tools like Checker Framework do just that? By modifying
    the output written by `javac`. It’s important that JSpecify require only a
    vanilla `javac`.
[^3]: Why does the difference arise? Every type variable must have some upper
    bound – the type variable’s erasure is that upper bound’s erasure. That
    implies that every type parameter must have some upper bound, since type
    variables often come directly from those (i.e., those that aren’t captured
    wildcards). But it doesn’t imply that a wildcard must have an upper bound,
    because even without one, the anonymous type variable that arises from
    capturing the wildcard still has the corresponding type parameter’s upper
    bound to lean on. The captured wildcard just wants to intersect the
    wildcard’s upper bound with that type if it has one, but it has no real
    need to. Also, viewing a wildcard as not requiring an upper bound is
    simpler: we can view every wildcard as having zero or one bounds.
