#!/usr/bin/env python3
"""Run the Kaggle remote jadx decompiler notebook."""

import argparse
import json
import os
import subprocess
import tempfile
import time
from pathlib import Path


def main():
    p = argparse.ArgumentParser()
    p.add_argument("url")
    p.add_argument("output", nargs="?", default="./output")
    a = p.parse_args()
    if not a.url.startswith(("http://", "https://")):
        p.error("First argument must be a URL (https://...)")
    kernel = os.environ.get("KAGGLE_KERNEL_ID")
    token = os.environ.get("KAGGLE_API_TOKEN")
    if not kernel or not token:
        p.error("Set KAGGLE_KERNEL_ID and KAGGLE_API_TOKEN")
    with tempfile.TemporaryDirectory() as td:
        notebook = {
            "nbformat": 4,
            "nbformat_minor": 4,
            "metadata": {
                "kernelspec": {
                    "display_name": "Python 3",
                    "language": "python",
                    "name": "python3",
                }
            },
            "cells": [
                {
                    "cell_type": "code",
                    "metadata": {},
                    "source": [f"APK_URL = {a.url!r}\n", "print(APK_URL)\n"],
                    "outputs": [],
                    "execution_count": None,
                },
                {
                    "cell_type": "code",
                    "metadata": {},
                    "source": [
                        "!pip -q install jadx\n",
                        "# Download and decompile according to the Kaggle runtime image.\n",
                    ],
                    "outputs": [],
                    "execution_count": None,
                },
            ],
        }
        Path(td, "jadx-decompiler.ipynb").write_text(json.dumps(notebook))
        Path(td, "kernel-metadata.json").write_text(
            json.dumps(
                {
                    "id": kernel,
                    "title": "jadx-apk-decompiler",
                    "code_file": "jadx-decompiler.ipynb",
                    "language": "python",
                    "kernel_type": "notebook",
                    "is_private": True,
                    "enable_gpu": False,
                    "enable_internet": True,
                }
            )
        )
        subprocess.run(["kaggle", "kernels", "push", "-p", td], check=True)
        print("⏳ Waiting for kernel...")
        while True:
            raw = subprocess.run(
                ["kaggle", "kernels", "status", kernel],
                text=True,
                stdout=subprocess.PIPE,
                stderr=subprocess.STDOUT,
            ).stdout
            print(raw.strip())
            if "complete" in raw.lower():
                break
            if any(x in raw.lower() for x in ("error", "cancel", "fail")):
                raise SystemExit("❌ Kernel failed")
            time.sleep(10)
        subprocess.run(
            [
                "kaggle",
                "kernels",
                "output",
                kernel,
                "-p",
                a.output,
                "--file-pattern",
                r".*\.zip$",
            ],
            check=True,
        )


if __name__ == "__main__":
    main()
