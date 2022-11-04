# JSpecify

**Note: This project is still under active development. Any and all aspects of
the project are subject to change prior to the 1.0 release. Do not depend on it
for production use.**

An artifact of well-specified annotations to power static analysis checks and
JVM language interop.  Developed by consensus of the partner organizations
listed at our main web site, [jspecify.org](http://jspecify.org).

Our current focus is on annotations for nullness analysis.

## How to build

Run `./gradlew` to build artifacts, or `./gradlew publishToMavenLocal` to
install artifacts to your Local Maven Repository.

(Temporary?) After updating `.java` files, use

`./gradlew javadoc && rsync -a --delete build/docs/javadoc/ docs/static/docs/api`

to update the static hosted generated files.
