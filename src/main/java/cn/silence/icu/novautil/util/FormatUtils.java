package cn.silence.icu.novautil.util;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2026/02/07 16:40 周六
 */
public class FormatUtils {

    private FormatUtils() {
    }

    /**
     * 计算字符串在等宽终端中的显示宽度
     * 中文/全角字符=2宽度，英文/数字/半角=1宽度
     */
    public static int getDisplayWidth(String str) {
        if (str == null) return 0;
        int width = 0;
        for (char c : str.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || c >= 0xFF00 && c <= 0xFFEF) { // 全角字符
                width += 2;
            } else {
                width += 1;
            }
        }
        return width;
    }

    /**
     * 左对齐填充：按显示宽度填充空格
     *
     * @param str         原始字符串
     * @param targetWidth 目标显示宽度（非字符数！）
     * @return 填充后的字符串
     */
    public static String leftAlign(String str, int targetWidth) {
        str = (str == null) ? "" : str;
        int current = getDisplayWidth(str);
        if (current >= targetWidth) return str; // 不截断（避免信息丢失）

        StringBuilder sb = new StringBuilder(str);
        int pad = targetWidth - current;
        while (pad-- > 0) sb.append(' ');
        return sb.toString();
    }

    /**
     * 右对齐填充（适用于工号等）
     */
    public static String rightAlign(String str, int targetWidth) {
        str = (str == null) ? "" : str;
        int current = getDisplayWidth(str);
        if (current >= targetWidth) return str;

        StringBuilder sb = new StringBuilder();
        int pad = targetWidth - current;
        while (pad-- > 0) sb.append(' ');
        sb.append(str);
        return sb.toString();
    }
}
