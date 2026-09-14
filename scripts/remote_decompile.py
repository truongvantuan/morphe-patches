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
                        "# Download, decompile, and archive the result in the Kaggle workspace.\n",
                        "from pathlib import Path\n",
                        "from urllib.request import urlopen\n",
                        "import shutil\n",
                        "import subprocess\n",
                        "import zipfile\n",
                        "\n",
                        "apk_path = Path('/kaggle/working/input.apk')\n",
                        "with urlopen(APK_URL, timeout=120) as response, apk_path.open('wb') as output:\n",
                        "    shutil.copyfileobj(response, output)\n",
                        "decompiled_path = Path('/kaggle/working/decompiled')\n",
                        "subprocess.run(['jadx', '-d', str(decompiled_path), str(apk_path)], check=True)\n",
                        "archive_path = Path('/kaggle/working/jadx_decompiled.zip')\n",
                        "with zipfile.ZipFile(archive_path, 'w', zipfile.ZIP_DEFLATED) as archive:\n",
                        "    for path in decompiled_path.rglob('*'):\n",
                        "        if path.is_file():\n",
                        "            archive.write(path, path.relative_to(decompiled_path.parent))\n",
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
        deadline = time.monotonic() + int(
            os.environ.get("KAGGLE_TIMEOUT_SECONDS", "1800")
        )
        while time.monotonic() < deadline:
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
        else:
            raise SystemExit("❌ Kernel polling timed out")
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
