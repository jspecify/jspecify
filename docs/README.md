# Landing Pages

The docs will rebuild automatically after a commit. If you want to test locally,
read on:

## How to build

To build this document, run the following commands:

```sh
$ cd path/to/jspecify
$ docker build -t jspecify-sphinx docs
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx make html
```

To
[translate the document into several languages](https://www.sphinx-doc.org/en/master/intl.html),
generate `.po` files by following commands:

```sh
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx make gettext
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx sphinx-intl update -p _build/gettext -l ja
```

After you finish updating `.po` files, run the following command to generate
HTML files:

```sh
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx make -e SPHINXOPTS="-D language='ja'" html
```

## How to build without Docker

Under Debian and Ubuntu, you may be able to run:

```sh
$ sudo apt-get install python3-sphinx python3-sphinx-rtd-theme
$ ( cd docs && make html )
```

## CommonMark vs. reStructuredText

These docs use reStructuredText (hence the `.rst` extension). If you are used to
CommonMark (and similar flavors of Markdown, like
[GitHub's](https://github.github.com/gfm/)), you may want to use a converter
that rewrites CommonMark to reStructuredText:

```sh
pandoc --from=markdown --to=rst docs/foo.md --output=docs/foo.rst
```
