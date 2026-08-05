# Installer icons

Platform icons for `build-installer.sh`, both derived from the single
256x256 image in `../../include/groove-G.ico` (which remains the Windows
installer icon):

- `groove-G.png`: straight PNG export, used for the Linux packages
- `groove-G.icns`: macOS icon container with the 256px original plus
  downscaled 128/64/32/16px renditions (stored as the icns types ic07,
  ic08, ic11, ic12, ic13); no upscaled renditions, since 256px is the
  native resolution of the source

To regenerate (e.g. after the ico changes), use Python with Pillow:

```python
from PIL import Image
im = Image.open('release/include/groove-G.ico')
im.save('release/jpackage/icons/groove-G.png')
im.save('release/jpackage/icons/groove-G.icns',
        append_images=[im.resize((s, s), Image.LANCZOS) for s in (128, 64, 32, 16)])
```

Note that Pillow's icns writer adds upscaled 512/1024px renditions; strip
them by rewriting the container with only the chunk types listed above
(see the icns format: 8-byte `icns` header, then per-icon chunks of
4-byte type + 4-byte big-endian length).
