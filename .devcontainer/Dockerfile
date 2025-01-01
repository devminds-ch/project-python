# https://github.com/devcontainers/images/tree/main/src/python
FROM mcr.microsoft.com/devcontainers/python:1-3.13-bookworm

# Install additional packages.
# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#    && apt-get -y install --no-install-recommends \
#       <your-package-list-here> \
#    && apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/*

# Install the project into `/app`
WORKDIR /app

# Enable bytecode compilation
ENV UV_COMPILE_BYTECODE=1

# Install UV / UVX
COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

# Install project dependencies
RUN --mount=type=bind,source=pyproject.toml,target=pyproject.toml \
    --mount=type=bind,source=uv.lock,target=uv.lock \
    uv sync --frozen --no-install-project --all-groups

# Place executables in the environment at the front of the path
ENV PATH="/app/.venv/bin:$PATH"
