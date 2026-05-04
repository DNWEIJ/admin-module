package dwe.holding.customer.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class ChipNummerClient {

    private final HttpClient httpClient;
    private final MessageSource messageSource;

    public ChipNummerClient(MessageSource messageSource) {
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        this.httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        this.messageSource = messageSource;
    }


    public String search(String chipNumber, Locale locale) throws Exception {
        HttpRequest getChipNummerPage = HttpRequest.newBuilder().uri(URI.create("https://chipnummer.nl/")).GET().build();

        HttpResponse<String> getResponse = httpClient.send(getChipNummerPage, HttpResponse.BodyHandlers.ofString());

        Document doc = Jsoup.parse(getResponse.body());
        String csrfToken = doc.select("meta[name=csrf-token]").attr("content");

        String form =
                "authenticity_token=" + URLEncoder.encode(csrfToken, StandardCharsets.UTF_8)
                        + "&mark%5Bnumber%5D=" + URLEncoder.encode(chipNumber, StandardCharsets.UTF_8)
                        + "&commit=Zoeken";

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://chipnummer.nl/"))
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
                .header("Accept", "text/vnd.turbo-stream.html, text/html, application/xhtml+xml")
                .header("User-Agent", "Mozilla/5.0")
                .header("Origin", "https://chipnummer.nl")
                .header("Referer", "https://chipnummer.nl/")
                .header("X-CSRF-Token", csrfToken)
                .POST(HttpRequest.BodyPublishers.ofString(form))
                .build();

        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        return processReturnedHtml(postResponse.body(), locale);
    }

    String processReturnedHtml(String body, Locale locale) {
        Document doc = Jsoup.parse(body);

        Elements all = doc.select("div.d-grid.gap-2 a");

        List<String> found = new ArrayList<>();
        List<String> notFound = new ArrayList<>();

        for (Element a : all) {
            String text = a.text();
            String href = a.attr("href");

            String formatted;
            if (!href.isBlank()) {
                formatted = "<a href=\"" + href + "\" target=\"_blank\">" + text + "</a>";
            } else {
                formatted = text;
            }
            if (a.hasClass("btn-success")) {
                found.add(formatted);
            } else {
                notFound.add(formatted);
            }
        }
        return "<p style='margin-left:5rem'><b>" + messageSource.getMessage("label.search.result", null, locale) + ": <b/><br/><br/>" +
                String.join("<br/>", found) + (found.isEmpty() || notFound.isEmpty() ? "" : "<br/><br/>") + String.join("<br/>", notFound)
                + "</p>";
    }
}

