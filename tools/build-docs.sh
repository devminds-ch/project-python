#!/bin/bash -e

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

pushd "${REPO_ROOT}" 2>&1 > /dev/null

echo "Building documentation..."
mkdir -p build
cd docs
uv run --all-groups make html

popd 2>&1 > /dev/null
