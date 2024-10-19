#!/bin/bash -e

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

pushd "${REPO_ROOT}" 2>&1 > /dev/null

echo "Building documentation..."
pip install -e . # required for python_training_project.version
mkdir -p build
cd docs
pipenv run make html

popd 2>&1 > /dev/null
