---
sidebar_position: 1
---

# Start Here

## What is this?

[A group of organizations](about) are working together to define a common set of
annotation types for use in Java static analysis, beginning with nullness
analysis.

This includes providing both a JAR file your code can depend on, and *precise*
specifications of these annotations' semantics.

* **Why standardize the annotations?** This tragic [stackoverflow answer] says
  it all.
* **Why standardize the semantics?** Because a library owner, or a team that
  uses multiple tools, deserves a single source of truth for how to
  annotate their code, instead of having to decide which tool to make happy.
* **Why do both together?** Because users deserve annotation types with
  Javadoc *right there* that answers their questions authoritatively.

Our release will be the first tool-neutral, library-neutral artifact for these
annotations; it is being developed by consensus of major stakeholders in Java
static analysis.  (Note: `javax.annotation` was an aborted attempt at this that
was never actually released, whose draft work was jammed into Maven anyway.)

Learn more about the JSpecify group and its goals in the [JSpecify FAQ].

## How do I learn about your nullness support?

As you read the following, when you think "that's weird, why did they do
that?", have a look at the [Nullness Design FAQ] to see if your question has
been answered.

### Begin with one of

* The [Javadoc], which is reasonably thorough and specific, though not an
  organized walkthrough
* The current [User Guide] draft, which is a better choice for reading
  top-to-bottom, but is currently a bit outdated and in need of improvement.
  It will change quite a bit before 1.0.

### Then if you're *really* interested

* The [specification], written to be understood by owners of compilers and
  static analysis tools, and currently a bit outdated
* Various informal and non-normative articles on our [wiki].
* [Open issues], (although some contain extremely long discussions that might
  not be worth your time)
* Or try it out

### Trying it out?

* For now, we'd suggest holding tight. The previous section will keep you busy!
* Once we release **version 0.3** (goal: end of 2022):
   * You can start using the annotations, if you accept the **very real risk of
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
  ready for prime time yet, however (hence the absence of a link to it).
* **Version 1.0** will commit **permanently** to the annotation APIs, and they
  will be safe to depend on. From that point, further specification changes
  will not break null-checking for you or your clients except to improve very
  rare corner cases. We have no prediction for when this release will be. We
  hope to get as much of your feedback as we can before then.

## How can I get involved?

Great question.

It's not too late for your input to matter. It's true we've been working on
this for a long time (since 2018), but it's just been laying groundwork. None
of it is "set in stone" (except, soon, our package and artifact names).

* Join our new [Google Group]. Introduce yourself! Ask questions or just tell
  us what you're hoping to see.
* Do you use any libraries or tools that you think should use/support JSpecify
  annotations? Please tell them about us!
* Give some thought to what factors would make your own projects more or less
  likely to adopt, and let us know.
* [File an issue] to request a feature or if something is wrong.
* Don't forget to Star and Watch our [github repo]!

(TODO: code of conduct link.)

[file an issue]: https://github.com/jspecify/jspecify/issues/new
[github repo]: https://github.com/jspecify/jspecify
[google group]: https://groups.google.com/g/jspecify-discuss
[javadoc]: /docs/api/org/jspecify/annotations/package-summary.html
[jspecify faq]: http://github.com/jspecify/jspecify/wiki/jspecify-faq
[nullness design faq]: https://github.com/jspecify/jspecify/wiki/nullness-design-FAQ
[open issues]: https://github.com/jspecify/jspecify/issues
[specification]: /docs/spec
[stackoverflow answer]: https://stackoverflow.com/questions/4963300/which-notnull-java-annotation-should-i-use
[user guide]: /docs/user-guide
[wiki]: https://github.com/jspecify/jspecify/wiki

