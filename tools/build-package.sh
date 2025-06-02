#!/bin/bash -e

# Get Git root directory
REPO_ROOT=$(git rev-parse --show-toplevel)

# Force the uv cache directory to be located in the repository root
# to avoid permission issues within CI/CD environments.
export UV_CACHE_DIR="${REPO_ROOT}/.uv_cache"

pushd "${REPO_ROOT}" 2>&1 > /dev/null

echo "Building package..."
uv build

popd 2>&1 > /dev/null
