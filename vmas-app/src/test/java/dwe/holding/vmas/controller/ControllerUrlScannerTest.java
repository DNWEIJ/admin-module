package dwe.holding.vmas.controller;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ControllerUrlScannerTest {

    private static final Set<String> MAPPING_ANNOTATIONS = Set.of(
            "RequestMapping", "GetMapping", "PostMapping", "PutMapping", "DeleteMapping", "PatchMapping"
    );


    @Test
    public void generateHtml() throws IOException {
        Path startDir = Paths.get("C:/workspace/admin");

        String insertStringFunction = "INSERT INTO new_vmas.admin_function (added_by,added_on,last_edited_by, last_edited_on, version, name) \n VALUES('system','%s','system','%s',0,'%s');";
        String insertStringFunctionRole = "INSERT INTO new_vmas.admin_function_role (added_by, added_on, last_edited_by, last_edited_on, version, member_id, function_id, role_id)" +
                "\n VALUES('system','%s','system','%s',0,77,%d,1);";

        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


        List<Path> controllers = findControllers(startDir);
        assertFalse(controllers.isEmpty(), "No controllers found");

        StringBuilder html = new StringBuilder();
        StringBuilder sqlInsertFunctionRole = new StringBuilder();
        StringBuilder sqlInsertFunction = new StringBuilder();

        html.append("""
                <html>
                <head>
                <title>URL Paths</title>
                <script>
                function sortTable(col) {
                    const table = document.getElementById("apiTable");
                    const rows = Array.from(table.rows).slice(1);
                    const asc = table.getAttribute("data-sort") !== "asc";
                
                    rows.sort((a, b) => {
                        let A = a.cells[col].innerText.toLowerCase();
                        let B = b.cells[col].innerText.toLowerCase();
                
                        if (col === 2) {
                            const order = ["get","post","put","delete","patch","all"];
                            return asc
                                ? order.indexOf(A) - order.indexOf(B)
                                : order.indexOf(B) - order.indexOf(A);
                        }
                
                        return asc ? A.localeCompare(B) : B.localeCompare(A);
                    });
                
                    table.setAttribute("data-sort", asc ? "asc" : "desc");
                    rows.forEach(r => table.appendChild(r));
                }
                </script>
                </head>
                <body>
                <table border='1' id='apiTable'>
                <tr>
                <th onclick="sortTable(0)">Controller</th>
                <th onclick="sortTable(1)">Method Name</th>
                <th onclick="sortTable(2)">HTTP</th>
                <th onclick="sortTable(3)">URL</th>
                </tr>
                """);

        Integer counter = 1;
        // for testing 1 controller
        // controllers = controllers.stream().filter(p -> p.getFileName().toString().equals("PetController.java")).toList();
        for (Path file : controllers) {

            String controllerName = file.getFileName().toString().replace(".java", "");
            List<String> lines = Files.readAllLines(file);

            String classPrefix = "";
            List<String> urls = new ArrayList<>();
            String httpMethod = "";

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                while (line.endsWith(",")) {
                    line += " " + lines.get(++i).trim();
                }


                // detect class-level RequestMapping
                if (line.startsWith("@RequestMapping") && classPrefix.isEmpty()) {
                    classPrefix = extractUrlFromAnnotation(line).getFirst();
                    continue;
                }

                // detect mapping annotation like @GetMapping({"a","b"})
                for (String ann : MAPPING_ANNOTATIONS) {
                    if (line.startsWith("@" + ann)) {
                        urls = extractUrlFromAnnotation(line);
                        httpMethod = ann.equals("RequestMapping") ? extractHttpFromRequestMapping(line) : ann.replace("Mapping", "").toUpperCase();
                        break;
                    }
                }

                // detect method declaration
                if (!urls.isEmpty() && line.matches(".*\\s+\\w+\\s*\\(.*\\)\\s*\\{?")) {
                    String methodName = line.replaceAll(".*\\s+(\\w+)\\s*\\(.*", "$1");

                    String fullUrl;
                    for (String url : urls) {
                        fullUrl = concat(classPrefix, url);
                        html.append("<tr><td>")
                                .append(controllerName)
                                .append("</td><td>")
                                .append(methodName)
                                .append("</td><td>")
                                .append(httpMethod)
                                .append("</td><td>")
                                .append(fullUrl)
                                .append("</td></tr>\n");
                        sqlInsertFunctionRole.append(String.format(insertStringFunctionRole, currentDateTime, currentDateTime, counter++)).append("\n");
                        sqlInsertFunction.append(String.format(insertStringFunction, currentDateTime, currentDateTime, httpMethod.toLowerCase() + "_" + fullUrl.toLowerCase())).append("\n");

                    }
                    urls = new ArrayList<>();
                    httpMethod = "";
                }
            }
        }

        html.append("</table>" + "<span>" + sqlInsertFunctionRole + "</span></body></html>");

        Path out = Paths.get("urlpaths.html");
        Files.writeString(out, html.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Path sqloutFunction = Paths.get("C:\\workspace\\admin\\vmas-app\\src\\dbchanges\\loadingDataFromVMAS\\sql\\add_function.sql");
        Files.writeString(sqloutFunction, sqlInsertFunction.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Path sqloutFunctionRole = Paths.get("C:\\workspace\\admin\\vmas-app\\src\\dbchanges\\loadingDataFromVMAS\\sql\\initial_add_functionrole.sql");
        Files.writeString(sqloutFunctionRole, sqlInsertFunctionRole.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Written: " + out.toAbsolutePath());
    }

    private List<Path> findControllers(Path root) throws IOException {
        List<Path> list = new ArrayList<>();
        Files.walk(root)
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".java"))
                .filter(p -> p.getFileName().toString().contains("Controller"))
                .forEach(list::add);
        return list;
    }

    private List<String> extractUrlFromAnnotation(String annLine) {
        List<String> urls = new ArrayList<>();
        int index = 0;

        while (index < annLine.length()) {
            int start = annLine.indexOf("\"", index);
            if (start == -1) break;
            int end = annLine.indexOf("\"", start + 1);
            if (end == -1) break;
            urls.add(annLine.substring(start + 1, end));
            index = end + 1;
        }
        return urls;
    }

    private String extractHttpFromRequestMapping(String line) {
        if (line.contains("RequestMethod.")) {
            int idx = line.indexOf("RequestMethod.");
            int end = line.indexOf("}", idx);
            if (end < 0) end = line.length();
            return line.substring(idx + "RequestMethod.".length(), end).replaceAll("[^A-Z]", "");
        }
        return "ALL";
    }

    private String concat(String a, String b) {
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;
        if (a.endsWith("/") && b.startsWith("/")) return a + b.substring(1);
        if (!a.endsWith("/") && !b.startsWith("/")) return a + "/" + b;
        return a + b;
    }
}