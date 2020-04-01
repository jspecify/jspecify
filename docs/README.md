

## how to build

To build this document, run the following commands:

```sh
$ cd path/to/jspecify
$ docker build -t jspecify-sphinx docs
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx make -e SPHINXOPTS="-D language='en'" html
```

To translate the document into several 

```sh
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx make gettext
$ docker run --rm -v $(pwd)/docs:/docs jspecify-sphinx sphinx-intl update -p _build/gettext -l ja
```
