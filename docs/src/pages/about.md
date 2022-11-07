---
title: About
---

# JSpecify

**Tool-independent Annotations for Java Static Analysis**

## Our Goal

Java static analysis tools, such as Error Prone and those built into IDEs, have been very successful at enabling automated refactorings and bug prevention.
Many of their most beneficial analyses require certain Java annotations (such as ``@Nullable`` or ``@Immutable``) to be present in the source or class file.

Yet for the 15 years since Java annotations were introduced (five years for type-use annotations), users have never had a clear answer for which incarnation of these annotation classes they can safely adopt.
Many such artifacts have been created, but there has never been a serious and successful effort to provide a single well-specified standard with industry consensus.

The lack of this artifact has constrained the prevalence and overall value of static analysis technology throughout the industry.
It has encouraged today's tools to accept a wide variety of annotations, treating them equivalently, which renders the annotation specifications meaningless (an API owner cannot know whether applying a particular annotation is correct without knowing which tool its users will be using to interpret them).

Finally, it complicates interoperation between Java and other JVM languages, which sometimes expect additional API metadata (such as nullability) that the Java language can't express.

This missing artifact, with full specifications, is what we are working to create.

:::caution xkcd
We have seen the xkcd comic. Please do not send us the xkcd comic. We know about the xkcd comic.
:::

## Activity

The primary location for our day-to-day work is on GitHub: https://github.com/jspecify/jspecify

## Talks about JSpecify

* [JVMLS19 overview presentation](https://drive.google.com/file/d/15wZ-cVPkfsNYzSez9WrAF4gEjWNzlDAD/view>)

## Participants

People from the following projects and organizations are collaborating on this project:


| Project                                                 | Organization                                  |
|---------------------------------------------------------|-----------------------------------------------|
| [Android](https://www.android.com>)                     | [Google](https://google.com)                  |
| [EISOP](https://eisop.uwaterloo.ca)                     | [EISOP Team](https://github.com/eisop)        |
| [Error Prone](https://errorprone.info)                  | [Google](https://google.com)                  |
| [Guava](https://github.com/google/guava)                | [Google](https://google.com)                  |
| [Infer](https://www.fbinfer.com)                        | [Meta](https://about.facebook.com)            |
| [IntelliJ IDEA](https://www.jetbrains.com/idea/)        | [JetBrains](https://www.jetbrains.com/)       |
| [Kotlin](https://kotlinlang.org/)                       | [JetBrains](https://www.jetbrains.com/)       |
| [NullAway](https://github.com/uber/NullAway)            | [Uber](https://uber.com)                      |
| [OpenJDK](https://openjdk.java.net)                     | [Oracle](https://www.oracle.com)              |
| [PMD](https://pmd.github.io/)                           | [PMD Team](https://pmd.github.io/)            |
| [Spring](https://tanzu.vmware.com/spring-app-framework) | [VMware](https://www.vmware.com/)             |
| [SonarQube](https://www.sonarqube.org/)                 | [SonarSource](https://www.sonarsource.com/)   |
| [SpotBugs](http://spotbugs.rtfd.io/)                    | [SpotBugs Team](https://github.com/spotbugs/) |
| (various)                                               | [Square](https://squareup.com)                |


## Contact Info

Email the group at <jspecify-dev@googlegroups.com>.

