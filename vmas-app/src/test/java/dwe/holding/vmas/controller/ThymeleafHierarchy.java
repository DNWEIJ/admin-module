package dwe.holding.vmas.controller;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ThymeleafHierarchy {

    enum NodeType { LAYOUT, PAGE, FRAGMENT, UNKNOWN }

    static class TemplateNode {
        String filePath;
        String fileName;
        NodeType type;
        List<String> definesFragments = new ArrayList<>();
        List<Reference> references    = new ArrayList<>();
        List<String> extendsLayout    = new ArrayList<>();

        TemplateNode(String filePath, String fileName) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.type     = NodeType.UNKNOWN;
        }
    }

    static class Reference {
        String kind;
        String target;
        String resolvedFile;
        Reference(String kind, String target) { this.kind = kind; this.target = target; }
    }

    static final Pattern P_FRAGMENT_DEF     = Pattern.compile("th:fragment\\s*=\\s*[\"']([^\"'(]+)");
    static final Pattern P_INSERT_REPLACE    = Pattern.compile("th:(?:insert|replace|include)\\s*=\\s*[\"']~?\\{?([^}\"']+)");
    static final Pattern P_LAYOUT_DECORATE   = Pattern.compile("layout:decorate\\s*=\\s*[\"']~?\\{?([^}\"']+)");
    static final Pattern P_LAYOUT_FRAGMENT   = Pattern.compile("layout:fragment\\s*=\\s*[\"']([^\"']+)");

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java ThymeleafHierarchy <project-root-path> [output-file]");
            System.exit(1);
        }
        String rootPath = args[0];
        String outPath  = args.length > 1 ? args[1] : "thymeleaf-hierarchy.html";
        Path root = Paths.get(rootPath);
        if (!Files.exists(root)) { System.err.println("Path not found: " + rootPath); System.exit(1); }

        System.out.println("Scanning: " + root.toAbsolutePath());
        Map<String, TemplateNode> nodes = scanTemplates(root);
        System.out.println("Found " + nodes.size() + " HTML files");
        classifyNodes(nodes);
        Files.writeString(Paths.get(outPath), generateHtml(nodes, root.toAbsolutePath().toString()));
        System.out.println("Output written to: " + Paths.get(outPath).toAbsolutePath());
    }

    static Map<String, TemplateNode> scanTemplates(Path root) throws IOException {
        Map<String, TemplateNode> nodes = new LinkedHashMap<>();
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String filePath = file.toString().replace('\\', '/');
                if (filePath.endsWith(".html") && !filePath.contains("/target/")) {
                    String rel  = root.relativize(file).toString().replace('\\', '/');
                    TemplateNode node = new TemplateNode(rel, file.getFileName().toString());
                    parseFile(file, node);
                    nodes.put(rel, node);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return nodes;
    }

    static void parseFile(Path file, TemplateNode node) throws IOException {
        String content = Files.readString(file);
        Matcher mf = P_FRAGMENT_DEF.matcher(content);
        while (mf.find()) node.definesFragments.add(mf.group(1).trim());
        Matcher mlf = P_LAYOUT_FRAGMENT.matcher(content);
        while (mlf.find()) node.definesFragments.add("[layout] " + mlf.group(1).trim());
        Matcher mi = P_INSERT_REPLACE.matcher(content);
        while (mi.find()) {
            String raw = mi.group(1).trim();
            String kw  = content.substring(mi.start(), mi.start() + 25).replaceAll("th:(insert|replace|include).*", "$1");
            Reference ref = new Reference(kw, raw);
            ref.resolvedFile = resolveTarget(raw);
            node.references.add(ref);
        }
        Matcher md = P_LAYOUT_DECORATE.matcher(content);
        while (md.find()) {
            String raw = md.group(1).trim();
            node.extendsLayout.add(raw);
            Reference ref = new Reference("layout:decorate", raw);
            ref.resolvedFile = resolveTarget(raw);
            node.references.add(ref);
        }
    }

    static String resolveTarget(String raw) {
        String t = raw.contains("::") ? raw.substring(0, raw.indexOf("::")).trim() : raw.trim();
        t = t.replaceAll("^~\\{", "").replaceAll("}$", "").trim();
        if (t.isEmpty()) return null;
        int slash = t.lastIndexOf('/');
        String base = slash >= 0 ? t.substring(slash + 1) : t;
        return base.endsWith(".html") ? base : base + ".html";
    }

    static void classifyNodes(Map<String, TemplateNode> nodes) {
        for (TemplateNode n : nodes.values()) {
            boolean isLayout   = n.filePath.contains("/layout") || n.filePath.contains("/layouts")
                    || n.fileName.contains("layout") || n.fileName.contains("Layout")
                    || n.definesFragments.stream().anyMatch(f -> f.contains("[layout]"));
            boolean decorates  = !n.extendsLayout.isEmpty();
            boolean hasRef     = n.references.stream().anyMatch(r -> r.kind.equals("insert") || r.kind.equals("replace") || r.kind.equals("include"));
            boolean defsOnly   = !n.definesFragments.isEmpty() && n.references.isEmpty();

            if (isLayout)         n.type = NodeType.LAYOUT;
            else if (decorates)   n.type = NodeType.PAGE;
            else if (defsOnly)    n.type = NodeType.FRAGMENT;
            else if (hasRef)      n.type = NodeType.PAGE;
            else if (!n.definesFragments.isEmpty()) n.type = NodeType.FRAGMENT;
            else                  n.type = NodeType.PAGE;
        }
    }

    static String generateHtml(Map<String, TemplateNode> nodes, String rootAbsPath) {
        List<TemplateNode> layouts   = nodes.values().stream().filter(n -> n.type == NodeType.LAYOUT).collect(Collectors.toList());
        List<TemplateNode> pages     = nodes.values().stream().filter(n -> n.type == NodeType.PAGE).collect(Collectors.toList());
        List<TemplateNode> fragments = nodes.values().stream().filter(n -> n.type == NodeType.FRAGMENT).collect(Collectors.toList());
        List<TemplateNode> unknown   = nodes.values().stream().filter(n -> n.type == NodeType.UNKNOWN).collect(Collectors.toList());

        Map<String, List<String>> usedBy = new HashMap<>();
        for (TemplateNode n : nodes.values())
            for (Reference r : n.references)
                if (r.resolvedFile != null)
                    usedBy.computeIfAbsent(r.resolvedFile, k -> new ArrayList<>()).add(n.filePath);

        StringBuilder sb = new StringBuilder();
        appendHtmlStart(sb, rootAbsPath, nodes.size());

        renderSection(sb, "Layouts",   layouts,   "layout",   "pip-layout",   usedBy);
        renderSection(sb, "Pages",     pages,     "page",     "pip-page",     usedBy);
        renderSection(sb, "Fragments", fragments, "fragment", "pip-fragment", usedBy);
        if (!unknown.isEmpty())
            renderSection(sb, "Unknown", unknown, "unknown", "pip-unknown", usedBy);

        appendHtmlEnd(sb);
        return sb.toString();
    }

    static void appendHtmlStart(StringBuilder sb, String rootAbsPath, int total) {
        sb.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        sb.append("<meta charset=\"UTF-8\">\n");
        sb.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        sb.append("<title>Thymeleaf Template Hierarchy</title>\n");
        sb.append("<style>\n");
        sb.append("@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;600&family=Syne:wght@400;700;800&display=swap');\n");
        sb.append("*,*::before,*::after{box-sizing:border-box;margin:0;padding:0}\n");
        sb.append(":root{\n");
        sb.append("  --bg:#0d0f14;--bg2:#13161e;--bg3:#1a1e28;--border:#252a38;\n");
        sb.append("  --text:#c8d0e0;--muted:#5a6480;--accent:#5b8eff;\n");
        sb.append("  --layout:#ff6b6b;--page:#4ecdc4;--fragment:#ffe66d;--unknown:#888;\n");
        sb.append("  --radius:8px;\n}\n");
        sb.append("html{height:100%}\n");
        sb.append("body{font-family:'Syne',sans-serif;background:var(--bg);color:var(--text);min-height:100%;overflow-y:scroll}\n");

        // header
        sb.append("header{padding:16px 28px;border-bottom:1px solid var(--border);background:var(--bg2);");
        sb.append("position:sticky;top:0;z-index:100;display:flex;align-items:center;gap:18px;flex-wrap:wrap}\n");
        sb.append("header h1{font-size:1.25rem;font-weight:800;letter-spacing:-.03em;color:#fff;white-space:nowrap}\n");
        sb.append("header h1 span{color:var(--accent)}\n");
        sb.append(".scan-path{font-family:'JetBrains Mono',monospace;font-size:.66rem;color:var(--muted);flex:1;min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}\n");
        sb.append(".legend{display:flex;gap:12px;flex-wrap:wrap}\n");
        sb.append(".legend-item{display:flex;align-items:center;gap:5px;font-size:.72rem;font-weight:600;white-space:nowrap}\n");
        sb.append(".dot{width:8px;height:8px;border-radius:50%;flex-shrink:0}\n");
        sb.append(".dot-layout{background:var(--layout)}.dot-page{background:var(--page)}.dot-fragment{background:var(--fragment)}.dot-unknown{background:var(--unknown)}\n");

        // toolbar
        sb.append(".toolbar{padding:10px 28px;background:var(--bg2);border-bottom:1px solid var(--border);");
        sb.append("position:sticky;top:53px;z-index:99;display:flex;gap:8px;align-items:center;flex-wrap:wrap}\n");
        sb.append(".search-wrap{position:relative;flex:1;min-width:160px;max-width:320px}\n");
        sb.append(".search-wrap input{width:100%;background:var(--bg3);border:1px solid var(--border);border-radius:var(--radius);");
        sb.append("padding:6px 10px 6px 30px;color:var(--text);font-family:'JetBrains Mono',monospace;font-size:.76rem;outline:none;transition:border-color .2s}\n");
        sb.append(".search-wrap input:focus{border-color:var(--accent)}\n");
        sb.append(".search-wrap svg{position:absolute;left:8px;top:50%;transform:translateY(-50%);opacity:.4;pointer-events:none}\n");
        sb.append(".filter-btn{padding:5px 12px;border-radius:var(--radius);border:1px solid var(--border);background:var(--bg3);");
        sb.append("color:var(--muted);font-family:'Syne',sans-serif;font-size:.72rem;font-weight:700;cursor:pointer;transition:all .2s;white-space:nowrap}\n");
        sb.append(".filter-btn.active{border-color:var(--accent);color:var(--accent);background:rgba(91,142,255,.1)}\n");
        sb.append(".filter-btn:hover:not(.active){border-color:var(--muted);color:var(--text)}\n");
        sb.append(".btn-expand{border-color:rgba(78,205,196,.4);color:var(--page)}\n");
        sb.append(".stats{margin-left:auto;font-size:.7rem;color:var(--muted);font-family:'JetBrains Mono',monospace}\n");

        // main
        sb.append(".main{padding:20px 28px 64px;max-width:1100px;margin:0 auto}\n");

        // section heading
        sb.append(".section-heading{display:flex;align-items:center;gap:10px;margin:28px 0 10px;");
        sb.append("font-size:.66rem;font-weight:700;text-transform:uppercase;letter-spacing:.14em;color:var(--muted)}\n");
        sb.append(".section-heading:first-child{margin-top:6px}\n");
        sb.append(".section-heading::after{content:'';flex:1;height:1px;background:var(--border)}\n");
        sb.append(".section-count{background:var(--bg3);border:1px solid var(--border);border-radius:20px;padding:1px 8px;font-size:.62rem}\n");

        // cards list
        sb.append(".cards-list{display:flex;flex-direction:column;gap:4px}\n");

        // details / summary card
        sb.append("details.template-card{background:var(--bg2);border:1px solid var(--border);border-radius:var(--radius);overflow:hidden;transition:border-color .15s}\n");
        sb.append("details.template-card:hover{border-color:#3a4160}\n");
        sb.append("details.template-card[open]{border-color:#3a4160}\n");
        sb.append("details.template-card[open] summary{border-bottom:1px solid var(--border)}\n");
        sb.append("details.template-card.hidden{display:none}\n");
        sb.append("details.template-card summary{list-style:none;cursor:pointer;display:flex;align-items:center;gap:9px;");
        sb.append("padding:9px 13px;background:var(--bg3);user-select:none}\n");
        sb.append("details.template-card summary::-webkit-details-marker{display:none}\n");

        // pip
        sb.append(".type-pip{width:8px;height:8px;border-radius:50%;flex-shrink:0}\n");
        sb.append(".pip-layout{background:var(--layout);box-shadow:0 0 6px var(--layout)}\n");
        sb.append(".pip-page{background:var(--page);box-shadow:0 0 6px var(--page)}\n");
        sb.append(".pip-fragment{background:var(--fragment);box-shadow:0 0 5px var(--fragment)}\n");
        sb.append(".pip-unknown{background:var(--unknown)}\n");

        sb.append(".card-name{font-weight:700;font-size:.84rem;color:#fff;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;max-width:300px}\n");
        sb.append(".card-path{font-family:'JetBrains Mono',monospace;font-size:.62rem;color:var(--muted);flex:1;min-width:0;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}\n");
        sb.append(".card-chevron{margin-left:auto;color:var(--muted);font-size:.65rem;transition:transform .2s;flex-shrink:0}\n");
        sb.append("details[open] .card-chevron{transform:rotate(90deg)}\n");

        // card body
        sb.append(".card-body{padding:10px 13px 12px;display:flex;flex-direction:column;gap:8px}\n");
        sb.append(".detail-group{display:flex;flex-direction:column;gap:3px}\n");
        sb.append(".detail-label{font-size:.56rem;font-weight:700;text-transform:uppercase;letter-spacing:.1em;color:var(--muted);margin-bottom:1px}\n");
        sb.append(".detail-row{font-family:'JetBrains Mono',monospace;font-size:.67rem;display:flex;gap:7px;align-items:flex-start;");
        sb.append("padding:4px 8px;background:var(--bg3);border:1px solid var(--border);border-radius:4px;word-break:break-all}\n");
        sb.append(".tag{flex-shrink:0;font-size:.54rem;padding:1px 5px;border-radius:3px;font-weight:700;white-space:nowrap;margin-top:1px}\n");
        sb.append(".tag-insert{background:rgba(91,142,255,.15);color:#7ba7ff;border:1px solid rgba(91,142,255,.3)}\n");
        sb.append(".tag-replace{background:rgba(78,205,196,.12);color:var(--page);border:1px solid rgba(78,205,196,.3)}\n");
        sb.append(".tag-include{background:rgba(255,230,109,.1);color:var(--fragment);border:1px solid rgba(255,230,109,.3)}\n");
        sb.append(".tag-decorate{background:rgba(255,107,107,.12);color:var(--layout);border:1px solid rgba(255,107,107,.3)}\n");
        sb.append(".tag-frag{background:rgba(255,230,109,.1);color:var(--fragment);border:1px solid rgba(255,230,109,.3)}\n");
        sb.append(".tag-usedby{background:rgba(91,142,255,.08);color:#8ab4ff;border:1px solid rgba(91,142,255,.2)}\n");
        sb.append(".val{color:var(--text);flex:1}\n");
        sb.append(".card-empty{font-size:.68rem;color:var(--muted);padding:3px 0;font-style:italic}\n");

        // scrollbar
        sb.append("::-webkit-scrollbar{width:6px}::-webkit-scrollbar-track{background:transparent}");
        sb.append("::-webkit-scrollbar-thumb{background:var(--border);border-radius:3px}");
        sb.append("::-webkit-scrollbar-thumb:hover{background:var(--muted)}\n");
        sb.append("</style>\n</head>\n<body>\n");

        // header
        sb.append("<header>\n");
        sb.append("  <h1>Thymeleaf <span>Hierarchy</span></h1>\n");
        sb.append("  <span class=\"scan-path\">").append(escHtml(rootAbsPath)).append("</span>\n");
        sb.append("  <div class=\"legend\">\n");
        sb.append("    <div class=\"legend-item\"><div class=\"dot dot-layout\"></div>Layout</div>\n");
        sb.append("    <div class=\"legend-item\"><div class=\"dot dot-page\"></div>Page</div>\n");
        sb.append("    <div class=\"legend-item\"><div class=\"dot dot-fragment\"></div>Fragment</div>\n");
        sb.append("    <div class=\"legend-item\"><div class=\"dot dot-unknown\"></div>Unknown</div>\n");
        sb.append("  </div>\n</header>\n");

        // toolbar
        sb.append("<div class=\"toolbar\">\n");
        sb.append("  <div class=\"search-wrap\">\n");
        sb.append("    <svg width=\"13\" height=\"13\" viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"2\"><circle cx=\"11\" cy=\"11\" r=\"8\"/><path d=\"m21 21-4.35-4.35\"/></svg>\n");
        sb.append("    <input type=\"text\" id=\"searchInput\" placeholder=\"Search templates...\" oninput=\"filterCards()\">\n");
        sb.append("  </div>\n");
        sb.append("  <button class=\"filter-btn active\" onclick=\"setFilter('all',this)\">All</button>\n");
        sb.append("  <button class=\"filter-btn\" onclick=\"setFilter('layout',this)\">Layouts</button>\n");
        sb.append("  <button class=\"filter-btn\" onclick=\"setFilter('page',this)\">Pages</button>\n");
        sb.append("  <button class=\"filter-btn\" onclick=\"setFilter('fragment',this)\">Fragments</button>\n");
        sb.append("  <button class=\"filter-btn btn-expand\" id=\"toggleAllBtn\" onclick=\"toggleAll()\">Expand All</button>\n");
        sb.append("  <span class=\"stats\" id=\"stats\">").append(total).append(" templates</span>\n");
        sb.append("</div>\n");

        sb.append("<div class=\"main\">\n");
    }

    static void appendHtmlEnd(StringBuilder sb) {
        sb.append("</div>\n"); // main
        sb.append("<script>\n");
        sb.append("let activeFilter='all',allExpanded=false;\n");

        sb.append("function toggleAll(){\n");
        sb.append("  allExpanded=!allExpanded;\n");
        sb.append("  document.querySelectorAll('details.template-card:not(.hidden)').forEach(d=>{\n");
        sb.append("    if(allExpanded) d.setAttribute('open',''); else d.removeAttribute('open');\n");
        sb.append("  });\n");
        sb.append("  document.getElementById('toggleAllBtn').textContent=allExpanded?'Collapse All':'Expand All';\n");
        sb.append("}\n");

        sb.append("function setFilter(f,btn){\n");
        sb.append("  activeFilter=f;\n");
        sb.append("  document.querySelectorAll('.filter-btn:not(.btn-expand)').forEach(b=>b.classList.remove('active'));\n");
        sb.append("  btn.classList.add('active');\n");
        sb.append("  filterCards();\n");
        sb.append("}\n");

        sb.append("function filterCards(){\n");
        sb.append("  const q=document.getElementById('searchInput').value.toLowerCase();\n");
        sb.append("  let visible=0;\n");
        sb.append("  document.querySelectorAll('details.template-card').forEach(card=>{\n");
        sb.append("    const match=(activeFilter==='all'||card.dataset.type===activeFilter)&&(!q||card.dataset.search.includes(q));\n");
        sb.append("    card.classList.toggle('hidden',!match);\n");
        sb.append("    if(match) visible++;\n");
        sb.append("  });\n");
        sb.append("  document.getElementById('stats').textContent=visible+' templates';\n");
        sb.append("  document.querySelectorAll('.section-heading').forEach(h=>{\n");
        sb.append("    const has=[...document.querySelectorAll('details.template-card[data-type=\"'+h.dataset.section+'\"]')].some(c=>!c.classList.contains('hidden'));\n");
        sb.append("    h.style.display=has?'':'none';\n");
        sb.append("    const list=h.nextElementSibling; if(list) list.style.display=has?'':'none';\n");
        sb.append("  });\n");
        sb.append("}\n");

        sb.append("</script>\n</body>\n</html>\n");
    }

    static void renderSection(StringBuilder sb, String title, List<TemplateNode> nodes,
                              String type, String pipClass, Map<String, List<String>> usedBy) {

        sb.append("<div class=\"section-heading\" data-section=\"").append(type).append("\">");
        sb.append(escHtml(title));
        sb.append("<span class=\"section-count\">").append(nodes.size()).append("</span></div>\n");
        sb.append("<div class=\"cards-list\">\n");

        for (TemplateNode n : nodes) {
            String searchText = (n.fileName + " " + n.filePath).toLowerCase();

            // <details> as the card — collapsed by default
            sb.append("<details class=\"template-card\" data-type=\"").append(type)
                    .append("\" data-search=\"").append(escAttr(searchText)).append("\">\n");

            // <summary> = the always-visible header row
            sb.append("  <summary>\n");
            sb.append("    <div class=\"type-pip ").append(pipClass).append("\"></div>\n");
            sb.append("    <span class=\"card-name\">").append(escHtml(n.fileName)).append("</span>\n");
            sb.append("    <span class=\"card-path\">").append(escHtml(n.filePath)).append("</span>\n");
            sb.append("    <span class=\"card-chevron\">&#9654;</span>\n");
            sb.append("  </summary>\n");

            // expanded body
            sb.append("  <div class=\"card-body\">\n");
            boolean hasContent = false;

            // extends layout
            if (!n.extendsLayout.isEmpty()) {
                hasContent = true;
                sb.append("    <div class=\"detail-group\">\n");
                sb.append("      <div class=\"detail-label\">Extends Layout</div>\n");
                for (String l : n.extendsLayout) {
                    sb.append("      <div class=\"detail-row\"><span class=\"tag tag-decorate\">layout</span>")
                            .append("<span class=\"val\">").append(escHtml(l)).append("</span></div>\n");
                }
                sb.append("    </div>\n");
            }

            // references (skip layout:decorate — shown above)
            List<Reference> refs = n.references.stream()
                    .filter(r -> !r.kind.equals("layout:decorate"))
                    .collect(Collectors.toList());
            if (!refs.isEmpty()) {
                hasContent = true;
                sb.append("    <div class=\"detail-group\">\n");
                sb.append("      <div class=\"detail-label\">References</div>\n");
                for (Reference r : refs) {
                    String tagCls = switch (r.kind) {
                        case "replace" -> "tag-replace";
                        case "include" -> "tag-include";
                        default        -> "tag-insert";
                    };
                    sb.append("      <div class=\"detail-row\"><span class=\"tag ").append(tagCls).append("\">")
                            .append(escHtml(r.kind)).append("</span><span class=\"val\">")
                            .append(escHtml(r.target)).append("</span></div>\n");
                }
                sb.append("    </div>\n");
            }

            // defines fragments
            if (!n.definesFragments.isEmpty()) {
                hasContent = true;
                sb.append("    <div class=\"detail-group\">\n");
                sb.append("      <div class=\"detail-label\">Defines Fragments</div>\n");
                for (String f : n.definesFragments) {
                    sb.append("      <div class=\"detail-row\"><span class=\"tag tag-frag\">fragment</span>")
                            .append("<span class=\"val\">").append(escHtml(f)).append("</span></div>\n");
                }
                sb.append("    </div>\n");
            }

            // used by
            List<String> ub = usedBy.getOrDefault(n.fileName, Collections.emptyList());
            if (!ub.isEmpty()) {
                hasContent = true;
                sb.append("    <div class=\"detail-group\">\n");
                sb.append("      <div class=\"detail-label\">Used By</div>\n");
                for (String u : ub) {
                    sb.append("      <div class=\"detail-row\"><span class=\"tag tag-usedby\">ref</span>")
                            .append("<span class=\"val\">").append(escHtml(u)).append("</span></div>\n");
                }
                sb.append("    </div>\n");
            }

            if (!hasContent) {
                sb.append("    <span class=\"card-empty\">No Thymeleaf references detected</span>\n");
            }

            sb.append("  </div>\n");  // card-body
            sb.append("</details>\n");
        }

        sb.append("</div>\n"); // cards-list
    }

    static String escHtml(String s)  { return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;"); }
    static String escAttr(String s)  { return escHtml(s).replace("\"","&quot;"); }

}
