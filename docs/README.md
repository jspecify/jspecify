

## how to build

To build this document, run the following commands:

```sh
$ cd path/to/jspecify
$ docker build -t jspecify-sphinx docs
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx make html
```

To [translate the document into several languages](https://www.sphinx-doc.org/en/master/intl.html), generate `.po` file by following commands:

```sh
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx make gettext
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx sphinx-intl update -p _build/gettext -l ja
```

After you finish updating the `.po` files, run the following command to generate HTML files:

```sh
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx make -e SPHINXOPTS="-D language='ja'" html
```
