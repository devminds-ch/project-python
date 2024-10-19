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

* [uv](https://docs.astral.sh/uv/) for dependencies and virtual environment
* [Sphinx](https://www.sphinx-doc.org/en/master/) for documentation
* [Flake8](https://flake8.pycqa.org/en/latest/) for style guide enforcement
* [mypy](https://mypy-lang.org/) for static type checking
* [Pylint](https://www.pylint.org/) for static code analysis
* [pytest](https://docs.pytest.org/en/stable/) for test creation and execution


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

All steps required to build, test or deploy the Python package are wrapped in separate shell scripts.

Check the content of the corresponding scripts for details.

### Build documentation

Build the Sphinx documentation:

```bash
./tools/build-docs.sh
```

### Build Python package

Build the Python package:

```bash
./tools/build-package.sh
```

### Run Python linters

Run static code analysis:

```bash
./tools/lint-package.sh
```

### Run Python tests

Run Python tests:

```bash
./tools/test-package.sh
```

### Deploy Python package

Make sure the set the following environment variables before calling the deployment script:

* `UV_PUBLISH_URL`
* `UV_PUBLISH_USERNAME`
* `UV_PUBLISH_PASSWORD`

Deploy the Python package:

```bash
./tools/deploy-package.sh
```
