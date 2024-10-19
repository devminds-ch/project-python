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
├── build   Reserved folder for build artifacts
├── dist    Reserved folder for build artifacts
├── docs    Sphinx documentation
├── src     Python package source code
├── tests   Python tests
└── tools   Scripts

```

## Build, test and deploy instructions

Create and activate the Python virtual environment:

```bash
pipenv sync --dev # setup virtual environment including development dependencies
pipenv shell      # activate virtual environment
```

Note: the Python virtual environment can be removed using `pipenv --rm`.

**IMPORTANT:** all of the following commands have to be executed within the Python virtual environment!

### Build documentation

Build the Sphinx documentation: `./tools/build-docs.sh`

```bash
pip install -e .  # package installation is required for Git version
cd docs
make html
```

### Build Python package

Build the Python package: `./tools/build-package.sh`

```bash
python -m build --wheel
```

### Run Python linters

Run static code analysis: `./tools/lint-package.sh`

```bash
mkdir -p build
flake8 src/python_training_project --format=pylint > build/flake8.txt
pylint src/python_training_project --msg-template="{path}:{line}: [{msg_id}, {obj}] {msg} ({symbol})" > build/pylint.txt
mypy src/python_training_project > build/mypy.txt
```

### Run Python tests

Run Python tests: `./tools/test-package.sh`

```bash
pip install -e .  # package installation is required for path resolution
mkdir -p build
pytest
```

### Deploy Python package

Deploy the Python package: `./tools/deploy-package.sh`

```bash
# Make sure to set the following environment variables
# TWINE_REPOSITORY_URL
# TWINE_USERNAME
# TWINE_PASSWORD
twine upload dist/*
```
