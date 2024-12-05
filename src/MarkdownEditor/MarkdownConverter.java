package MarkdownEditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownConverter {

    // 将 Markdown 文本转换为 HTML
    public String convert(String markdown) {
        StringBuilder html = new StringBuilder(" ");
        html.append("""
                    <!DOCTYPE html>
                    <html lang="zh">
                    <title>Markdown演示</title>
                    <body>
                    <div class="sidebar_left"></div>
                    <div class="content">
                    """); // 添加样式

        // 分行处理
        String[] lines = markdown.split("\n");
        boolean inOrderedList = false;
        boolean inUnorderedList = false;
        boolean inCodeBlock = false;

        for (String line : lines) {
            // 处理代码块
            if (line.startsWith("```")) {
                inCodeBlock = !inCodeBlock; // 切换代码块状态
                if (inCodeBlock) {
                    html.append("    <pre><code>\n"); // 缩进代码块开始
                } else {
                    html.append("    </code></pre>\n"); // 缩进代码块结束
                }
                continue; // 跳过代码块标记行
            }

            if (inCodeBlock) {
                html.append("        ").append(line).append("\n"); // 缩进代码行
                continue;
            }

            // 处理标题
            if (line.startsWith("# ")) {
                html.append(processHeader(line, 1));
            } else if (line.startsWith("## ")) {
                html.append(processHeader(line, 2));
            } else if (line.startsWith("### ")) {
                html.append(processHeader(line, 3));
            } else if (line.startsWith("#### ")) {
                html.append(processHeader(line, 4));
            } else if (line.startsWith("##### ")) {
                html.append(processHeader(line, 5));
            } else if (line.startsWith("###### ")) {
                html.append(processHeader(line, 6));
            } else if (line.startsWith("* ") || line.startsWith("- ")) {
                // 处理无序列表
                inUnorderedList = handleUnorderedList(html, line, inUnorderedList);
            } else if (line.matches("^\\d+\\. .*")) { // 有序列表（数字加点）
                inOrderedList = handleOrderedList(html, line, inOrderedList);
            } else {
                // 处理段落和普通文本
                if (inOrderedList) {
                    html.append("    </ol>\n"); // 缩进结束有序列表
                    inOrderedList = false;
                }
                if (inUnorderedList) {
                    html.append("    </ul>\n"); // 缩进结束无序列表
                    inUnorderedList = false;
                }

                String processedLine = processTextFormatting(line);
                html.append("    <p>").append(processedLine).append("</p>\n"); // 缩进段落
            }
        }

        // 结束未关闭的列表
        if (inOrderedList) {
            html.append("    </ol>\n");
        }
        if (inUnorderedList) {
            html.append("    </ul>\n");
        }

        html.append("""
                    </div>
                    <div class="sidebar_right"></div>
                    </div>
                    </body>
                    <style>
                    body {margin: 0;font-family: Arial, sans-serif;display: flex;height: 100vh;overflow: hidden;}
                    pre {background-color: rgba(255, 255, 255,1);padding: 10px;border-left: 5px solid #ccc; }
                    code {background-color: rgba(255, 255, 255,0.3);font-family: Arial, sans-serif;font-weight: bold;color: rgb(231, 49, 17);padding: 2px 4px;border-radius: 3px; }
                    .sidebar_left {width: 20%;left: 0%;background-color: rgba(0, 0, 0, 0.5);backdrop-filter: blur(10px);color: white;padding: 20px;box-shadow: 2px 0 5px rgba(0, 0, 0, 0.3);}
                    .sidebar_right {width: 20%;left: 0%;background-color: rgba(0, 0, 0, 0.5);backdrop-filter: blur(10px);color: white;padding: 20px;box-shadow: 2px 0 5px rgba(0, 0, 0, 0.3);}
                    .content {width: 80%;padding: 20px;overflow-y: auto;color: #f4f4f4;background-color:rgb(22, 21, 21);backdrop-filter: blur(20px);}
                    </style>
                    </html>
                    """);
        return html.toString();
    }

    // 处理标题
    private String processHeader(String line, int level) {
        return "    <h" + level + ">" + line.substring(level).trim() + "</h" + level + ">\n"; // 缩进标题
    }

    // 处理无序列表
    private boolean handleUnorderedList(StringBuilder html, String line, boolean inUnorderedList) {
        if (!inUnorderedList) {
            html.append("    <ul>\n"); // 缩进开始无序列表
            inUnorderedList = true;
        }
        html.append("        <li>").append(line.substring(2)).append("</li>\n"); // 缩进列表项
        return inUnorderedList;
    }

    // 处理有序列表
    private boolean handleOrderedList(StringBuilder html, String line, boolean inOrderedList) {
        if (!inOrderedList) {
            html.append("    <ol>\n"); // 缩进开始有序列表
            inOrderedList = true;
        }
        // 提取列表项内容
        Pattern pattern = Pattern.compile("^(\\d+)\\. (.*)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            html.append("        <li>").append(matcher.group(2)).append("</li>\n"); // 缩进列表项
        }
        return inOrderedList;
    }

    // 处理文字格式
    private String processTextFormatting(String line) {
        return line
                .replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>")  // 粗体
                .replaceAll("\\*(.+?)\\*", "<em>$1</em>")                // 斜体
                .replaceAll("`(.+?)`", "<code>$1</code>")                // 行内代码
                .replaceAll("\\[(.+?)\\]\\((.+?)\\)", "<a href=\"$2\">$1</a>"); // 链接
    }
}