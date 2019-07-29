# Our Goal

Java static analysis tools, such as Error Prone and those built into IDEs, have
been very successful at enabling automated refactorings and bug prevention. Many
of their most beneficial analyses require certain Java annotations (such as
`@Nullable` or `@Immutable`) to be present in the source or class file. Yet for
the 15 years since Java annotations were introduced (five years for type-use
annotations), users have never had a clear answer for which incarnation of these
annotation classes they can safely adopt. Many such artifacts have been created,
but there has never been a serious and successful effort to provide **a single
well-specified standard with industry consensus**. The lack of this artifact has
constrained the prevalence and overall value of static analysis technology
throughout the industry. It has encouraged today's tools to accept a wide
variety of annotations, treating them equivalently, which renders the annotation
specifications meaningless (an API owner cannot know whether applying a
particular annotation is correct without knowing which tool its users will be
using to interpret them). Finally, it complicates interoperation between Java
and other JVM languages, which sometimes expect additional API metadata (such as
nullability) that the Java language can't express. This missing artifact, with
full specifications, is what we are working to create.[^1]

[^1]: We have seen the xkcd comic. Please do not send us the xkcd comic. We know
    about the xkcd comic.

<!-- TODO(eaftan): add links to JVMLS talk, requirements doc & design docs
     when they are ready -->

# Participants

People from the following projects and organizations are collaborating on
this project:

Project           | Organization
----------------- | ------------
Android           | Google
Checker Framework |
Eclipse           |
Error Prone       | Google
Guava             | Google
IntelliJ IDEA     | JetBrains
Kotlin            | JetBrains
NullAway          | Uber
Pivotal           | Spring
PMD               |
SpotBugs          |
                  | SonarSource
                  | Square

# Contact info

Email the group at <well-defined-annotations@googlegroups.com>.
