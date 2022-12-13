---
sidebar_position: 1
---

# Start Here

## What is this?

[A group of organizations](/about) are working together to define a common set
of annotation types for use in JVM languages, to improve static analysis and
language interoperation. We've been focusing on nullness analysis first.

This includes providing both an artifact of annotation types your code can
depend on, and *precise* specifications of their semantics.

* **Why standardize the annotations?** Because you deserve better than this
  tragic [stackoverflow answer] depicts.
* **Why standardize the semantics?** Because you deserve a single source of
  truth for how your code should be annotated, instead of having to decide
  which tool to make happy at the expense of the others.
* **Why do both together?** Because you deserve annotation types with
  documentation on them, right there, that can answer your questions
  authoritatively.

JSpecify is developed by consensus of major stakeholders in Java static
analysis.  Our eventual 1.0 release will be the first tool-neutral,
library-neutral artifact for these annotations  (Note: `javax.annotation` was an
aborted attempt at this that was never actually released.)

Learn more about the JSpecify group and its goals in the [JSpecify FAQ].

## How do I learn about your nullness support?

As you read the following and have questions, check the [Nullness Design FAQ],
then [send us a mail](mailto:jspecify-discuss@googlegroups.com)

### Begin with one of

* The [Javadoc], which is pretty thorough and specific (but is a reference page,
  not a tutorial walkthrough)
* The current [User Guide] draft, which is a better choice for reading
  top-to-bottom, but is a little bit outdated at the moment

### Then if you're *really* interested

* The [specification], written to be understood by owners of compilers and
  static analysis tools, and currently matches JSpecify 0.2, not 0.3
* Our [wiki] has a number of informal, non-normative articles on varous topics
* [Open issues]
* Or try it out...

### Trying it out?

* You might wish to be an early adopter of **version 0.3** in your projects;
  see the [adoption](https://github.com/jspecify/jspecify/wiki/adoption) wiki
  page for advice.
* When our **reference implementation** is ready (targeting 2023 Q1), you'll be
  able to validate your usages of the annotations against our defined semantics.
  This is when you will really get to find out how helpful or annoying our
  current design choices are for you, and we'll be glad to hear about it.  This
  checker is not quite ready for prime time yet, however. It can be found, but
  we're not linking to it yet.
* **Version 1.0** will commit **permanently** to the annotation APIs, and they
  will be safe to depend on. From that point, no incompatible changes from a
  compilation perspective will happen, and any further specification adjustments
  will not break null-checking for you or your clients except to improve rare
  corner cases. We have no prediction for when this release will be. We hope to
  get as much of your feedback as we can before then.

## How can I get involved?

Great question.

It's not too late for your input to matter. It's true we've been working on this
for a long time (since 2018), but it's just been laying groundwork. None of it
is "set in stone" (except, as of 0.3, the package name).

* Join our new [Google Group]. Introduce yourself! Ask questions or just tell
  us what you're hoping to see.
* Do you use any libraries or tools that you think should use/support JSpecify
  annotations? Please tell them about us!
* Give some thought to what factors would make your own projects more or less
  likely to adopt, and let us know.
* [File an issue] to request a feature or if something is wrong.
* Don't forget to Star and Watch our [github repo]!

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
