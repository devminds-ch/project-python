# Python Training Project by [devminds GmbH](https://devminds.ch)

This Python project is used for DevOps CI/CD trainings.

The project contains a Python package providing a CLI to calculate the sum of two numbers:

```bash
Usage: python_training_project [OPTIONS] COMMAND [ARGS]...

Options:
  --version  Show the version and exit.
  --help     Show this message and exit.

Commands:
  sum
```


## Toolchain

The Python package is based on the following toolchain:

* [Pipenv](https://pipenv.pypa.io/en/latest/) for dependencies and virtual environment
* [Sphinx](https://www.sphinx-doc.org/en/master/) for documentation
* [Flake8](https://flake8.pycqa.org/en/latest/) for style guide enforcement
* [mypy](https://mypy-lang.org/) for static type checking
* [Pylint](https://www.pylint.org/) for static code analysis
* [pytest](https://docs.pytest.org/en/stable/) for test creation and execution
* [Twine](https://twine.readthedocs.io/en/stable/) for package deployment


## Directory structure

```
├── docs    Sphinx documentation
├── src     Python package source code
└── tests   Python tests
```

## Build, test and deploy instructions

Create and activate the Python virtual environment:

```bash
pipenv sync --dev # setup virtual environment including development dependencies
pipenv shell      # activate virtual environment
```

Note: the Python virtual environment can be removed using `pipenv --rm`.

**IMPORTANT:** all of the following commands have to be executed within the Python virtual environment!

Build the Sphinx documentation:

```bash
pip install -e .  # package installation is required for Git version
cd docs
make html
```

Build the Python package:

```bash
python -m build --wheel
```

Execute static code analysis:

```bash
flake8 src/python_training_project
pylint src/python_training_project
mypy src/python_training_project
```

Execute Python tests:

```bash
pip install -e .  # package installation is required for path resolution
pytest
```

Deploy the Python package:

```bash
twine upload --repository-url REPOSITORY_URL --username USERNAME --password PASSWORD dist/*
```
