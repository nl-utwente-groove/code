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

## Windows installer decoration

Two bitmaps replace WiX's decoration of the `.msi` pages
(`../wix/launch-simulator.wxf`), in the sizes WiX prescribes:

- `groove-dialog.bmp` (493x312 pixels, for 370x234 dialog units), the
  background of the first and last pages. The dialogs' controls start at
  130 units (173px), and everything right of the separator at 164px is the
  system dialog colour `#F0F0F0`, so the checkboxes, which Windows Installer
  cannot draw transparently, do not stand out as gray blocks. The panel left
  of the separator carries the G on a light green `#DDEEDC` (about 15% of the
  G's average green `#3D9E37` on white).
- `groove-banner.bmp` (493x58 pixels), the top banner of the pages in
  between (progress, files in use, maintenance): the same green, with the G
  at the right where WiX has its icon. The banner's title and description
  are drawn over it in black.

To regenerate:

```python
from PIL import Image, ImageDraw
TINT, GRAY, LINE = (0xDD, 0xEE, 0xDC), (240, 240, 240), (160, 160, 160)
g = Image.open('release/include/groove-G.ico').convert('RGBA')
W, H, PANEL, SIZE = 493, 312, 164, 124
dlg = Image.new('RGB', (W, H), GRAY)
dlg.paste(TINT, (0, 0, PANEL, H))
ImageDraw.Draw(dlg).line((PANEL, 0, PANEL, H - 1), fill=LINE)
icon = g.resize((SIZE, SIZE), Image.LANCZOS)
dlg.paste(icon, ((PANEL - SIZE) // 2, 36), icon)
dlg.save('release/jpackage/icons/groove-dialog.bmp')
BW, BH, BSIZE = 493, 58, 46
ban = Image.new('RGB', (BW, BH), TINT)
icon = g.resize((BSIZE, BSIZE), Image.LANCZOS)
ban.paste(icon, (BW - BSIZE - 12, (BH - BSIZE) // 2), icon)
ban.save('release/jpackage/icons/groove-banner.bmp')
```
