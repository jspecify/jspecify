---
title: Start Here
---

# Start here (to learn more)

## What is this?

[These organizations](about) are working together to define a common set of
annotation types for use in static analysis, beginning with nullness analysis.

This includes providing both the JAR file your code can depend on, and *precise*
specifications of these annotations' semantics.

* **Why standardize the annotations?** This tragic 
  [stackoverflow answer](https://stackoverflow.com/questions/4963300/which-notnull-java-annotation-should-i-use)
  says it all.
* **Why standardize the semantics?** Because a library owner, or a team that
  uses multiple tools, deserves a single source of truth telling them how to
  annotate their code. No more "ToolA wants it this way, but ToolB wants it that
  way."
* **Why do both together?** Because users deserve annotation types with thorough
  Javadoc that can authoritatively answer their questions.

Are we adding Yet Another set of annotations onto the long list? In one sense,
sure. But our release will be the *first* tool-neutral, library-neutral
artifact in this list; the first to be developed by consensus of the major
stakeholders. (`javax.annotation` was an aborted attempt that never actually
released, whose draft work was jammed into Maven anyway.)

**That's it?** Our vision includes much more than just providing nullness
annotations. But this first project turned out to be Hard (who knew?), so we've
been focusing tightly on it. We've posted [sketchy
notes](http://github.com/jspecify/jspecify/wiki/long-term-roadmap) on what else
we want to do, and will fill that out more when we can.

Learn more about the JSpecify group and its goals in the [JSpecify
FAQ](http://github.com/jspecify/jspecify/wiki/jspecify-faq).

## How do I learn about your nullness support?

### Start with one of

* The [Javadoc](https://jspecify.dev/docs/api/org/jspecify/annotations/package-summary.html),
  which is reasonably thorough and specific, though not an organized walkthrough
* The current [User Guide draft](user-guide), which is better for reading
  top-to-bottom, but is currently a bit outdated and in need of improvement.
  It will likely change a lot before 1.0.

### If you're *really* interested

* The [specification](/docs/spec), written for analyzer owners, and currently outdated
* Various articles on our [wiki](https://github.com/jspecify/jspecify/wiki), an
  unorganized batch of random thoughts on a range of topics
* [Open issues](https://github.com/jspecify/jspecify/issues), although these are
  sometimes terrifying
* Try it out

### As you read...

* Each time you think "that's weird, why did they do that?" -- and you will --
  have a look at the
  [Nullness Design FAQ](https://github.com/jspecify/jspecify/wiki/nullness-design-FAQ).
  If it didn't answer your question, [file an issue](https://github.com/jspecify/jspecify/issues/new)
  or email [jspecify-discuss](mailto:jspecify-discuss@googlegroups.com).
* Keep notes so you can send us feedback!

## How do I try it out?

* For now, we'd suggest holding tight. The previous section will keep you quite
  busy!
* Once we release version **0.3** (goal: end of 2022):
   * You can start using the annotations, IF you accept the **very real risk of
     future changes**. Be extremely cautious about using them in a released
     *library* (ask our advice, please).
   * However, your tools won't support the correct semantics yet; all you can do
     is add our package name onto the long list of annotations it recognizes,
     and it will treat them the same as anything else. 
* When our **reference implementation** is ready, you'll be able to validate
  your usages of the annotations against our defined semantics. This is when you
  will really get to find out how helpful or annoying our current design choices
  are for you, and we'll be glad to hear about it.
* **1.0** will commit **permanently** to the annotation APIs, and they will be
  safe to depend on. From that point, further specification changes will not
  break null-checking for you or your clients except to improve minor corner
  cases.  We have no prediction for when this release will be. We hope to get as
  much of your feedback as we can before then.

## How do I get more involved?

Great question.

It is **not too late** for your input to matter. It's true we've been working on
this a long time (since 2018), but it's all been groundwork, and none of it is
absolutely set in stone except our package and artifact names.

* Join our new [Google Group](https://groups.google.com/g/jspecify-discuss).
  Introduce yourself! Ask questions or tell us what you're hoping for.
* Do you use any libraries or tools that you think should use/support JSpecify
  annotations? Please tell them about us!
* Give some thought to what factors would make your projects more or less likely
  to adopt, and let us know.
* Of course, "star" and "watch" our [github project](https://github.com/jspecify/jspecify), etc.
* Do you like writing documentation? Start with an email or issue about it and
  let's discuss.

