**These sample inputs are an experiment.**

They use annotations whose names and meanings are not finalized.

They have not been code reviewed.

We do not know if this is even the format that we want our sample inputs to be
in.

Whatever our final samples look like, we do **not** expect to present them as
"conformance tests" that require any behavior from tools: The goal of our
project is to provide one source of nullness information. Tools may use some,
all, or none of that information. They may also use information from other
sources. Based on the information they have available for any given piece of
code, tools always have the option to issue a warning / error / other diagnostic
for that code, and they always have the option not to.

The hope is that samples like these may be useful to those who wish to discuss
the spec or implement tools. To that end, we welcome comments on both the format
of these files and their content.
