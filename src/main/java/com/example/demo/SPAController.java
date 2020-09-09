package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;


@Controller
@Profile("production")
@Slf4j
public class SPAController {
    private final String[] index = readIndex();

    private String[] readIndex() {
        try(InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
            Document doc = Jsoup.parse(inputStream, "UTF-8", "");
            doc.outputSettings().prettyPrint(false);

            // Inject <base href='...'> placeholder
            Element base = doc.selectFirst("base[href]");
            if (base == null) {
                base = new Element("base");
                doc.head().prependChild(base);
            }
            base.attr("href", "@basehref@");

            return StringUtils.split(doc.html(),"@basehref@");
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load index.html", e);
        }
    }

    @GetMapping(value = {"/", "/{x:[\\w-]+}", "/{x:^(?!api$).*$}/**/{y:[\\w-]+}"}, produces = "text/html")
    public ResponseEntity<String> serveIndex(HttpServletRequest request, HttpServletResponse response) {
        String servletPath = request.getServletPath();
        String result;

        // Compute base href
        if (servletPath == null || "/".equals(servletPath) || servletPath.isEmpty()) {
            result = String.join(".", index);
        } else {
            if (servletPath.startsWith("/")) {
                servletPath = servletPath.substring(1);
            }
            String[] segments = StringUtils.delimitedListToStringArray(servletPath, "/");
            String baseHref = "." + "/..".repeat(segments.length-1);
            result = String.join(baseHref, index);
        }

        return ResponseEntity
                .ok()
                .header("Cache-Control", "max-age=60, must-revalidate, no-transform")
                .body(result);
    }
}
