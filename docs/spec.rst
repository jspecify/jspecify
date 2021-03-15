JSpecify spec outline
=====================

About this document
-------------------

This document summarizes our current thinking for proposing a set of
nullness annotations. **Nothing here is “set in stone” yet.**

**As of March 2021, this doc is currently two docs concatenated
together. They conflict with each other in places. We will iterate to
address this.**

It will be very helpful to read or refer to the `core
concepts/glossary <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit>`__
document in order to understand this material correctly. There are
numerous other documents in our `shared
folder <https://drive.google.com/drive/folders/1vZl1odNCBncVaN7EwlwfqI05T_CHIqN->`__.

Overview
--------

We propose a set of type annotations to specify the intended nullness of
individual type usages. At a high level, our proposed semantics is
similar to existing tools’ treatment of nullness type annotations. We
treat nullness annotations as purely compile-time type refinements
without effect on runtime semantics. Besides the typical
“`nullable <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.ejpb5ee0msjt>`__”
and
“`non-nullable <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.8wgyiwyvi49f>`__”
refinements, we distinguish values of “`unspecified
nullness <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.xb9w6p3ilsq3>`__”,
which typically arise from unannotated code, and allow for (with
optional warnings if desired) type checks involving “unspecified
nullness” to succeed even when they are unsound.

To avoid excessive annotation overhead in hand-written Java code while
clearly distinguishing annotated from legacy code, we also propose a set
of explicit defaulting annotations. In the absence of a defaulting
annotation, type usages will be considered of “unspecified nullness.”

We give `semantics <#semantics>`__ to these annotations independent of
particular tools. We don’t specify what tools must do with that semantic
information, nor do we forbid them from adding to it (e.g., with flow
typing in implementation code). We merely define how to interpret our
annotations in method, field, and class declarations. This approach
gives documented meaning to annotations appearing in declarations while
allowing Java code authors to use their normal toolchain to compile code
(though we do recommend code authors use additional tools to minimize
the chance of incorrect annotations). Crucially, this approach also
allows any tool to interpret annotations that appear in Java bytecode
produced by other tools.

**Note:** we have adopted precise meanings for the terms used in this
document; it will be necessary to read (or refer to) the
`glossary <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit>`__
document in order to interpret this information correctly.

General
-------

The package name will be ``org.jspecify.annotations``.
[`#1 <https://github.com/jspecify/jspecify/issues/1>`__]

We will release one canonical artifact, in which all annotations have
runtime retention.
[`#28 <https://github.com/jspecify/jspecify/issues/28>`__] None of the
annotations defined in this document will be marked
`repeatable <https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/annotation/Repeatable.html>`__.

The type-use annotations
------------------------

In this release, we will provide a single parameterless type-use
annotation called ``@Nullable``.

.. _structural-legality-type-use:

Structural legality
~~~~~~~~~~~~~~~~~~~

The set of type-use annotations are specified to be *structurally* legal
in the set of circumstances detailed below. Where annotations are
structurally legal, semantic violations are of course still possible.

-  Illegal on type usages that are `categorically
   non-nullable <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.2m67iuk50zcb>`__
   (this supersedes all rules that follow):
   [`#17 <https://github.com/jspecify/jspecify/issues/17>`__]

   -  When the annotated type usage is of any primitive type.
   -  For an outer type used as a component type in an inner type
      expression.
   -  In a `non-nullable type
      context <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.d00h1zvk0vt3>`__.
   -  Note that the following rules still apply to any non-root
      component types of such type usages.

-  Otherwise, legal on any
   `non-root <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.j1ewrpknx869>`__
   component type, regardless of the root type or surrounding `type
   context <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.pfoww2aic35t>`__.

   -  This may be a `type
      argument <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.3gm7aajjj46m>`__,
      explicit wildcard bound, array component type, or the type used in
      a variadic parameter declaration.
   -  For example, ``Iterator<@Nullable String>`` is structurally legal
      *anywhere* that ``Iterator<String>`` is.
   -  Cannot annotate a type parameter or wildcard *itself*, only their
      bounds. [`#19 <https://github.com/jspecify/jspecify/issues/19>`__,
      `#31 <https://github.com/jspecify/jspecify/issues/31>`__]

-  Legal in the following type contexts (including when the type usage
   is a `type
   variable <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.uxek2gfsybvo>`__,
   regardless of its bound):
   [`#17 <https://github.com/jspecify/jspecify/issues/17>`__]

   -  Return type of a method.
   -  Formal parameter type (of a method, constructor, or lambda
      expression).
   -  Field type.
   -  Type parameter upper bound (but *not* the type parameter itself)
      [`#60 <https://github.com/jspecify/jspecify/issues/60>`__].

-  Illegal when applied to a class declaration.
   [`#7 <https://github.com/jspecify/jspecify/issues/7>`__]

   -  For example, ``public @Nullable class Foo {}`` is not allowed.

-  Except as already indicated, the legality of applying to a type usage
   in `implementation
   code <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.cjuxrgo7keqs>`__
   (and the semantics of doing so) is currently unspecified:

   -  A local variable type.
   -  The type in a cast expression.
   -  An array or object creation expression.
   -  An explicit type argument supplied to a generic method or
      constructor (including via a member reference), or to an instance
      creation expression for a generic class.

Tools are encouraged to treat an illegally placed annotation in Java
source code as an error. In bytecode, illegally placed annotations may
be best ignored.

Type usages defined here as *legal* or *illegal* (as opposed to
*unspecified*) to receive type use annotations are considered **in
scope** of this specification. Tools may extend this specification to
*unspecified* type usages at their own choosing.

The defaulting annotations
--------------------------

In this release, we will provide a single parameterless declaration
annotation called ``@NullMarked``.
[`#5 <https://github.com/jspecify/jspecify/issues/5>`__,
`#87 <https://github.com/jspecify/jspecify/issues/87>`__]

.. _structural-legality-defaulting:

Structural legality
~~~~~~~~~~~~~~~~~~~

-  The set of defaulting annotations can be applied to:

   -  A *named* class.
   -  A package. [*debated*
      `#34 <https://github.com/jspecify/jspecify/issues/34>`__]
   -  A module. [*debated*
      `#34 <https://github.com/jspecify/jspecify/issues/34>`__]
   -  *Not* a method
      [`#43 <https://github.com/jspecify/jspecify/issues/43>`__],
      constructor
      [`#43 <https://github.com/jspecify/jspecify/issues/43>`__], or
      field [`#50 <https://github.com/jspecify/jspecify/issues/50>`__].
      *debated*

Semantics
---------

In this section, we discuss how type checking should play out.

Type hierarchy
~~~~~~~~~~~~~~

Our nullability annotations produce the following apparent type
hierarchy [`#80 <https://github.com/jspecify/jspecify/issues/80>`__]:

| ``Foo`` (written in the context of ``@NullMarked``)
| ``⋖ Foo`` (written outside the context of ``@NullMarked``)
| ``⋖ @Nullable Foo``

It can be useful to conceptualize these similarly to `3-valued
logic <https://en.wikipedia.org/wiki/Three-valued_logic>`__
[`#33 <https://github.com/jspecify/jspecify/issues/33>`__]. While that’s
workable, it can be useful to instead define “unspecified nullness” as
an existential quantification over the other two (which logically still
validates the above hierarchy). Two unrelated occurrences of unspecified
nullness can sometimes be represented as two different variables,
similar to how the Java type system handles wildcards.

The above rules make ``@Nullable Object`` the top (least precise) type.
(Note that ``null`` is *not* the bottom type.) Here are some more
examples of subtyping, with types written in the context of
``@NullMarked``:

-  ``String ⋖ Object ⋖ @Nullable Object``
-  ``String ⋖ @Nullable String ⋖ @Nullable Object``
-  ``null ⋖ @Nullable String ⋖ @Nullable Object``

For a given set of types, we can define their **glb** (*greatest lower
bound*) as a type from the given set that is at least as specific as all
others. Similarly, the **lub** (*least upper bound*) of a set of types
is a type from the set that is at most as specific as all others.

Finally, a **type check** (e.g., to determine assignability) for a pair
of `augmented types <#augmented-type>`__ includes validating **both**
(a) a type check of the `base
types <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__,
handled by the off-the-shelf compiler, and (b) a type check for
nullability.

-  This specification additionally encourages allowing type checks
   involving unspecified nullness to succeed even when they are
   **unsound** (optionally with warnings, similar to “unchecked
   conversions” in Java generics), such as the following
   [`#33 <https://github.com/jspecify/jspecify/issues/33>`__]:

   -  a nullable type where a subtype of unspecified nullness is
      required
   -  a type of unspecified nullness where a subtype of a non-null type
      is required
   -  a type of unspecified nullness where a subtype of unspecified
      nullness is required

-  For usability reasons, many tools will not generate generate
   warnings/errors when applying unsound rules like those above. Others
   may generate them optionally, likely with the warnings/errors off by
   default. Even when a tool does report these warnings/errors, we
   **strongly** encourage the tool to permit users to suppress these
   warnings without suppressing other soundness violations.

-  When converting type components of parameterized types, their
   nullabilities should be considered invariant where their base types
   are.

   -  This means that “list of nullable string” and “list of non-null
      string” are not convertible to each other, but “list of non-null
      string” is convertible to ``List<? extends @Nullable String>``.

   -  In addition, we encourage unsoundly allowing unspecified nullness
      to be “the same type” as any nullness, even for invariant type
      components
      [`#33 <https://github.com/jspecify/jspecify/issues/33>`__]. In
      particular, we encourage allowing the following type checks to
      succeed unsoundly (similar to raw type conversions)
      [`#69 <https://github.com/jspecify/jspecify/issues/69>`__]:

      -  “a list of ``Bar`` instances that are not null” is “the same
         type as” “a list of ``Bar`` instances that have unspecified
         nullness”
      -  “a list of ``Bar`` instances that are nullable” is “the same
         type as” “a list of ``Bar`` instances that have unspecified
         nullness”

Defaulting annotations in effect
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

For a given type usage, we define the **defaulting annotation in
effect** to be the one located at the *nearest containing scope*
surrounding the type usage.

-  Class members are contained by classes, which may be contained by
   other class members or classes, and top-level classes are contained
   by packages, which may be contained by modules.
-  If no such defaulting annotation exists, then no defaulting
   annotation is in effect.

We call any type usage that itself carries a `structurally
legal <#structural-legality-type-use>`__ type-use annotation
**explicitly annotated**.

Parameterized types
~~~~~~~~~~~~~~~~~~~

This section directly builds on `JLS
4.5 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.5>`__
to extend nullability to parameterized types.

Parametric nullability
^^^^^^^^^^^^^^^^^^^^^^

If a type parameter’s bound is nullable, then unannotated usages of that
type parameter (inside the scope of ``@NullMarked``) have *parametric
nullability*.

Type arguments of parameterized types
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Tools may wish to check that type arguments are subtypes of the
corresponding bounds’ types (in addition to base type well-formedness,
see `JLS
4.5 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.5>`__).
As usual, tools may still wish to allow (with warning if desired)
unsound type arguments involving unspecified nullness.

Specifically, a tool might reject a parameterized type with an explicit
annotation, such as ``ImmutableList<@Nullable String>``, if
``ImmutableList``\ ’s type parameter is bounded to be non-null. The tool
might report an error when encountering this case in source code and
otherwise ignore the explicit ``@Nullable`` annotation.

Overriding
~~~~~~~~~~

If a method overrides other methods according to Java language rules
(see `JLS
8.4.8.1 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-8.html#jls-8.4.8.1>`__,
also cf. `JVMS
5.4.5 <https://docs.oracle.com/javase/specs/jvms/se14/html/jvms-5.html#jvms-5.4.5>`__),
then tools may wish to check that:

-  The overriding method’s augmented return type is be
   return-type-substitutable for the
   `supermethods <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.5nvbughni6vx>`__\ ’
   return types (corresponds to covariant return types for base types,
   an uncontroversial Java feature).

As usual, tools may wish to allow (with warning if desired) unsound
declarations involving unspecified nullness. Note that
`supermethods <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.5nvbughni6vx>`__
and
`superparameters <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.m2gxs1ddzqwp>`__
may be defined by members of parameterized supertypes.

Concerns around uninitialized objects
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

The broad intention of nullability is that type usages specified as
non-null should at runtime only represent non-null values. Since fields
have to be initialized before they can be non-null, this turns out to be
impractical in edge cases, however. Further, this problem can affect
method return values, e.g., when methods return field values. We
therefore likewise only expect non-null guarantees to hold for instance
(static, respectively) fields and method results by the time their
declaring class’s constructor (static initializer, respectively) has
finished (similar to when final fields are guaranteed to be
initialized).

Note this semantics does allow for situations in which null values may
be observable in “non-null” fields and method returns, namely while
objects are under construction. We simply encourage API owners to
minimize these cases for non-private (both static and instance) fields
and methods, which typically involves not “leaking” object references
outside an API until they’re fully constructed. Some tools may attempt
to identify such “leaks” and may attempt to ensure proper field
initialization as defined here during object construction.

Examples
~~~~~~~~

As an example, let’s consider a fragment of Guava’s ``ImmutableMap``:

.. code:: java

   @NullMarked
   public class ImmutableMap<K, V> implements Map<K, V> {
     public static <K, V> ImmutableMap<K, V> of(K key, V value);
     public @Nullable V get(@Nullable Object key);
   }

Because of the use of ``@NullMarked``, every type use in this class’s
declaration is fixed to either nullable or non-null (including
type-variable uses, since their type parameters are considered
implicitly bounded by non-null ``Object``).

-  Can the parameters to ``of()``\ ’s be null? No, from ``K`` and
   ``V``\ ’s bounds, which are determined implicitly by ``@NullMarked``.
-  Can ``get()``\ ’s return ``null``? Yes, from its explicit annotation.
-  It is a mismatch to refer to
   ``ImmutableMap<@Nullable String, Object>`` because
   ``@Nullable String`` is outside of ``K``\ ’s bounds.

To illustrate wildcards, consider a method return type
``ImmutableMap<? extends @Nullable String, ?>`` with no defaulting
annotation in scope:

-  Can the method return a null map? That is unspecified, since no
   defaulting annotation is in scope.
-  Can the map’s keys or values be null? No, because the wildcards
   inherit that bound from the bounds of ``K`` and ``V`` in
   ``ImmutableMap``.

As another example, Guava’s ``Function`` would be declared as follows to
allow functions that accept and/or return ``null``:

.. code:: java

   @NullMarked
   public interface Function<F extends @Nullable Object, T extends @Nullable Object> {
     T apply(F in);
   }

Note ``F``\ ’s and ``T``\ ’s admittedly verbose but very explicit
``extends @Nullable Object`` bounds, which mean that ``apply``\ ’s
parameter and result are of parametric nullability.

Concrete ``Function`` implementations can still choose not to support
nulls:

.. code:: java

   @NullMarked
   class Foo implements Function<String, Integer> {
   }

Discussion: Expression types
----------------------------

It is not the purpose of this proposal to dictate precise behavior that
checkers must follow. But we expect Java source code analyzers to want
to extend our semantics from type usages as defined above to expression
types (including expression type components).

As an example, consider a hypothetical annotated version of
``java.util.List``:

.. code:: java

   @NullMarked
   public interface List<E extends @Nullable Object> extends Collection<E> {
     public boolean add(E element);
     public E get(int index);
   }

Now, in client code like this:

.. code:: java

   @NullMarked
   public String foo(List<String> xs) {
     xs.add(null); // mismatch: add() expects non-null String
     return xs.get(0); // compatible: get() returns non-null String
   }

Note that (because of the defaulting annotation in effect) both
``foo``\ ’s return type’s and ``xs``\ ’s ``String`` type argument’s are
non-null types. That means that, considering ``xs``\ ’s type argument,
``xs.add()``\ ’s expected parameter type is likewise non-null
``String``, as is ``xs.get()``\ ’s return type.

Note that unlike with base types, a ``null`` reference is *no longer*
automatically assignable to any type:

-  It clearly isn’t usable where a non-null value is required (as in the
   example above).
-  It also isn’t assignable to types with parametric nullability (since
   their type parameters permit non-null instantiations).

For the latter point, consider the following example:

.. code:: java

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

Again we do not prescribe how tools handle any of these scenarios, so
tools may be silent or issue lower-priority warnings on source lines
marked “mismatch” here. They’re purely illustrative of how we imagine
tools will apply semantics to expression typing.

The Simplest(?) Thing That Could Possibly Work for subtyping
------------------------------------------------------------

   .. rubric:: High-level overview
      :name: high-level-overview

   It may be that some people will use this doc to guide their
   implementations in the near future. Please don’t hesitate to let
   cpovirk know of any confusing bits.

   I should probably preemptively clarify at least one thing. In this
   doc, I have tried to distinguish explicitly between 3 “kinds of
   nullability” of a given type usage. Each kind is derived (at least in
   part) from the previous:

   1. What annotation (if any) appears directly on that type usage?
   2. What is the `nullness operator <#nullness-operator>`__ of that
      type usage?
   3. For that type usage…

      -  Is it safe to assume that is not ``null``?
      -  Is it safe to put a ``null`` into it?
      -  neither (as in “parametric nullness”)
      -  both (as in “unspecified nullness” in “lenient mode”)

   TODO(cpovirk): Link to my “Don’t say ‘nullable’” doc once I write it.

.. _concept-references:

References to concepts defined by this spec
-------------------------------------------

When a rule in this spec refers to any concept that is defined in this
spec (for example, `substitution <#substitution>`__ or
`containment <#containment>`__), apply this spec’s definition (as
opposed to other definitions, such as the ones in the JLS).

Additionally, when a rule in this spec refers to a JLS rule that in turn
refers to a concept that is defined in this spec, likewise apply this
spec’s definition.

In particular, when a JLS rule refers to types, apply this spec’s
definition of `augmented types <#augmented-type>`__ (as oppposed to
`base
types <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__).

Nullness operator
-----------------

An nullness operator is one of 4 values:

-  ``UNION_NULL``
-  ``NO_CHANGE``
-  ``UNSPECIFIED``
-  ``MINUS_NULL``

..

   The distinction among these 4 values is similar to the distinction
   among the Kotlin types ``Foo?``, ``Foo``, ``Foo!``, and ``Foo!!``,
   respectively.

Augmented type
--------------

An augmented type consists of a `base
type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__
and a `nullness operator <#nullness-operator>`__ corresponding to *each*
of its `type
components <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.g7gl9fwq1tt5>`__.

   This differs from our current `glossary
   definition <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.367l48xhsikk>`__,
   which is written in terms of a “nullness” for each component, not a
   “nullness operator.” Still, the glossary’s concept of the “nullness”
   of a type is derivable from the type’s nullness operator. Notably,
   the glossary’s “nullable” type is our `least convenient
   world <#multiple-worlds>`__\ ’s `trusted null-inclusive under every
   parameterization <#trusted-null-inclusive-under-every-parameterization>`__,
   and the glossary’s “non-nullable” type is our least convenient
   world’s `trusted null-exclusive under every
   parameterization <#trusted-null-exclusive-under-every-parameterization>`__.

For our purposes, base types (and thus augmented types) include not just
class and interface types, array types, and type variables but also
`intersection types <#intersection-types>`__ and the null type. This is
true even though the JLS sometimes does not supply rules for
intersection types and sometimes has separate rules for the null type.

The goal of this spec is to define rules for augmented types compatible
with those that the JLS defines for base types.

   In almost all cases, this spec agrees with the JLS’s rules when
   specifying what *base* types appear in a piece of code. It makes an
   exception for `“Bound of an unbounded
   wildcard,” <#unbounded-wildcard>`__ for which it specifies a bound of
   ``Object`` that the JLS does not specify.

When this spec uses capital letters, they refer to augmented types
(unless otherwise noted). This is in contrast to the JLS, which
typically uses them to refer to base types.

When this spec refers to “the nullness operator of” a type ``T``, it
refers specifically to the nullness operator of the type component that
is the entire type ``T``, without reference to the nullness operator of
any other type components of ``T``.

   For example, the nullness operator of ``List<@Nullable Object>``
   would be ``NO_CHANGE`` (at least in a `null-marked
   context <#null-marked-context>`__), even though the nullness operator
   of its element type ``Object`` is ``UNION_NULL``.

Null-marked context
-------------------

To determine whether a type usage appears in a null-marked context:

Look for an ``@org.jspecify.nullness.NullMarked`` annotation on any of
the enclosing scopes surrounding the type usage.

Class members are enclosed by classes, which may be enclosed by other
class members or classes. and top-level classes are enclosed by
packages, which may be enclosed by modules.

   Packages are *not* enclosed by “parent” packages.

..

   This definition of “enclosing” likely matches `the definition in the
   Java compiler
   API <https://docs.oracle.com/en/java/javase/14/docs/api/java.compiler/javax/lang/model/element/Element.html#getEnclosingElement()>`__.

If an ``@org.jspecify.nullness.NullMarked`` annotation exists on one of
these scopes, then the type usage is in a null-marked context.
Otherwise, it is not.

.. _augmented-type-of-usage:

Augmented type of a type usage appearing in code
------------------------------------------------

For most type usages in source code or bytecode on which JSpecify
nullness annotations are structurally valid, this section defines how to
determine their `augmented types <#augmented-type>`__. Note, however,
that rules for specific cases below take precedence over the general
rule here.

Because the JLS already has rules for determining the `base
type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__
for a type usage, this section covers only how to determine its
`nullness operator <#nullness-operator>`__.

To determine the nullness operator, apply the following rules in order.
Once one condition is met, skip the remaining conditions.

-  If the type usage is annotated with
   ``@org.jspecify.nullness.Nullable``, its nullness operator is
   ``UNION_NULL``.
-  If the type usage appears in a `null-marked
   context <#null-marked-context>`__, its nullness operator is
   ``NO_CHANGE``.
-  Its nullness operator is ``UNSPECIFIED``.

..

   The choice of nullness operator is *not* affected by any nullness
   operator that appears in a corresponding location in a supertype. For
   example, if one type declares a method whose return type is annotated
   ``@Nullable``, and if another type overrides that method but does not
   declare the return type as ``@Nullable``, then the override’s return
   type will *not* have nullness operator ``UNION_NULL``.

   The rules here never produce the fourth nullness operator,
   ``MINUS_NULL``. (It will appear later in
   `substitution <#substitution>`__. Additionally, we expect for tool
   authors to produce ``MINUS_NULL`` based on the results of null checks
   in implementation code.) However, if tool authors prefer, they can
   safely produce ``MINUS_NULL`` in any case in which it is equivalent
   to ``NO_CHANGE``. For example, there is no difference between a
   ``String`` with ``NO_CHANGE`` and a ``String`` with ``MINUS_NULL``.

.. _intersection-types:

Augmented type of an intersection type
--------------------------------------

   Technically speaking, the JLS does not define syntax for an
   intersection type. Instead, it defines a syntax for type parameters
   and casts that supports multiple types. Then the intersection type is
   derived from those. Intersection types can also arise from operations
   like `capture conversion <#capture-conversion>`__. See `JLS
   4.9 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.9>`__.

   One result of all this is that it’s never possible for a programmer
   to write an annotation “on an intersection type.”

This spec assigns a `nullness operator <#nullness-operator>`__ to each
individual element of an intersection type, following our normal rules
for type usages. It also assigns a nullness operator to the intersection
type as a whole. The nullness operator of the type as a whole is always
``NO_CHANGE``.

   This lets us provide, for every `base
   type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__,
   a rule for computing its `augmented type <#augmented-type>`__. But we
   require ``NO_CHANGE`` so as to avoid questions like whether “a
   ``UNION_NULL`` intersection type whose members are ``UNION_NULL``
   ``Foo`` and ``UNION_NULL`` ``Bar``” is a subtype of “a ``NO_CHANGE``
   intersection type with those same members.” Plus, it would be
   difficult for tools to output the nullness operator of an
   intersection type in a human-readable way.

..

   To avoid ever creating an intersection type with a nullness operator
   other than ``NO_CHANGE``, we define special handling for intersection
   types under `“Applying a nullness operator to an augmented
   type.” <#applying-operator>`__

.. _unbounded-wildcard:

Bound of an “unbounded” wildcard
--------------------------------

In source, an unbounded wildcard is written as ``<?>``. This section
does **not** apply to ``<? extends Object>``, even though that is often
equivalent to ``<?>``. See `JLS
4.5.1 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.5.1>`__.

In bytecode, such a wildcard is represented as a wildcard type with an
empty list of upper bounds and an empty list of lower bounds. This
section does **not** apply to a wildcard with any bounds in either list,
even a sole upper bound of ``Object``.

   For a wildcard with an explicit bound of ``Object`` (that is,
   ``<? extends Object>``, perhaps with an annotation on ``Object``),
   instead apply `the normal rules <#augmented-type-of-usage>`__ for the
   explicit bound type.

If an unbounded wildcard appears in a `null-marked
context <#null-marked-context>`__, then it has a single upper bound
whose `base
type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__
is ``Object`` and whose `nullness operator <#nullness-operator>`__ is
``UNION_NULL``.

If an unbounded wildcard appears outside a null-marked context, then it
has a single upper bound whose base type is ``Object`` and whose
nullness operator is ``UNSPECIFIED``.

   In both cases, we specify a bound that does not exist in the source
   or bytecode, deviating from the JLS. Because the base type of the
   bound is ``Object``, this should produce no user-visible differences
   except to tools that implement JSpecify nullness analysis.

Whenever a JLS rule refers specifically to ``<?>``, disregard it, and
instead apply the rules for ``<? extends T>``, where ``T`` has a base
type of ``Object`` and the nullness operator defined by this section.

.. _object-bounded-type-parameter:

Bound of an ``Object``-bounded type parameter
---------------------------------------------

In source, an ``Object``-bounded type parameter can be writen in either
of 2 ways:

-  ``<T>``
-  ``<T extends Object>`` with no JSpecify nullness type annotations on
   the bound

See `JLS
4.4 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.4>`__.

In bytecode, ``<T>`` and ``<T extends Object>`` are both represented as
a type parameter with only a single upper bound, ``Object``, and no
JSpecify nullness type annotations on the bound.

If an ``Object``-bounded type parameter appears in a `null-marked
context <#null-marked-context>`__, then its bound has a `base
type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__
of ``Object`` and a `nullness operator <#nullness-operator>`__ of
``NO_CHANGE``.

   Note that this gives ``<T>`` a different bound than ``<?>`` (though
   only in a null-marked context).

If an ``Object``-bounded type parameter appears outside a null-marked
context, then its bound has a base type of ``Object`` and a nullness
operator of ``UNSPECIFIED``.

   All these rules match the behavior of `our normal
   rules <#augmented-type-of-usage>`__ for determining the `augmented
   type <#augmented-type>`__ of the bound ``Object``. The only “special”
   part is that we consider the source code ``<T>`` to have a bound of
   ``Object``, just as it does when compiled to bytecode.

.. _null-types:

Augmented null types
--------------------

The JLS refers to “the null type.” In this spec, we assign a `nullness
operator <#nullness-operator>`__ to all types, including the null type.
This produces multiple null types:

-  the null `base
   type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__
   with nullness operator ``NO_CHANGE``: the “bottom”/“nothing” type
   used in `capture conversion <#capture-conversion>`__

      No value, including ``null`` itself, has this type.

-  the null base type with nullness operator ``MINUS_NULL``

      This is equivalent to the previous type. Tools may use the 2
      interchangeably.

-  the null base type with nullness operator ``UNION_NULL``: the type of
   the null reference

-  the null base type with nullness operator ``UNSPECIFIED``

      This may be relevant only in implementation code.

.. _multiple-worlds:

The least convenient world and the most convenient world
--------------------------------------------------------

Some of the rules in this spec come in 2 versions, 1 for “the least
convenient world” and 1 for “the most convenient world.”

Tools may implement either or both versions of the rules.

   Our goal is to allow tools and their users to choose their desired
   level of strictness in the presence of ``UNSPECIFIED``. “The least
   convenient world” usually assumes that types are incompatible unless
   it has enough information to prove they are compatible; “the most
   convenient world” assumes that types are compatible unless it has
   enough information to prove they are incompatible.

   Thus, strict tools may want to implement the least-convenient-world
   version of rules, and lenient tools may wish to implement the
   most-convenient-world version. Or a tool might implement both and let
   users select which rules to apply.

   Another possibility is for a tool to implement both versions and to
   use that to distinguish between “errors” and “warnings.” Such a tool
   might run each check first in the least convenient world and then, if
   the check fails, run it again in the most convenient world. If the
   check fails in both worlds, the tool would produce an error. If it
   passes only because of the most convenient interpretation, the tool
   would produce a warning.

The main body of each section describes the *least*-convenient-world
rule. If the most-convenient-world rule differs, the differences are
explained at the end.

.. _propagating-multiple-worlds:

Propagating the most/least convenient world
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When one rule in this spec refers to another, it refers to the rule for
the same “world.” For example, when the rules for
`containment <#containment>`__ refer to the rules for
`subtyping <#subtyping>`__, the most-convenient-world containment check
applies the most-convenient-world subtyping check, and the
least-convenient-world containment check applies the
least-convenient-world subtyping check.

This applies even if a rule says it is the same for both worlds: It
means “the same except that any other rules are applied in the
corresponding world.”

Same type
---------

``S`` and ``T`` are the same type if ``S`` is a `subtype <#subtyping>`__
of ``T`` and ``T`` is a subtype of ``S``.

Subtyping
---------

``A`` is a subtype of ``F`` if both of the following conditions are met:

-  ``A`` is a subtype of ``F`` according to the `nullness-delegating
   subtyping rules for Java <#nullness-delegating-subtyping>`__.
-  ``A`` is a `nullness subtype <#nullness-subtyping>`__ of ``F``.

.. _nullness-delegating-subtyping:

Nullness-delegating subtyping rules for Java
--------------------------------------------

The Java subtyping rules are defined in `JLS
4.10 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.10>`__.
We add to them as follows:

-  `As always <#concept-references>`__, interpret the Java rules as
   operating on `augmented types <#augmented-type>`__, not `base
   types <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__.
   However, when applying the Java direct-supertype rules themselves,
   *ignore* the `nullness operator <#nullness-operator>`__ of the input
   types and output types. The augmented types matter only when the Java
   rules refer to *other* rules that are defined in this spec. *Those*
   rules respect the nullness operator of some type components — but
   never the nullness operator of the type component that represents the
   whole input or output type.

      To “ignore” the output’s nullness operator, we recommend
      outputting a value of ``NO_CHANGE``, since that is valid for all
      types, including `intersection types <#intersection-types>`__.

-  When the Java array rules require one type to be a *direct* supertype
   of another, consider the direct supertypes of ``T`` to be *every*
   type that ``T`` is a `subtype <#subtyping>`__ of (as always, applying
   the definition of subtyping in this spec).

Nullness subtyping
------------------

   The primary complication in subtyping comes from type-variable
   usages. Our rules for them must account for every combination of type
   arguments with which a given generic type can be parameterized.

``A`` is a nullness subtype of ``F`` if any of the following conditions
are met:

-  ``F`` is `trusted null-inclusive under every
   parameterization <#trusted-null-inclusive-under-every-parameterization>`__.
-  ``A`` is `trusted null-exclusive under every
   parameterization <#trusted-null-exclusive-under-every-parameterization>`__.
-  ``A`` has a `nullness-subtype-establishing
   path <#nullness-subtype-establishing-path>`__ to any type whose base
   type is the same as the base type of ``F``.

Nullness subtyping (and thus subtyping itself) is *not* transitive.

(Contrast this with our `nullness-delegating
subtyping <#nullness-delegating-subtyping>`__ rules and
`containment <#containment>`__ rules: Each of those is defined as a
transitive closure. However, technically speaking, `there are cases in
which those should not be transitive,
either <https://groups.google.com/d/msg/jspecify-dev/yPnkx_GSb0Q/hLgS_431AQAJ>`__.
Fortunately, this “mostly transitive” behavior is exactly the behavior
that implementations are likely to produce naturally. Maybe someday we
will find a way to specify this fully correctly.)

Nullness subtyping (and thus subtyping itself) is *not* reflexive.

   It does end up being reflexive in the `most convenient
   world <#multiple-worlds>`__. We don’t state that as a rule for 2
   reasons: First, it arises naturally from the definitions in that
   world. Second, we don’t want to suggest that subtyping is reflexive
   in the `least convenient world <#multiple-worlds>`__.

Trusted null-inclusive under every parameterization
---------------------------------------------------

A type is trusted null-inclusive under every parameterization if it
meets either of the following conditions:

-  Its `nullness operator <#nullness-operator>`__ is ``UNION_NULL``.
-  It is an `intersection type <#intersection-types>`__ whose elements
   all are trusted null-inclusive under every parameterization.

**Most convenient world:** The rule is the same except that the
requirement for “``UNION_NULL``” is loosened to “``UNION_NULL`` or
``UNSPECIFIED``.”

Trusted null-exclusive under every parameterization
---------------------------------------------------

A type is trusted null-exclusive under every parameterization if it has
a `nullness-subtype-establishing
path <#nullness-subtype-establishing-path>`__ to either of the
following:

-  any type whose `nullness operator <#nullness-operator>`__ is
   ``MINUS_NULL``

-  any augmented class or array type

      This rule refers specifically to a “class or array type,” as
      distinct from other types like type variables and `intersection
      types <#intersection-types>`__.

Nullness-subtype-establishing path
----------------------------------

``A`` has a nullness-subtype-establishing path to ``F`` if both of the
following hold:

-  ``A`` has `nullness operator <#nullness-operator>`__ ``NO_CHANGE`` or
   ``MINUS_NULL``.

-  There is a path from ``A`` to ``F`` through
   `nullness-subtype-establishing direct-supertype
   edges <#nullness-subtype-establishing-direct-supertype-edges>`__.

      The path may be empty. That is, ``A`` has a
      nullness-subtype-establishing path to itself — as long as it has
      one of the required nullness operators.

**Most convenient world:** The rules are the same except that the
requirement for “``NO_CHANGE`` or ``MINUS_NULL``” is loosened to
“``NO_CHANGE``, ``MINUS_NULL``, or ``UNSPECIFIED``.”

Nullness-subtype-establishing direct-supertype edges
----------------------------------------------------

``T`` has nullness-subtype-establishing direct-supertype edges to the
union of the nodes computed by the following 2 rules:

Upper-bound rule:

-  if ``T`` is an augmented `intersection type <#intersection-types>`__:
   all the intersection type’s elements whose `nullness
   operator <#nullness-operator>`__ is ``NO_CHANGE`` or ``MINUS_NULL``
-  if ``T`` is an augmented type variable: all the corresponding type
   parameter’s upper bounds whose nullness operator is ``NO_CHANGE`` or
   ``MINUS_NULL``
-  otherwise: no nodes

Lower-bound rule:

-  for every type parameter ``P`` that has a lower bound whose `base
   type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__
   is the same as ``T``\ ’s base type and whose nullness operator is
   ``NO_CHANGE``: the type variable ``P``

   TODO(cpovirk): What if the lower bound has some other nullness
   operator? I’m pretty sure that we want to allow ``UNSPECIFIED`` in
   the most convenient world (as we did before my recent edits), and we
   may want to allow more.

-  otherwise: no nodes

**Most convenient world:** The rules are the same except that the
requirements for “``NO_CHANGE`` or ``MINUS_NULL``” are loosened to
“``NO_CHANGE``, ``MINUS_NULL``, or ``UNSPECIFIED``.”

Containment
-----------

The Java rules are defined in `JLS
4.5.1 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-4.html#jls-4.5.1>`__.
We add to them as follows:

-  Disregard the 2 rules that refer to a bare ``?``. Instead, treat
   ``?`` like ``? extends Object``, where the `nullness
   operator <#nullness-operator>`__ of the ``Object`` bound is specified
   by `“Bound of an unbounded wildcard.” <#unbounded-wildcard>`__

      This is just a part of our universal rule to treat a bare ``?``
      like ``? extends Object``.

-  The rule written specifically for ``? extends Object`` applies only
   if the nullness operator of the ``Object`` bound is ``UNION_NULL``.

-  When the JLS refers to the same type ``T`` on both sides of a rule,
   the rule applies if and only if this spec defines the 2 types to be
   the `same type <#same-type>`__.

**Most convenient world:** The rules are the same except that the
requirement for “``UNION_NULL``” is loosened to “``UNION_NULL`` or
``UNSPECIFIED``.”

Substitution
------------

   Substitution on Java base types barely requires an explanation: See
   `JLS
   1.3 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-1.html#jls-1.3>`__.
   Substitution on `augmented types <#augmented-type>`__, however, is
   trickier: If ``Map.get`` returns “``V`` with `nullness
   operator <#nullness-operator>`__ ``UNION_NULL``,” and if a user has a
   map whose value type is “``String`` with nullness operator
   ``UNSPECIFIED``,” then what does its ``get`` method return? Naive
   substitution would produce “``String`` with nullness operator
   ``UNSPECIFIED`` with nullness operator ``UNION_NULL``.” To reduce
   that to a proper augmented type with a single nullness operator, we
   define this process.

To substitute each type argument ``Aᵢ`` for each corresponding type
parameter ``Pᵢ``:

For every type-variable usage ``V`` whose `base
type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__
is ``Pᵢ``, replace ``V`` with the result of the following operation:

-  If ``V`` is `trusted null-exclusive under every
   parameterization <#trusted-null-exclusive-under-every-parameterization>`__
   in the `least convenient world <#multiple-worlds>`__, then replace it
   with the result of `applying <#applying-operator>`__ ``MINUS_NULL``
   to ``Aᵢ``.

      This is the one instance in which a rule references another rule
      to be run under a *different* “world.” Normally, all rules are run
      `under the same “world.” <#propagating-multiple-worlds>`__ But in
      this instance, the null-exclusivity rule (and all rules that it in
      turn applies) are always run in the least convenient world.

   ..

      This special case improves behavior in “the
      ``ImmutableList.Builder`` case”: Consider an unannotated user of
      that class. Its builder will have an element type whose `nullness
      operator <#nullness-operator>`__ is ``UNSPECIFIED``. Without this
      special case, ``builder.add(objectUnionNull)`` would pass the
      subtyping check in the `most convenient
      world <#multiple-worlds>`__. This would happen even though we have
      enough information to know that the parameter to ``add`` is
      universally null-exclusive — even in the most convenient world.
      The special case here makes that subtyping check fail.

-  Otherwise, replace ``V`` with the result of applying the nullness
   operator of ``V`` to ``Aᵢ``.

.. _applying-operator:

Applying a nullness operator to an augmented type
-------------------------------------------------

The process of applying a `nullness operator <#nullness-operator>`__
requires 2 inputs:

-  the nullness operator to apply
-  the `augmented type <#augmented-type>`__ (which, again, includes a
   `nullness operator <#nullness-operator>`__ for that type) to apply it
   to

The result of the process is an augmented type.

The process is as follows:

First, based on the pair of nullness operators (the one to apply and the
one from the augmented type), compute a “desired nullness operator.” Do
so by applying the following rules in order. Once one condition is met,
skip the remaining conditions.

-  If the nullness operator to apply is ``MINUS_NULL``, the desired
   nullness operator is ``MINUS_NULL``.
-  If either nullness operator is ``UNION_NULL``, the desired nullness
   operator is ``UNION_NULL``.
-  If either nullness operator is ``UNSPECIFIED``, the desired nullness
   operator is ``UNSPECIFIED``.
-  The desired nullness operator is ``NO_CHANGE``.

Then, if the input augmented type is *not* an `intersection
type <#intersection-types>`__, the output is the same as the input but
with its nullness operator replaced with the desired nullness operator.

Otherwise, the output is an intersection type. For every element ``Tᵢ``
of the input type, the output type has an element that is the result of
applying the desired nullness operator to ``Tᵢ``.

   In this case, the desired nullness operator is always equal to the
   nullness operator to apply that was an input to this process. That’s
   because the nullness operator of the intersection type itself is
   defined to always be ``NO_CHANGE``.

Capture conversion
------------------

The Java rules are defined in `JLS
5.1.10 <https://docs.oracle.com/javase/specs/jls/se14/html/jls-5.html#jls-5.1.10>`__.
We add to them as follows:

-  The parameterized type that is the output of the conversion has the
   same `nullness operator <#nullness-operator>`__ as the parameterized
   type that is the input type.

-  Disregard the JLS rule about ``<?>``. Instead, treat ``?`` like
   ``? extends Object``, where the `nullness
   operator <#nullness-operator>`__ of the ``Object`` bound is specified
   by `“Bound of an unbounded wildcard.” <#unbounded-wildcard>`__

      This is just a part of our universal rule to treat a bare ``?``
      like ``? extends Object``.

-  When a rule generates a lower bound that is the null type, we specify
   that its nullness operator is ``NO_CHANGE``. (See `“Augmented null
   types.” <#null-types>`__)
