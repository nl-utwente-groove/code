# Download statistics from GitHub releases

Design note, 2026-09-13. Nothing implemented yet.

## What SourceForge gives and GitHub does not

SourceForge keeps a per-file download log and exposes it as a time series: totals per
day/week/month, split by country and by operating system, per file or per directory, on
the project's stats page and as JSON
(`https://sourceforge.net/projects/groove/files/stats/json?start_date=…&end_date=…`, and
the same under `files/<path>/stats/json` for a single file or directory). The whole
history since 2007 is queryable at any time.

GitHub keeps one number: the cumulative `download_count` of each release asset, exposed by
the releases API (`GET /repos/nl-utwente-groove/code/releases`, unauthenticated access
suffices). There is no history, no per-period breakdown, no country or OS. The counter
counts every GET of the asset, including CI runs, mirrors and vulnerability scanners: the
two test releases of 2026-09-13 stood at 2–7 downloads per asset within hours, with at
most one or two human downloads among them. The traffic API (`traffic/clones`,
`traffic/views`) is about the repository, not the assets, keeps 14 days and needs push
access; it is not a substitute.

State of the release history on 2026-09-13: 28 releases on GitHub since `release-6_8_0`
(2024-03), each with a `bin` and a `bin+doc` zip; from the 99.x test releases on, also four
platform installers, the yFiles add-on zip and the READ-ME text asset. Counts per asset
range from 2 to 56; the whole GitHub history is a few hundred downloads. SourceForge
still records 10–120 downloads per month in 2025–2026 (mostly of old versions), so the
SourceForge page stays the larger source for some time.

So "similar to SourceForge" is achievable for the time dimension only: sample the counters
and difference them. Country and OS are lost; the platform installers give an OS proxy
for the part of the traffic that uses them.

## Design: snapshot and difference

**Collector.** A scheduled GitHub Actions workflow (daily cron plus `workflow_dispatch`)
reads the releases API and appends one row per asset to an append-only CSV
`snapshots.csv` with columns `date,tag,asset,count`, then commits the file with the
workflow's `GITHUB_TOKEN`. The whole collector is one `gh api` line:

```
gh api repos/nl-utwente-groove/code/releases --paginate \
  --jq --arg d "$(date -u +%F)" \
  '.[] | .tag_name as $t | .assets[] | [$d, $t, .name, .download_count] | @csv' \
  >> snapshots.csv
```

Size: at present ~70 rows a day, ~1 MB a year uncompressed, growing linearly with the
number of assets; no problem for decades. A row per asset per day, rather than a row per
asset per change, keeps the derivation trivial and makes a missed day visible as a gap.
Run the cron at an odd minute (e.g. `17 3 * * *`); GitHub delays and drops jobs
scheduled on the hour.

**Derivation** (at render time, not stored): the downloads of an asset on a day are the
difference between its counts on consecutive snapshot days. A negative difference means
the asset was re-uploaded (the counter restarts at zero); count the new value as the
increment. An asset that disappears from the API (a deleted test release) simply stops.
From this: totals per release, per asset kind (`bin`, `bin+doc`, per-platform
installer, yFiles add-on), per month and overall, i.e. the SourceForge "downloads over
time" and "by file" views. Pre-releases (versions 99.x, see `release/README.md`) are
excluded from the presentation, not from the data.

**Presentation.** A page on the website (`nl-utwente-groove.github.io`, Jekyll) with one
chart (monthly downloads, stacked by asset kind, Chart.js from cdnjs) and one table
(totals per release), built by a small script that fetches `snapshots.csv` from
`raw.githubusercontent.com` (CORS-enabled) and derives the series in the browser. The
collector also regenerates a Markdown table of totals per release in its own `README.md`,
so the numbers are readable without JavaScript and without the website.

**SourceForge baseline.** A one-off import of the SourceForge monthly totals since 2007
(project-wide, and per release directory where the file layout allows) into
`sourceforge-monthly.csv`, so the chart runs continuously across the move to GitHub.
The SourceForge stats page stays linked for the country breakdown. The import can be
repeated later as long as the SourceForge project exists; the script belongs next to the
collector.

## Where it lives

Three options for the data and the collector:

1. **A branch of `code`.** No new repository, but daily data commits in the code
   repository's history and a workflow unrelated to the code.
2. **The website repository.** Data next to the page, but every daily commit triggers a
   Jekyll rebuild and shows up in the website history.
3. **A new repository `nl-utwente-groove/download-stats`** holding `snapshots.csv`,
   `sourceforge-monthly.csv`, the two scripts and the workflow; the website page only
   reads from it. One concern per repository; the data history is the data's own.

Option 3 is the recommendation. The workflow needs `contents: write` on that repository
and nothing on `code` (the releases of a public repository are readable anonymously; the
token is passed anyway to stay clear of the unauthenticated rate limit).

Risk to verify at setup: GitHub disables scheduled workflows in repositories without
activity for 60 days. The collector's own daily commits should count as activity; if
they turn out not to, the fallback is a `workflow_dispatch` trigger fired from a cron in
`code`, or a monthly manual run.

## Not done here

- No per-download detail: no country, no OS beyond the installer platform, no referrer.
- No filtering of bot traffic; the numbers are documented as raw counter differences.
- Day resolution only, as the cron determines. Finer resolution would need a finer cron
  and buys nothing at the current volume.

## Effort

Repository, workflow and collector: an hour. SourceForge import script: an hour.
Website page with chart and table: an afternoon, mostly fitting the Jekyll theme.
