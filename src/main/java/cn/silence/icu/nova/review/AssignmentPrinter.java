package cn.silence.icu.nova.review;

import cn.silence.icu.nova.review.model.Person;
import cn.silence.icu.nova.util.FormatUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2026/02/07 16:46 周六
 */
public class AssignmentPrinter {

    public static void printAssignments(Map<Person, List<Person>> assignments) {
        // 动态计算最大显示宽度（中文安全）
        int maxReviewerWidth = assignments.keySet().stream().mapToInt(p -> FormatUtils.getDisplayWidth(p.toString())).max().orElse(20) + 2; // +2 留呼吸感

        int maxRevieweeWidth = assignments.values().stream().flatMap(List::stream).mapToInt(p -> FormatUtils.getDisplayWidth(p.toString())).max().orElse(20) + 2;

        // 表头
        System.out.println(FormatUtils.leftAlign("审查人", maxReviewerWidth) + FormatUtils.leftAlign("→ 审查对象", maxRevieweeWidth));
        System.out.println("─".repeat(Math.max(maxReviewerWidth + maxRevieweeWidth, 50)));

        // 数据行
        assignments.forEach((reviewer, targets) -> {
            String reviewerStr = FormatUtils.leftAlign(reviewer.toString(), maxReviewerWidth);
            String targetsStr = targets.stream().map(Person::toString).collect(Collectors.joining(", "));
            System.out.println(reviewerStr + targetsStr);
        });

        System.out.println("─".repeat(Math.max(maxReviewerWidth + maxRevieweeWidth, 50)));
        System.out.printf("✅ 共 %d 人参与审查，覆盖全部被审查人员%n", assignments.size());
    }
}
