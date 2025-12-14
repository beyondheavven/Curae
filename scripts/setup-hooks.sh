#!/usr/bin/env bash
set -e
git config core.hooksPath .githooks
chmod +x .githooks/*
echo "Hooks have been installed!"
