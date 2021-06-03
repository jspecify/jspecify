JSpecify nullness spec draft
============================

This document is our draft specification for the semantics of a set of
nullness annotations.

   .. rubric:: Advice to readers
      :name: advice-to-readers

   For someone new to our nullness annotations, this document does not
   make a good introduction. This document is targeted more at tool
   authors or advanced users. For new users, we are working on
   additional documentation, including Javadoc, a User Guide, and a FAQ.

   .. rubric:: The world “nullable”
      :name: the-world-nullable

   In this doc, I aim not to refer to whether a type “is nullable.”
   Instead, I draw some distinctions, creating roughly 3 kinds of “Is it
   nullable?” questions we can ask for any given type usage. Each kind
   is derived (at least in part) from the previous:

   1. What annotation (if any) appears directly on that type usage?
   2. What is the `nullness operator <#nullness-operator>`__ of that
      type usage?
   3. For that type usage…

      -  Is it safe to assume that is not ``null``?
      -  Is it safe to put a ``null`` into it?
      -  neither (what we sometimes call “parametric nullness”)
      -  both (as can happen with ``UNSPECIFIED`` under lenient tools)

   .. rubric:: The scope of this spec
      :name: the-scope-of-this-spec

   This spec does not address *when* tools must apply a given check. For
   example, it does not state when tools must apply the
   `subtyping <#subtyping>`__ check.

   We expect that tools will typically apply them in the same cases that
   they apply standard Java checks. For example, if code contains the
   parameterized type ``List<@Nullable Foo>``, we would expect tools to
   check that ``@Nullable Foo`` is a subtype of the bound of the type
   parameter of ``List``.

   However, this is up to tool authors. In fact, JSpecify annotations
   can be used even by tools that are not “nullness checkers” at all.
   For example, a tool that lists the members of an API could show the
   nullness of each type in the API, without any checking that those
   types are “correct.”

Normative and non-normative sections
------------------------------------

This document contains some non-normative comments to emphasize points
or to anticipate likely questions. Those comments are set off as block
quotes.

   This is an example of a non-normative comment.

This document also links to other documents. Those documents are
non-normative.

   As of this writing, we know that this spec is not entirely
   sufficient: It sometimes relies on references to other documents
   (like the glossary). We will need to fix this by copying those
   definitions here.

Details common to all annotations
---------------------------------

-  The package name is ``org.jspecify.nullness``.
   [`#1 <https://github.com/jspecify/jspecify/issues/1>`__]
-  The Java module name is ``org.jspecify``.
   [`#181 <https://github.com/jspecify/jspecify/issues/181>`__]
-  The Maven artifact is ``org.jspecify:jspecify``.
   [`#181 <https://github.com/jspecify/jspecify/issues/181>`__]

All annotations have runtime retention.
[`#28 <https://github.com/jspecify/jspecify/issues/28>`__] None of the
annotations are marked
`repeatable <https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/lang/annotation/Repeatable.html>`__.

The type-use annotation
-----------------------

We provide a parameterless type-use annotation called ``@Nullable``.

Recognized locations for type-use annotations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

A location is a *recognized* location for our type-use annotation in the
circumstances detailed below. A type at recognized location has the
semantics described in this spec. The spec does not assign semantics to
types in other locations, nor to any annotations on such types.

-  Unrecognized when applied to any component of a type usage in
   `implementation
   code <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.cjuxrgo7keqs>`__:

   -  A local variable type.
   -  The type in a cast expression.
   -  An array or object creation expression.
   -  An explicit type argument supplied to a generic method or
      constructor (including via a member reference), or to an instance
      creation expression for a generic class.

   ..

      In practice, we expect that tools will treat annotations in most
      of the above locations much like they treat annotations in other
      locations. Still, this spec does not concern itself with
      implementation code: We believe that the most important domain for
      us to focus on is that of APIs.

-  Unrecognized when applied to a class declaration.
   [`#7 <https://github.com/jspecify/jspecify/issues/7>`__]

      For example, ``public @Nullable class Foo {}`` is unrecognized.

-  Unrecognized on type usages that are intrinsically non-nullable:
   [`#17 <https://github.com/jspecify/jspecify/issues/17>`__]

   -  When the annotated type usage is of any primitive type.

   -  For the outer type that qualifies an inner type.

         For example, ``@Nullable Foo.Bar`` is unrecognized because the
         outer type ``Foo`` is intrinsically non-nullable.

   -  In any of the following non-nullable type contexts:

      -  supertype in a class declaration
      -  thrown exception type
      -  enum constant declaration
      -  receiver parameter type

   But note that the following rules still apply to any non-root
   component types of such type usages.

-  Unrecognized on any component of a receiver parameter type.
   [`#157 <https://github.com/jspecify/jspecify/issues/157>`__]

      This partially overlaps with the rule about non-nullable type
      contexts above: Both rules cover an annotation on the
      intrinsically non-nullable top-level type, but this rule extends
      that to *all* components of the type.

-  Otherwise, recognized on any
   `non-root <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.j1ewrpknx869>`__
   component type, regardless of the root type or surrounding `type
   context <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.pfoww2aic35t>`__.

   -  This may be a `type
      argument <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.3gm7aajjj46m>`__,
      explicit wildcard bound, array component type, or the type used in
      a variadic parameter declaration.

         For example, the annotation in ``Iterator<@Nullable String>``
         is always recognized, aside from the exception for
         implementation code discussed above.

   -  Exception: Annotations on a type-parameter declaration or a
      wildcard *itself* are unrecognized.
      [`#19 <https://github.com/jspecify/jspecify/issues/19>`__,
      `#31 <https://github.com/jspecify/jspecify/issues/31>`__]

         Annotations on their *bounds* can still be recognized. So too
         can annotations on a *type-variable usage*.

-  Recognized in the following type contexts (including when the type
   usage is a `type
   variable <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=id.uxek2gfsybvo>`__,
   regardless of the corresponding type parameter’s bound):
   [`#17 <https://github.com/jspecify/jspecify/issues/17>`__]

   -  Return type of a method.

   -  Formal parameter type of a method or constructor.

   -  Field type.

   -  Type parameter upper bound.
      [`#60 <https://github.com/jspecify/jspecify/issues/60>`__]

         But, again, *not* the type parameter itself.

..

   Tools are encouraged to treat an unrecognized annotation in Java
   source code as an error unless they define semantics for that
   location — and especially to treat annotations on intrinsically
   non-nullable locations as an error. In bytecode, unrecognized
   annotations may be best ignored (again, unless a tool defines
   semantics for them).

The declaration annotation
--------------------------

We provide a single parameterless declaration annotation called
``@NullMarked``.
[`#5 <https://github.com/jspecify/jspecify/issues/5>`__,
`#87 <https://github.com/jspecify/jspecify/issues/87>`__]

Recognized locations for declaration annotations
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Our declaration annotation is specified to be *recognized* when applied
to the locations listed below:

-  A *named* class.
-  A package. [`#34 <https://github.com/jspecify/jspecify/issues/34>`__]
-  A module. [`#34 <https://github.com/jspecify/jspecify/issues/34>`__]

..

   *Not* a method
   [`#43 <https://github.com/jspecify/jspecify/issues/43>`__],
   constructor
   [`#43 <https://github.com/jspecify/jspecify/issues/43>`__], or field
   [`#50 <https://github.com/jspecify/jspecify/issues/50>`__].

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
   scope <#null-marked-scope>`__), even though the nullness operator of
   its element type ``Object`` is ``UNION_NULL``.

Null-marked scope
-----------------

To determine whether a type usage appears in a null-marked scope:

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

If one of those scopes is directly annotated with
``@org.jspecify.nullness.NullMarked``, then the type usage is in a
null-marked scope. Otherwise, it is not.

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
   scope <#null-marked-scope>`__, its nullness operator is
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
scope <#null-marked-scope>`__, then it has a single upper bound whose
`base
type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__
is ``Object`` and whose `nullness operator <#nullness-operator>`__ is
``UNION_NULL``.

If an unbounded wildcard appears outside a null-marked scope, then it
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
scope <#null-marked-scope>`__, then its bound has a `base
type <https://docs.google.com/document/d/1KQrBxwaVIPIac_6SCf--w-vZBeHkTvtaqPSU_icIccc/edit#bookmark=kix.k81vs7t5p45i>`__
of ``Object`` and a `nullness operator <#nullness-operator>`__ of
``NO_CHANGE``.

   Note that this gives ``<T>`` a different bound than ``<?>`` (though
   only in a null-marked scope).

If an ``Object``-bounded type parameter appears outside a null-marked
scope, then its bound has a base type of ``Object`` and a nullness
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

Multiple “worlds”
-----------------

Some of the rules in this spec come in 2 versions: One version requires
a property to hold “in all worlds,” and the other requires it to hold
only “in some world.”

Tool authors may wish to implement either or both versions of the rules.

   Our goal is to allow tools and their users to choose their desired
   level of strictness in the presence of ``UNSPECIFIED``. The basic
   idea is that, every time a tool encounters a type component with the
   nullness operator ``UNSPECIFIED``, it forks off 2 “worlds”: 1 in
   which the operator is ``UNION_NULL`` and 1 in which it is
   ``NO_CHANGE``.

   Since we lack a nullness specification for the type, we assume that
   either of the resulting worlds may be the “correct” specification.
   The all-worlds version of a rule, by requiring types to be compatible
   in all possible worlds, holds that types are incompatible unless it
   has enough information to prove they are compatible. The some-world
   version, by requiring types to be compatible only in *some* world,
   holds that types are compatible unless it has enough information to
   prove they are incompatible. (By behaving “optimistically,” the
   some-world checking behaves much like Kotlin’s checking of “platform
   types.”)

   Thus, strict tools may want to implement the all-worlds version of
   rules, and lenient tools may wish to implement the some-world
   version. Or a tool might implement both and let users select which
   rules to apply.

   Yet another possibility is for a tool to implement both versions and
   to use that to distinguish between “errors” and “warnings.” Such a
   tool might run each check first in the all-worlds version and then,
   if the check fails, run it again in the some-world version. If the
   check fails in both cases, the tool would produce an error. If it
   passes only because of the some-world version, the tool would produce
   a warning.

The main body of each section of the spec describes the all-worlds rule.
If the some-world rule differs, the differences are explained at the
end.

   A small warning: To implement the full some-world rules, a tool must
   also implement at least part of the all-worlds rules. Those rules are
   required as part of `substitution <#substitution>`__.

.. _propagating-multiple-worlds:

Propagating the choice of world
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

When one rule in this spec refers to another, it refers to the same
version of the rule. For example, when the rules for
`containment <#containment>`__ refer to the rules for
`subtyping <#subtyping>`__, the some-world containment check applies the
some-world subtyping check, and the all-worlds containment check applies
the all-worlds subtyping check.

This meta-rule applies except when a rule refers explicitly to a
particular version of another rule.

Same type
---------

``S`` and ``T`` are the same type if ``S`` is a `subtype <#subtyping>`__
of ``T`` and ``T`` is a subtype of ``S``.

The same-type check is *not* defined to be reflexive or transitive.

   For more discussion of reflexive and transitive checks, see the
   comments under `nullness subtyping <#nullness-subtyping>`__.

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

Nullness subtyping (and thus subtyping itself) is *not* defined to be
reflexive or transitive.

(Contrast this with our `nullness-delegating
subtyping <#nullness-delegating-subtyping>`__ rules and
`containment <#containment>`__ rules: Each of those is defined as a
transitive closure. However, technically speaking, `there are cases in
which those should not be transitive,
either <https://groups.google.com/d/msg/jspecify-dev/yPnkx_GSb0Q/hLgS_431AQAJ>`__.
Fortunately, this “mostly transitive” behavior is exactly the behavior
that implementations are likely to produce naturally. Maybe someday we
will find a way to specify this fully correctly.)

   Subtyping does end up being transitive when the check is required to
   hold in `all worlds <#multiple-worlds>`__. And it does end up being
   reflexive when the check is required to hold only in `some
   world <#multiple-worlds>`__. We don’t state those properties as rules
   for 2 reasons: First, they arise naturally from the definitions.
   Second, we don’t want to suggest that subtyping is reflexive and
   transitive under both versions of the rule.

   Yes, it’s pretty terrible for something called “subtyping” not to be
   reflexive or transitive. A more accurate name for this concept would
   be “consistent,” a term used in gradual typing. However, we use
   “subtyping” anyway. In our defense, we need to name multiple
   concepts, including not just subtyping but also
   `same-type <#same-type>`__ checks and `containment <#containment>`__.
   If we were to coin a new term for each, tool authors would need to
   mentally map between those terms and the analogous Java terms.

Trusted null-inclusive under every parameterization
---------------------------------------------------

A type is trusted null-inclusive under every parameterization if it
meets either of the following conditions:

-  Its `nullness operator <#nullness-operator>`__ is ``UNION_NULL``.
-  It is an `intersection type <#intersection-types>`__ whose elements
   all are trusted null-inclusive under every parameterization.

**Some-world version:** The rule is the same except that the requirement
for “``UNION_NULL``” is loosened to “``UNION_NULL`` or ``UNSPECIFIED``.”

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

**Some-world version:** The rules are the same except that the
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
   the some-world version (as we did before my recent edits), and we may
   want to allow more.

-  otherwise: no nodes

**Some-world version:** The rules are the same except that the
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

**Some-world version:** The rules are the same except that the
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
   in `all worlds <#multiple-worlds>`__, then replace it with the result
   of `applying <#applying-operator>`__ ``MINUS_NULL`` to ``Aᵢ``.

      This is the one instance in which a rule specifically refers to
      the `all-worlds <#multiple-worlds>`__ version of another rule.
      Normally, `a rule “propagates” its version to other
      rules <#propagating-multiple-worlds>`__. But in this instance, the
      null-exclusivity rule (and all rules that it in turn applies) are
      the `all-worlds <#multiple-worlds>`__ versions.

   ..

      This special case improves behavior in “the
      ``ImmutableList.Builder`` case”: Consider an unannotated user of
      that class. Its builder will have an element type whose `nullness
      operator <#nullness-operator>`__ is ``UNSPECIFIED``. Without this
      special case, ``builder.add(objectUnionNull)`` would pass the
      subtyping check in the `some-world <#multiple-worlds>`__ version.
      This would happen even though we have enough information to know
      that the parameter to ``add`` is universally null-exclusive,
      regardless of world. The special case here makes that subtyping
      check fail, as desired.

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
