# hexdoc-hexbound

Python web book docgen and [hexdoc](https://pypi.org/project/hexdoc) plugin for Hexbound.

## Version scheme

We use [hatch-gradle-version](https://pypi.org/project/hatch-gradle-version) to generate the version number based on whichever mod version the docgen was built with.

The version is in this format: `mod-version.python-version.mod-pre.python-dev.python-post`

For example:
* Mod version: `0.11.1-7`
* Python package version: `1.0.dev0`
* Full version: `0.11.1.1.0rc7.dev0`

## Setup

Install Python 3.11.

```sh
python3.11 -m venv venv

.\venv\Scripts\activate   # Windows
. venv/bin/activate.fish  # fish
source venv/bin/activate  # everything else

# run from the repo root, not doc/
pip install -e .[dev] --find-links ./doc/libs
```

## Usage

For local testing, create a file called `.env` in the repo root following this template:
```sh
GITHUB_REPOSITORY=object-Object/hexbound
GITHUB_SHA=1.19
GITHUB_PAGES_URL=https://hexbound.hexxy.media/
```

Useful commands:
```sh
# show help
hexdoc -h

# render and serve the web book in watch mode
nodemon --config doc/nodemon.json

# render and serve the web book
hexdoc serve

# export, render, and merge the web book
hexdoc export
hexdoc render
hexdoc merge

# start the Python interpreter with some extra local variables
hexdoc repl
```