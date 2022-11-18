---
title: Start Here
---

# Start here

## What is this?

[A group of organizations](about) are working together to define a common set of
annotation types for use in Java static analysis, beginning with nullness
analysis.

This includes providing both a JAR file your code can depend on, and *precise*
specifications of these annotations' semantics.

* **Why standardize the annotations?** This tragic 
  [stackoverflow answer](https://stackoverflow.com/questions/4963300/which-notnull-java-annotation-should-i-use)
  says it all.
* **Why standardize the semantics?** Because a library owner, or a team that
  uses multiple tools, deserves a single source of truth telling them how to
  annotate their code, instead of having to decide which tool to make happy.
* **Why do both together?** Because users deserve annotation types with
  Javadoc *right there* that answers their questions authoritatively.

Our release will be the *first* tool-neutral, library-neutral artifact for
these annotations; the first to be developed by *consensus* of the major
stakeholders. (Note: `javax.annotation` was an aborted attempt that never
actually released, whose draft work was jammed into Maven anyway.)

Learn more about the JSpecify group and its goals in the [JSpecify
FAQ](http://github.com/jspecify/jspecify/wiki/jspecify-faq).

## How do I learn about your nullness support?

As you read, if you ever think "that's weird, why did they do that?", have a
look at the [Nullness Design
FAQ](https://github.com/jspecify/jspecify/wiki/nullness-design-FAQ); your
question might be answered.

### Start with one of

* The [Javadoc](/docs/api/org/jspecify/annotations/package-summary.html),
  which is reasonably thorough and specific, though not an organized walkthrough
* The current [User Guide draft](/docs/user-guide), which is a better choice
  for reading top-to-bottom, but is currently a bit outdated and in need of
  improvement.  It will change quite a bit before 1.0.

### Then if you're *really* interested

* The [specification](/docs/spec), written for owners of static analyzers, and
  currently outdated
* Various informal and non-normative articles on our
  [wiki](https://github.com/jspecify/jspecify/wiki)
* [Open issues](https://github.com/jspecify/jspecify/issues), although these are
  sometimes terrifying
* Or try it out

## How do I try it out?

* For now, we'd suggest holding tight. The previous section will keep you busy!
* Once we release version **0.3** (goal: end of 2022):
   * You can start using the annotations, IF you accept the **very real risk of
     future changes**. Be extremely cautious about using them in a released
     *library*.
   * However, your tools won't support the correct semantics yet; all you can do
     is add our package name onto the long list of annotations it recognizes,
     and it will treat them the same as the rest, until it releases fuller
     support for our specifications.
* When our **reference implementation** is ready, you'll be able to validate
  your usages of the annotations against our defined semantics. This is when you
  will really get to find out how helpful or annoying our current design choices
  are for you, and we'll be glad to hear about it. This checker is not quite
  ready for prime time yet, however.
* **1.0** will commit **permanently** to the annotation APIs, and they will be
  safe to depend on. From that point, further specification changes will not
  break null-checking for you or your clients except to improve very rare
  corner cases. We have no prediction for when this release will be. We hope to
  get as much of your feedback as we can before then.

## How can I get involved?

Great question.

It is **not too late** for your input to matter. It's true we've been working on
this a long time (since 2018), but it's all been groundwork, and none of it is
absolutely set in stone except our package and artifact names.

* Join our new [Google Group](https://groups.google.com/g/jspecify-discuss).
  Introduce yourself! Ask questions or tell us what you're hoping for.
* Do you use any libraries or tools that you think should use/support JSpecify
  annotations? Please tell them about us!
* Give some thought to what factors would make your own projects more or less
  likely to adopt, and let us know.
* Don't forget to Star and Watch our [github repo](https://github.com/jspecify/jspecify)!

(TODO: code of conduct link.)
