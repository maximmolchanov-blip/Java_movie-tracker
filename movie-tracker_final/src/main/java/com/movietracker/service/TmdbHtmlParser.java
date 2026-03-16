package com.movietracker.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TmdbHtmlParser {

    private static final String TMDB_MOVIE_URL = "https://www.themoviedb.org/movie/";
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    /**
     * Парсить HTML-сторінку фільму на TMDB і повертає слоган.
     */
    public String fetchTagline(Long tmdbId) {
        if (tmdbId == null) return null;

        try {
            String url = TMDB_MOVIE_URL + tmdbId;
            log.info("Parsing HTML from: {}", url);

            Document doc = Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(5000)
                    .get();

            // Шукаємо слоган — <h3 class="tagline">
            Element taglineEl = doc.selectFirst("h3.tagline");
            if (taglineEl != null && !taglineEl.text().isBlank()) {
                log.info("Found tagline for tmdbId={}: {}", tmdbId, taglineEl.text());
                return taglineEl.text();
            }

            log.info("No tagline found for tmdbId={}", tmdbId);
            return null;

        } catch (Exception e) {
            log.warn("Failed to parse HTML for tmdbId={}: {}", tmdbId, e.getMessage());
            return null;
        }
    }
}
