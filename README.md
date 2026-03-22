# MovieMagic — Movie Discovery App (Android)

An Android application for discovering popular and highly-rated films, optimised for both phones and tablets, featuring a browsable movie grid, detail views, trailer playback, user reviews, and offline favourites.

---

## Features

- **Movie Discovery Grid** — Scrollable grid of movie posters sorted by popularity or rating, fetched from TheMovieDB API
- **Detail View** — Full movie details including synopsis, release year, rating, runtime, and poster artwork
- **Trailer Playback** — Launches YouTube trailers directly from the detail screen
- **User Reviews** — Displays community reviews from TheMovieDB
- **Favourites** — Users can save films offline to a local SQLite database for access without internet
- **Tablet Optimised** — Two-pane master-detail layout on large screens using Fragments

---

## Technical Highlights

| Component | Implementation |
|---|---|
| Movie data | TheMovieDB REST API |
| Local persistence | SQLite via ContentProvider |
| UI | RecyclerView, Master-Detail Fragment pattern |
| Image loading | Picasso |
| Trailer playback | YouTube Intent / WebView |
| Architecture | Loader pattern, CursorAdapter |

---

## Setup

```bash
git clone https://github.com/chetnapriyadarshini/MovieMagic.git
```

1. Create a free account at [themoviedb.org](https://www.themoviedb.org) and obtain an API key
2. Add the key to `gradle.properties` as `MyMovieDbApiKey="your_key_here"`
3. Build and run in Android Studio

---

## Contact

Created by [@chetnapriyadarshini](https://github.com/chetnapriyadarshini)
