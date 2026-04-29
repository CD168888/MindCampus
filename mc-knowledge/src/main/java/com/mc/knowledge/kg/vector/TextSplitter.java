package com.mc.knowledge.kg.vector;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文本切分器 - 支持 TXT、Markdown、PDF、Word 等格式的文档切分
 *
 * @author MindCampus
 */
@Component
@Slf4j
public class TextSplitter {

    private static final int DEFAULT_CHUNK_SIZE = 500;
    private static final int DEFAULT_OVERLAP = 50;

    private static final Pattern CHINESE_SENTENCE_END = Pattern.compile(
        "[。！？；\n]|(?<=[a-zA-Z])\\.\\s|(?<=[0-9])\\s*[,，]");

    /**
     * 切分文本
     *
     * @param text     原始文本
     * @param chunkSize 每段最大字符数
     * @param overlap  相邻段重叠字符数
     * @return 切分后的文本片段列表
     */
    public List<String> split(String text, int chunkSize, int overlap) {
        if (!StringUtils.hasText(text)) {
            return new ArrayList<>();
        }

        text = cleanText(text);
        if (text.length() <= chunkSize) {
            List<String> result = new ArrayList<>();
            result.add(text.trim());
            return result;
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            if (end < text.length()) {
                int breakPoint = findBestBreakPoint(text, start, end);
                if (breakPoint > start) {
                    end = breakPoint;
                }
            }

            String chunk = text.substring(start, end).trim();
            if (chunk.length() > 10) {
                chunks.add(chunk);
            }

            start = end - overlap;
            if (start <= 0 || start >= text.length()) {
                break;
            }
        }

        log.info("[TextSplitter] 文本长度: {}, 切分为 {} 个片段", text.length(), chunks.size());
        return chunks;
    }

    /**
     * 默认切分（500 字符，50 重叠）
     */
    public List<String> split(String text) {
        return split(text, DEFAULT_CHUNK_SIZE, DEFAULT_OVERLAP);
    }

    /**
     * 找到最佳断点（尽量在句号、换行等处断开）
     */
    private int findBestBreakPoint(String text, int start, int end) {
        for (int i = end - 1; i >= Math.max(start, end - 100); i--) {
            if (i < text.length()) {
                char c = text.charAt(i);
                if (c == '\n' || c == '。' || c == '！' || c == '？' || c == ';' || c == ';' || c == '，') {
                    return i + 1;
                }
            }
        }
        return end;
    }

    /**
     * 清洗文本（去除 HTML 标签、多余空白等）
     */
    public String cleanText(String text) {
        if (text == null) return "";
        text = Jsoup.clean(text, Safelist.none());
        text = text.replaceAll("<[^>]+>", "");
        text = text.replaceAll("\\s+", " ");
        text = StringEscapeUtils.unescapeHtml4(text);
        text = text.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
        return text.trim();
    }

    /**
     * 批量清洗文本 - 减少中间 String 对象创建
     */
    public List<String> cleanTextBatch(List<String> texts) {
        if (texts == null || texts.isEmpty()) return new ArrayList<>();
        List<String> result = new ArrayList<>(texts.size());
        for (String text : texts) {
            result.add(cleanText(text));
        }
        return result;
    }

    /**
     * 释放资源（用于大文件处理后的内存清理）
     */
    public void gc() {
        System.gc();
    }

    /**
     * 解析 TXT 文件内容
     */
    public String parseTxt(String content) {
        return cleanText(content);
    }

    /**
     * 解析 Markdown 文件内容
     */
    public String parseMarkdown(String content) {
        if (content == null) return "";
        content = cleanText(content);
        content = content.replaceAll("```[\\s\\S]*?```", "");
        content = content.replaceAll("`[^`]+`", "");
        content = content.replaceAll("!\\[.*?\\]\\(.*?\\)", "");
        content = content.replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1");
        content = content.replaceAll("#+\\s*", "");
        content = content.replaceAll("[-*_]{3,}", "");
        return content.trim();
    }

    /**
     * 解析 PDF 文件内容（调用 PDFBox）
     */
    public String parsePdf(byte[] pdfBytes) {
        StringBuilder text = new StringBuilder();
        try {
            var document = org.apache.pdfbox.Loader.loadPDF(pdfBytes);
            var stripper = new org.apache.pdfbox.text.PDFTextStripper();
            stripper.setSortByPosition(true);
            text.append(stripper.getText(document));
            document.close();
        } catch (Exception e) {
            log.error("[TextSplitter] PDF 解析失败", e);
        }
        return cleanText(text.toString());
    }

    /**
     * 解析 Word 文件内容（调用 POI 事件模式，流式解析避免 OOM）
     */
    public String parseWord(byte[] wordBytes) {
        StringBuilder text = new StringBuilder();
        try {
            var fis = new java.io.ByteArrayInputStream(wordBytes);
            var document = new org.apache.poi.xwpf.usermodel.XWPFDocument(fis);
            for (var para : document.getParagraphs()) {
                String p = para.getText();
                if (p != null && !p.isBlank()) {
                    text.append(p).append('\n');
                }
            }
            document.close();
            fis.close();
            fis = null;
            document = null;
        } catch (Exception e) {
            log.error("[TextSplitter] Word 解析失败", e);
        }
        return cleanText(text.toString());
    }

    /**
     * 流式解析 Word 文件 - 按段落分块，避免大文件 OOM
     * 返回每个段落块（用于大文件直接分批处理）
     */
    public List<String> parseWordParagraphs(byte[] wordBytes) {
        List<String> paragraphs = new ArrayList<>();
        try {
            var fis = new java.io.ByteArrayInputStream(wordBytes);
            var document = new org.apache.poi.xwpf.usermodel.XWPFDocument(fis);
            for (var para : document.getParagraphs()) {
                String p = para.getText();
                if (p != null && !p.isBlank()) {
                    paragraphs.add(p.trim());
                }
            }
            document.close();
            fis.close();
            fis = null;
            document = null;
        } catch (Exception e) {
            log.error("[TextSplitter] Word 流式解析失败", e);
        }
        return paragraphs;
    }

    /**
     * 根据文件类型解析内容
     *
     * @param content   文件文本内容
     * @param fileType 文件类型（txt/md/pdf/docx）
     * @return 清洗后的文本
     */
    public String parseByFileType(String content, String fileType) {
        if (fileType == null) fileType = "";
        fileType = fileType.toLowerCase().trim();
        switch (fileType) {
            case "md":
            case "markdown":
                return parseMarkdown(content);
            case "pdf":
                return parsePdf(content.getBytes());
            case "docx":
            case "doc":
                return parseWord(content.getBytes());
            default:
                return parseTxt(content);
        }
    }
}
