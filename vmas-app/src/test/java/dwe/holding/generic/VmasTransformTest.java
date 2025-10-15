package dwe.holding.generic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

class VmasTransformTest {
    @Test
    void test() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

        Resource[] resources = resolver.getResources("classpath:/input/*.*");
        BufferedWriter writer = null;
        for (Resource resource : resources) {
            Document doc = Jsoup.parse(resource.getFile());

            process(doc);

            Path newFilePath = Paths.get("C:\\workspace\\admin\\vmas-app\\src\\main\\resources\\output"+resource.getFilename());
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(newFilePath, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                 PrintWriter printWriter = new PrintWriter(bufferedWriter))
            {
                printWriter.println(doc);
            }
        }
    }

    private static void process(Document doc) {
        doc.select("td").remove();
        doc.select("head").remove();
        doc.select("head").remove();
    }
}