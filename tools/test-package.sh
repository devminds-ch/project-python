#!/bin/bash -e

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

pushd "${REPO_ROOT}" 2>&1 > /dev/null

mkdir -p build

echo "Running tests..."
uv run --all-extras pytest

popd 2>&1 > /dev/null
