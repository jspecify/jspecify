# JSpecify

**Note: This project is still under active development. Any and all aspects of
the project are subject to change prior to the 1.0 release. Do not depend on it
for production use.**

An artifact of well-specified annotations to power static analysis checks and
JVM language interop.  Developed by consensus of the partner organizations
listed at our main web site, [jspecify.org](http://jspecify.org).

Our current focus is on annotations for nullness analysis.

## Stuff to read

The main page of the [wiki](/jspecify/jspecify/wiki/) lists a number of jumping-off points.

## Status 2022-11

We're striving to get our 0.3 release out before the holidays. Goals for this release:

* Get the final package name/location (we *can* still change it later but we
  sure hope very much not to)
* Represent *some specific decision* on every design question we're aware of --
  though we know we'll have to reverse some of those decisions in future releases.
* Include javadoc consistent with those decisions.
* Get the reference implementation reasonably caught-up with the specs so that
  you can actually try things out. (You might be able to find it, but for now we aren't
  linking to it.)
  
Upon the release we'll advertise the project a bit more widely than we have been
so far. We're working to make sure it's worth your time to learn about.

## How to build

Run `./gradlew` to build artifacts, or `./gradlew publishToMavenLocal` to
install artifacts to your Local Maven Repository.

(Temporary?) After updating `.java` files, use

`./gradlew javadoc && rsync -a --delete build/docs/javadoc/ docs/static/docs/api`

to update the static hosted generated files.
