# https://github.com/devcontainers/images/tree/main/src/python
FROM mcr.microsoft.com/devcontainers/python:1-3.12-bookworm

# Install additional packages.
# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#    && apt-get -y install --no-install-recommends \
#       <your-package-list-here> \
#    && apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/*

# Install pipenv dependencies
COPY Pipfile Pipfile.lock /tmp/
RUN cd /tmp && pipenv install --system --dev
