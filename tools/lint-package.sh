#!/bin/bash -e

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

pushd "${REPO_ROOT}" 2>&1 > /dev/null

mkdir -p build

STATUS=0
set +e

echo "Running flake8..."
flake8 src/python_training_project --format=pylint > build/flake8.log
if [ $? -ne 0 ]; then
    STATUS=1
fi

echo "Running pylint..."
pylint src/python_training_project --msg-template="{path}:{line}: [{msg_id}, {obj}] {msg} ({symbol})" > build/pylint.log
if [ $? -ne 0 ]; then
    STATUS=1
fi

echo "Running mypy..."
mypy src/python_training_project > build/mypy.log
if [ $? -ne 0 ]; then
    STATUS=1
fi

set -e
popd 2>&1 > /dev/null

exit $STATUS
