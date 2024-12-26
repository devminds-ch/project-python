#!/bin/bash -e

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

pushd "${REPO_ROOT}" 2>&1 > /dev/null

mkdir -p build

STATUS=0
set +e

echo "Running flake8..."
uv run --all-extras flake8 src/python_training_project --format=pylint > build/flake8.txt
if [ $? -ne 0 ]; then
    STATUS=1
fi

echo "Running mypy..."
uv run --all-extras mypy src/python_training_project > build/mypy.txt
if [ $? -ne 0 ]; then
    STATUS=1
fi

echo "Running pylint..."
uv run --all-extras pylint src/python_training_project --msg-template="{path}:{line}: [{msg_id}, {obj}] {msg} ({symbol})" > build/pylint.txt
if [ $? -ne 0 ]; then
    STATUS=1
fi

echo "Running ruff check..."
uv run --all-extras ruff check > build/ruff.txt
if [ $? -ne 0 ]; then
    STATUS=1
fi

echo "Running ruff format..."
uv run --all-extras ruff format --check
if [ $? -ne 0 ]; then
    STATUS=1
fi

set -e
popd 2>&1 > /dev/null

exit $STATUS
