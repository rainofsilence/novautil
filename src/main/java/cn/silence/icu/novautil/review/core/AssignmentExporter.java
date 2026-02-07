package cn.silence.icu.novautil.review.core;

import cn.silence.icu.novautil.review.model.Person;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码审查分配结果导出工具类（增强版）
 * ✅ 自动在文件名添加时间戳（避免覆盖）
 * ✅ 智能路径解析（支持目录/文件路径）
 * ✅ 中文安全处理 + 专业排版
 */
public final class AssignmentExporter {

    // 时间戳格式：文件名用紧凑格式，内容用详细格式
    private static final DateTimeFormatter FILENAME_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final DateTimeFormatter CONTENT_TIMESTAMP_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 默认文件名前缀
    private static final String CSV_PREFIX = "code_review_assignments";
    private static final String MD_PREFIX = "review_summary";

    private AssignmentExporter() {
        // 工具类禁止实例化
    }

    // ==================== CSV 导出（自动时间戳） ====================

    /**
     * 导出分配结果为 CSV 文件（自动在文件名添加时间戳）
     *
     * @param assignment 分配结果 Map<审查人, 被审查人列表>
     * @param basePath   基础路径（支持两种用法）：
     *                   - 目录路径：如 "exports/" → 生成 exports/code_review_assignments_20260207_174522.csv
     *                   - 文件路径：如 "review.csv" → 生成 review_20260207_174522.csv
     * @throws IOException 文件写入异常
     */
    public static void exportToCsv(Map<Person, List<Person>> assignment, String basePath) throws IOException {
        validateAssignment(assignment, "CSV");

        // 生成带时间戳的完整文件路径
        String timestamp = LocalDateTime.now().format(FILENAME_TIMESTAMP_FORMATTER);
        String finalPath = generateTimestampedPath(basePath, timestamp, "csv", CSV_PREFIX);

        createParentDir(finalPath);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(finalPath), StandardCharsets.UTF_8))) {

            // BOM 头（确保 Excel 正确识别 UTF-8）
            writer.write("\ufeff");

            // 表头
            writer.write("审查人姓名,审查人工号,被审查人姓名,被审查人工号,审查人数,分配时间");
            writer.newLine();

            // 数据行（内容使用详细时间戳）
            String contentTime = LocalDateTime.now().format(CONTENT_TIMESTAMP_FORMATTER);
            for (Map.Entry<Person, List<Person>> entry : assignment.entrySet()) {
                Person reviewer = entry.getKey();
                List<Person> reviewees = entry.getValue();

                String revieweeNames = String.join(";",
                        reviewees.stream().map(Person::getName).toArray(String[]::new));
                String revieweeIds = String.join(";",
                        reviewees.stream().map(Person::getEmployeeId).toArray(String[]::new));

                String[] row = {
                        escapeCsv(reviewer.getName()),
                        escapeCsv(reviewer.getEmployeeId()),
                        escapeCsv(revieweeNames),
                        escapeCsv(revieweeIds),
                        String.valueOf(reviewees.size()),
                        contentTime
                };
                writer.write(String.join(",", row));
                writer.newLine();
            }

            System.out.printf("✅ CSV 导出成功: %s (共 %d 条记录)%n",
                    finalPath, assignment.size());
        }
    }

    // ==================== Markdown 导出（自动时间戳） ====================

    /**
     * 导出分配结果为专业 Markdown 报告（自动在文件名添加时间戳）
     *
     * @param assignment 分配结果 Map<审查人, 被审查人列表>
     * @param basePath   基础路径（支持两种用法）：
     *                   - 目录路径：如 "docs/" → 生成 docs/review_summary_20260207_174522.md
     *                   - 文件路径：如 "review.md" → 生成 review_20260207_174522.md
     * @throws IOException 文件写入异常
     */
    public static void exportToMarkdown(Map<Person, List<Person>> assignment, String basePath) throws IOException {
        validateAssignment(assignment, "Markdown");

        // 生成带时间戳的完整文件路径
        String timestamp = LocalDateTime.now().format(FILENAME_TIMESTAMP_FORMATTER);
        String finalPath = generateTimestampedPath(basePath, timestamp, "md", MD_PREFIX);

        createParentDir(finalPath);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(finalPath), StandardCharsets.UTF_8))) {

            // ===== 标题与元信息（内容使用详细时间戳）=====
            String contentTime = LocalDateTime.now().format(CONTENT_TIMESTAMP_FORMATTER);
            writer.write("# 📋 代码审查分配清单\n\n");
            writer.write("> **生成时间**: " + contentTime + "  \n");
            writer.write("> **分配模式**: 双池交叉审查  \n");

            // ===== 统计摘要 =====
            int totalReviewers = assignment.size();
            int totalTasks = assignment.values().stream().mapToInt(List::size).sum();
            Set<String> uniqueRevieweeIds = assignment.values().stream()
                    .flatMap(List::stream)
                    .map(Person::getEmployeeId)
                    .collect(Collectors.toSet());

            writer.write("\n## 📊 分配统计\n");
            writer.write("| 项目 | 数值 |\n");
            writer.write("|------|------|\n");
            writer.write(String.format("| 参与审查人数 | **%d** 人 |\n", totalReviewers));
            writer.write(String.format("| 被审查覆盖人数 | **%d** 人 |\n", uniqueRevieweeIds.size()));
            writer.write(String.format("| 审查任务总数 | **%d** 项 |\n", totalTasks));
            writer.write(String.format("| 人均审查量 | **%.1f** 人/人 |\n",
                    (double) totalTasks / totalReviewers));
            writer.write("\n> 💡 **分配原则**: 被审查人100%覆盖 | 审查人按需抽样 | 任务量均衡（差≤1）\n\n");

            // ===== 详细分配表 =====
            writer.write("## 👥 详细分配明细\n");
            writer.write("| 审查人 | 工号 | 被审查人 | 被审查人工号 | 人数 |\n");
            writer.write("|:-------|:-----|:----------|:--------------|-----:|\n");

            // 按审查人工号排序（便于查阅）
            assignment.entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getKey().getEmployeeId()))
                    .forEach(entry -> {
                        Person reviewer = entry.getKey();
                        List<Person> reviewees = entry.getValue();

                        String revieweeNames = reviewees.stream()
                                .map(p -> escapeMarkdown(p.getName()))
                                .collect(Collectors.joining(", "));
                        String revieweeIds = reviewees.stream()
                                .map(Person::getEmployeeId)
                                .collect(Collectors.joining(", "));

                        try {
                            writer.write(String.format(
                                    "| %s | `%s` | %s | `%s` | %d |\n",
                                    escapeMarkdown(reviewer.getName()),
                                    reviewer.getEmployeeId(),
                                    revieweeNames.isEmpty() ? "—" : revieweeNames,
                                    revieweeIds.isEmpty() ? "—" : revieweeIds,
                                    reviewees.size()
                            ));
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });

            // ===== 使用说明 =====
            writer.write("\n## ℹ️ 使用说明\n");
            writer.write("- **审查人**：需在规定时间内完成所列人员的代码审查\n");
            writer.write("- **被审查人**：请提前准备好待审查代码并通知审查人\n");
            writer.write("- **任务量**：系统已自动均衡分配（最大差值≤1人）\n");
            writer.write("- **问题反馈**：分配异常请联系技术负责人\n\n");

            // ===== 页脚 =====
            writer.write("---\n");
            writer.write("**NovaUtil 代码审查分配系统** | v1.0.0  \n");
            writer.write("© 2026 团队名称 | 本清单自动生成，文件名含时间戳避免覆盖");

            System.out.printf("✅ Markdown 导出成功: %s (覆盖 %d 人)%n",
                    finalPath, uniqueRevieweeIds.size());
        }
    }

    // ==================== 智能路径生成（核心增强） ====================

    /**
     * 生成带时间戳的完整文件路径
     *
     * @param basePath      基础路径（用户输入）
     * @param timestamp     时间戳（yyyyMMdd_HHmmss）
     * @param extension     扩展名（不含点）
     * @param defaultPrefix 默认文件名前缀（当basePath为目录时使用）
     * @return 完整文件路径
     */
    private static String generateTimestampedPath(String basePath, String timestamp,
                                                  String extension, String defaultPrefix) {
        if (basePath == null || basePath.trim().isEmpty()) {
            basePath = "."; // 默认当前目录
        }

        File baseFile = new File(basePath);
        String parentDir;
        String newFilename;

        // 判断是否为目录（三种情况：显式目录结尾、实际是目录、无扩展名视为目录）
        boolean isDirectory = basePath.endsWith(File.separator) ||
                basePath.endsWith("/") ||
                (baseFile.isDirectory() && baseFile.exists()) ||
                (!basePath.contains(".") && !baseFile.isFile());

        if (isDirectory) {
            // 情况1：basePath是目录 → 使用默认前缀
            parentDir = baseFile.getAbsolutePath();
            newFilename = String.format("%s_%s.%s", defaultPrefix, timestamp, extension);
        } else {
            // 情况2：basePath是文件路径 → 保留原文件名基础部分
            parentDir = baseFile.getParent() != null ? baseFile.getParent() : ".";
            String originalName = baseFile.getName();

            // 拆分原文件名：移除扩展名（保留多段扩展名如 .tar.gz 的最后一段）
            int lastDotIndex = originalName.lastIndexOf('.');
            String baseName, ext;
            if (lastDotIndex > 0 && lastDotIndex < originalName.length() - 1) {
                baseName = originalName.substring(0, lastDotIndex);
                ext = originalName.substring(lastDotIndex + 1);
                // 仅当扩展名匹配目标类型时替换，否则保留原扩展名
                if (ext.equalsIgnoreCase(extension)) {
                    newFilename = String.format("%s_%s.%s", baseName, timestamp, extension);
                } else {
                    newFilename = String.format("%s_%s.%s", originalName, timestamp, extension);
                }
            } else {
                // 无扩展名 → 直接追加
                newFilename = String.format("%s_%s.%s", originalName, timestamp, extension);
            }
        }

        return new File(parentDir, newFilename).getAbsolutePath();
    }

    // ==================== 辅助方法（保持不变） ====================

    /**
     * 验证分配数据有效性
     */
    private static void validateAssignment(Map<Person, List<Person>> assignment, String format) {
        if (assignment == null || assignment.isEmpty()) {
            throw new IllegalArgumentException(format + "导出: 分配数据为空");
        }
        for (Map.Entry<Person, List<Person>> entry : assignment.entrySet()) {
            if (entry.getKey() == null) {
                throw new IllegalArgumentException(format + "导出: 审查人存在 null");
            }
            if (entry.getValue() == null) {
                throw new IllegalArgumentException(
                        String.format("%s导出: 审查人[%s]的被审查列表为 null",
                                format, entry.getKey().getEmployeeId()));
            }
        }
    }

    /**
     * 创建文件父目录
     */
    private static void createParentDir(String filePath) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("无法创建目录: " + parent.getAbsolutePath());
        }
    }

    /**
     * CSV 字段转义
     */
    private static String escapeCsv(String field) {
        if (field == null || field.isEmpty()) return "";
        if (field.contains(",") || field.contains("\n") || field.contains("\"")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

    /**
     * Markdown 内容转义
     */
    private static String escapeMarkdown(String text) {
        if (text == null) return "";
        return text.replace("|", "\\|").replace("\n", " ");
    }

    // ==================== 单池结果转换 ====================

    /**
     * 将单池分配结果转换为双池格式（便于统一导出）
     *
     * @param singlePoolAssignment 单池结果 Map<Person, Person>
     * @return 转换后的 Map<Person, List<Person>>
     */
    public static Map<Person, List<Person>> convertSingleToDualFormat(
            Map<Person, Person> singlePoolAssignment) {
        if (singlePoolAssignment == null) {
            throw new IllegalArgumentException("单池分配结果不能为空");
        }
        Map<Person, List<Person>> converted = new LinkedHashMap<>();
        for (Map.Entry<Person, Person> entry : singlePoolAssignment.entrySet()) {
            converted.put(entry.getKey(), Collections.singletonList(entry.getValue()));
        }
        return converted;
    }
}
