"""Verify provenance, integrity and executable PostgreSQL classpath without running the JAR."""
import hashlib
from pathlib import Path
import sys
import zipfile


def verify(directory, sha):
    directory = Path(directory)
    if (directory / "source-sha.txt").read_text().strip() != sha:
        raise ValueError("Artifact source SHA differs from this event")
    expected = (directory / "app.jar.sha256").read_text().split()[0]
    if hashlib.sha256((directory / "app.jar").read_bytes()).hexdigest() != expected:
        raise ValueError("Artifact checksum mismatch")
    with zipfile.ZipFile(directory / "app.jar") as jar:
        manifest = jar.read("META-INF/MANIFEST.MF").decode()
        if "Main-Class: org.springframework.boot.loader.launch.JarLauncher" not in manifest:
            raise ValueError("Artifact is not an executable Spring Boot JAR")
        if not any(name.startswith("BOOT-INF/lib/postgresql-") for name in jar.namelist()):
            raise ValueError("PostgreSQL driver missing from executable classpath")
    print("Artifact SHA, checksum, executable launcher and PostgreSQL driver verified")


if __name__ == "__main__":
    verify(sys.argv[1], sys.argv[2])
