#!/usr/bin/env python3
"""Render a JDT external-annotation (.eea) file as annotated Java declarations.

Usage: python eea-render.py <file.eea> [--raw] [--all]
  --raw   also print the original (unannotated) declaration under each member
  --all   also list members that carry no annotated signature

Invoke it through the interpreter, not as a bare `eea-render.py`: on Windows the
.py file association opens (and instantly closes) a separate console window.

AI-generated: Claude Opus 5, 2026-08 (reviewed; see util.AIGenerated for the
convention on the Java side).
"""
import sys

PRIMS = {'B': 'byte', 'C': 'char', 'D': 'double', 'F': 'float', 'I': 'int',
         'J': 'long', 'S': 'short', 'Z': 'boolean', 'V': 'void'}
MARK = {'0': '@Nullable ', '1': '@NonNull '}


class Sig:
    """Recursive-descent parser for JVMS 4.7.9.1 signatures with JDT nullness marks."""

    def __init__(self, s):
        self.s = s
        self.i = 0

    def peek(self):
        return self.s[self.i] if self.i < len(self.s) else ''

    def take(self):
        c = self.s[self.i]
        self.i += 1
        return c

    def nullness(self):
        """A '0'/'1' marker may directly follow L, T or [."""
        if self.peek() in MARK:
            return MARK[self.take()]
        return ''

    def ident(self, stop):
        start = self.i
        while self.peek() and self.peek() not in stop:
            self.i += 1
        return self.s[start:self.i]

    def type(self):
        c = self.peek()
        if not c:
            raise ValueError(f'truncated signature {self.s!r}')
        if c in PRIMS:
            return PRIMS[self.take()]
        if c == '[':
            self.take()
            mark = self.nullness()
            return mark + self.type() + '[]'
        if c == 'T':
            self.take()
            mark = self.nullness()
            name = self.ident(';')
            self.take()  # ';'
            return mark + name
        if c == 'L':
            self.take()
            mark = self.nullness()
            name = self.ident('<;.')
            out = mark + name.split('/')[-1]
            if self.peek() == '<':
                out += self.args()
            while self.peek() == '.':  # inner class
                self.take()
                out += '.' + self.ident('<;.')
                if self.peek() == '<':
                    out += self.args()
            self.take()  # ';'
            return out
        raise ValueError(f'unexpected {c!r} at {self.i} in {self.s!r}')

    def args(self):
        self.take()  # '<'
        parts = []
        while self.peek() not in ('>', ''):
            if self.peek() == '*':
                self.take()
                parts.append(self.nullness() + '?')
            elif self.peek() in '+-':
                bound = 'extends' if self.take() == '+' else 'super'
                # a nullness mark may sit on the wildcard; it reads as the bound's
                mark = self.nullness()
                parts.append(f'? {bound} {mark}{self.type()}')
            else:
                parts.append(self.type())
        self.take()  # '>'
        return '<' + ','.join(parts) + '>'

    def typeparams(self):
        """<K:Ljava/lang/Object;V::Lsome/Iface;> -> ['K', 'V extends Iface']"""
        if self.peek() != '<':
            return []
        self.take()
        out = []
        while self.peek() not in ('>', ''):
            name = self.ident(':')
            bounds = []
            while self.peek() == ':':
                self.take()
                if self.peek() != ':' and self.peek() != '>':
                    bounds.append(self.type())
            bounds = [b for b in bounds if b != 'Object']
            out.append(name + (' extends ' + ' & '.join(bounds) if bounds else ''))
        self.take()  # '>'
        return out


def render(name, sig):
    """Render one member signature as a Java-ish declaration."""
    p = Sig(sig)
    tps = p.typeparams()
    prefix = ('<' + ', '.join(tps) + '> ') if tps else ''
    if p.peek() != '(':  # field
        return f'{prefix}{p.type()} {name}'
    p.take()
    params = []
    while p.peek() not in (')', ''):
        params.append(p.type())
    p.take()
    ret = p.type()
    throws = []
    while p.peek() == '^':
        p.take()
        throws.append(p.type())
    decl = f'{prefix}{ret} {name}({", ".join(params)})'
    if throws:
        decl += ' throws ' + ', '.join(throws)
    return decl


def parse(path):
    """-> (classname, class-typeparam-lines, [(super, sigs)], [(member, orig, ann|None)])

    Entries are a non-indented head line followed by one or two indented signature
    lines (original, then optionally the annotated variant). Head lines starting
    with 'class'/'super' describe the type itself; all others name a member.
    """
    with open(path, encoding='utf-8') as f:
        lines = [ln.rstrip('\n') for ln in f]
    entries = []
    i = 0
    while i < len(lines):
        ln = lines[i]
        i += 1
        if not ln.strip() or ln.startswith(' '):
            continue
        sigs = []
        while i < len(lines) and lines[i].startswith(' '):
            sigs.append(lines[i][1:])
            i += 1
        entries.append((ln.strip(), sigs))
    header, cls_tp, supers, members = '', [], [], []
    for head, sigs in entries:
        if head.startswith('class '):
            header, cls_tp = head.split()[-1], sigs
        elif head.startswith('super '):
            supers.append((head.split()[-1], sigs))
        else:
            members.append((head, sigs[0], sigs[1] if len(sigs) > 1 else None))
    return header, cls_tp, supers, members


def main():
    args = [a for a in sys.argv[1:] if not a.startswith('--')]
    flags = {a for a in sys.argv[1:] if a.startswith('--')}
    if len(args) != 1 or flags - {'--raw', '--all'}:
        sys.exit(__doc__.strip())
    try:
        header, cls_tp, supers, members = parse(args[0])
    except OSError as e:
        sys.exit(f'cannot read {args[0]}: {e.strerror}')
    tps = Sig(cls_tp[-1]).typeparams() if cls_tp else []
    simple = header.split('/')[-1]
    decl = simple + ('<' + ', '.join(tps) + '>' if tps else '')
    for name, sigs in supers:
        decl += ' extends ' + name.split('/')[-1] + Sig(sigs[-1]).args()
    annotated = [m for m in members if m[2]]
    print(f'// {header}   ({len(annotated)} of {len(members)} members annotated)')
    print(f'type {decl} {{')
    for name, orig, ann in members:
        if ann is None:
            if '--all' in flags:
                print(f'    {render(name, orig)};   // not annotated')
            continue
        print(f'    {render(name, ann)};')
        if '--raw' in flags:
            print(f'        // was: {render(name, orig)}')
    print('}')


if __name__ == '__main__':
    main()
