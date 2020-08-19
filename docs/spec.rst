JSpecify 0.1 spec outline
=========================

About this document
-------------------

This document summarizes our current thinking for proposing a set of
nullness annotations. **Nothing here is “set in stone” yet.**

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

Goals
-----

Fulfill the `project
requirements <https://docs.google.com/document/d/1d5NgP4Pjk_ahkISpRa3nFb1FzPz9yS9luByiBj6qCsk/edit>`__
and `nullness-specific
requirements <https://docs.google.com/document/d/106aEs5DspNywGwdBwGX_iSSERicTPcSma7bQ8GYMZb4/edit>`__.

General
-------

The package name will be ``org.jspecify.annotations``.
[`#1 <https://github.com/jspecify/jspecify/issues/1>`__]

We will release one canonical artifact, in which all annotations have
runtime retention.
[`#28 <https://github.com/jspecify/jspecify/issues/28>`__]

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

-  Applying more than one of these annotations to the same `type
   component <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.g7gl9fwq1tt5>`__
   within the same type usage (since we currently define only
   ``@Nullable``, this means applying ``@Nullable`` twice) is illegal.
   [*debated*
   `#100 <https://github.com/jspecify/jspecify/issues/100>`__]

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
annotation called ``@DefaultNonNull``.
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

-  Applying more than one of these annotations to the same scope (since
   we currently define only ``@DefaultNonNull``, this means applying
   ``@DefaultNonNull`` twice) is illegal. [*debated*
   `#100 <https://github.com/jspecify/jspecify/issues/100>`__,
   `#87 <https://github.com/jspecify/jspecify/issues/87>`__]

Semantics
---------

In this section, we discuss how type checking should play out.

Type hierarchy
~~~~~~~~~~~~~~

Our nullability annotations produce the following apparent type
hierarchy [`#80 <https://github.com/jspecify/jspecify/issues/80>`__]:

| ``Foo`` (written in the context of ``@DefaultNonNull``)
| ``<: Foo`` (written outside the context of ``@DefaultNonNull``)
| ``<: @Nullable Foo``

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
``@DefaultNonNull``:

-  ``String <: Object <: @Nullable Object``
-  ``String <: @Nullable String <: @Nullable Object``
-  ``null <: @Nullable String <: @Nullable Object``

For a given set of types, we can define their **glb** (*greatest lower
bound*) as a type from the given set that is at least as specific as all
others. Similarly, the **lub** (*least upper bound*) of a set of types
is a type from the set that is at most as specific as all others.

Finally, a **type check** (e.g., to determine assignability) for a pair
of `augmented
types <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.367l48xhsikk>`__
includes validating **both** (a) a type check of the `base
types <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.e98jyoqkxvjd>`__,
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
type parameter (inside the scope of ``@DefaultNonNull``) have
*parametric nullability*.

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
also cf. \ `JVMS
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

Intended “meaning”
~~~~~~~~~~~~~~~~~~

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

   @DefaultNonNull
   public class ImmutableMap<K, V> implements Map<K, V> {
     public static <K, V> ImmutableMap<K, V> of(K key, V value);
     public @Nullable V get(@Nullable Object key);
   }

Because of the use of ``@DefaultNonNull``, every type use in this
class’s declaration is fixed to either nullable or non-null (including
type-variable uses, since their type parameters are considered
implicitly bounded by non-null ``Object``).

-  Can the parameters to ``of()``\ ’s be null? no (from ``K`` and
   ``V``\ ’s bounds, which are determined implicitly by
   ``@DefaultNonNull``)
-  Can ``get()``\ ’s return ``null``? yes (from its explicit annotation)
-  It is a mismatch to refer to
   ``ImmutableMap<@Nullable String, Object>`` because
   ``@Nullable String`` is outside of ``K``\ ’s bounds.

To illustrate wildcards, consider a method return type
``ImmutableMap<? extends @Nullable String, ?>`` with no defaulting
annotation in scope:

-  Can the method return a null map? That is unspecified (since no
   defaulting annotation is in scope)
-  Can the map’s keys be null? no. This is because the wildcard’s
   explicit bound, ``@Nullable String``, is intersected with the
   wildcarded type parameter ``K``\ ’s bound, non-null ``Object``. In
   other words, the explicit ``@Nullable`` here is ignored and might
   justify a warning. That warning can be avoided by writing
   ``? extends String`` without changing the resulting key type:
   ``String``, being not explicitly annotated, has unspecified
   nullability, but that’s still superseded by the wildcarded type
   parameter’s bound, non-null ``Object``.
-  Can the map’s values be null? Also no, because the wildcard inherits
   that bound from the bound of ``V`` in ``ImmutableMap``.

As another example, Guava’s ``Function`` would be declared as follows to
allow functions that accept and/or return ``null``:

.. code:: java

   @DefaultNonNull
   public interface Function<F extends @Nullable Object, T extends @Nullable Object> {
     T apply(F in);
   }

Note ``F``\ ’s and ``T``\ ’s admittedly verbose but very explicit
``extends @Nullable Object`` bounds, which mean that ``apply``\ ’s
parameter and result are of parametric nullability.

Concrete ``Function`` implementations can still choose not to support
nulls:

.. code:: java

   @DefaultNonNull
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

   @DefaultNonNull
   public interface List<E extends @Nullable Object> extends Collection<E> {
     public boolean add(E element);
     public E get(int index);
   }

Now, in client code like this:

.. code:: java

   @DefaultNonNull
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

   @DefaultNonNull
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
