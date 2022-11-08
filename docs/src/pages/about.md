---
title: About Us
---

## Who are we?

The following projects and organizations are collaborating on this project:

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

## What are we trying to do?

Java static analysis tools, such as Error Prone and those built into IDEs, have been very successful
at enabling automated refactorings and bug prevention.

Many of their most beneficial analyses require certain Java annotations (such as `@Nullable` or
`@Immutable`) to be present in the source or class file being analyzed. Yet for the 
going-on-two-decades since Java annotations were introduced, there's been no serious and 
successful effort to provide a single well-specified artifact for these annotations with 
industry consensus. Instead, each tool has generally provided its own, and each tool interprets 
the meanings of the annotations differently.

We think the lack of such an artifact has been one of the factors constraining the prevalance and 
overall value of static analysis technology throughout the industry. And it complicates 
interoperation between Java and other JVM languages, which sometimes expect additional API 
metadata (such as for nullness) that the Java language can't express.

So, here we are. That's what we're working on.

:::caution xkcd
We have seen the xkcd comic. Please do not send us the xkcd comic. We know about the xkcd comic.
:::

## Contact Info

Email the group at <jspecify-dev@googlegroups.com>.
