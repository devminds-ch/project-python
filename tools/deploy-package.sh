#!/bin/bash -e

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

pushd "${REPO_ROOT}" 2>&1 > /dev/null

echo "Deploying package..."
uv publish dist/*

popd 2>&1 > /dev/null
