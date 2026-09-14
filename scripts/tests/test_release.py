"""Regression tests for the Python release-staging script."""

import subprocess
import sys
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
SCRIPT = ROOT / "scripts/prepare_release.py"


class ReleaseScriptTest(unittest.TestCase):
    """Check the release script's basic command-line validation."""

    def test_invalid_version(self):
        """Reject versions that do not use the X.Y.Z format."""
        r = subprocess.run(
            [sys.executable, str(SCRIPT), "1.0"],
            cwd=ROOT,
            text=True,
            capture_output=True,
        )
        self.assertNotEqual(r.returncode, 0)
        self.assertIn("Version must be X.Y.Z", r.stderr)

    def test_help(self):
        """Expose command-line help successfully."""
        r = subprocess.run(
            [sys.executable, str(SCRIPT), "--help"], text=True, capture_output=True
        )
        self.assertEqual(r.returncode, 0)


if __name__ == "__main__":
    unittest.main()
