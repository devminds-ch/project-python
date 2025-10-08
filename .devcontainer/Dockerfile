# https://github.com/devcontainers/images/tree/main/src/python
#FROM mcr.microsoft.com/devcontainers/python:1-3.13-bookworm
FROM python:3.14-slim-bookworm

# https://code.visualstudio.com/remote/advancedcontainers/add-nonroot-user
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

# Create the user
RUN groupadd --gid $USER_GID $USERNAME \
    && useradd --uid $USER_UID --gid $USER_GID -m $USERNAME --shell /bin/bash \
    # [Optional] Add sudo support. Omit if you don't need to install software after connecting.
    && apt-get update \
    && apt-get install -y sudo \
    && echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
    && chmod 0440 /etc/sudoers.d/$USERNAME

# Install additional packages.
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends \
    git \
    make \
    openssh-client \
    && apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/*

# Install the project into `/app`
WORKDIR /app

# Enable bytecode compilation
ENV UV_COMPILE_BYTECODE=1 \
    UV_LINK_MODE=copy

# Install UV / UVX
COPY --from=ghcr.io/astral-sh/uv:latest /uv /uvx /bin/

# Install project dependencies
COPY pyproject.toml uv.lock ./
RUN uv sync --frozen --no-install-project --all-groups \
    && rm pyproject.toml uv.lock \
    && chown -R $USERNAME:$USERNAME /app

# Place executables in the environment at the front of the path
ENV PATH="/app/.venv/bin:$PATH"

# HACK: https://github.com/sphinx-doc/sphinx/issues/11739
ENV LC_ALL=C.UTF-8

# [Optional] Set the default user. Omit if you want to keep the default as root.
USER $USERNAME
