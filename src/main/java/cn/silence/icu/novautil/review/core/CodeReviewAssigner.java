package cn.silence.icu.novautil.review.core;

import cn.silence.icu.novautil.review.model.Person;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 代码审查分配引擎
 * 核心原则：
 * 1. 被审查人（reviewee）必须100%全覆盖
 * 2. 审查人（reviewer）可抽样参与（大池仅抽样，小池全员上）
 * 3. 单池模式：循环互查（无自查）
 * 4. 双池模式：双向分配（A查B + B查A），严格隔离
 */
public final class CodeReviewAssigner {

    private CodeReviewAssigner() {
        // 工具类禁止实例化
    }

    // ==================== 单池分配 ====================

    /**
     * 单池循环分配：每人审查下一人（形成闭环）
     *
     * @param people 人员池（至少2人）
     * @return Map<审查人, 被审查人> （一对一）
     * @throws IllegalArgumentException 池子为空/不足2人/含重复工号
     */
    public static Map<Person, List<Person>> assignSinglePool(List<Person> people) {
        validatePeople(people, "单池");
        if (people.size() < 2) {
            throw new IllegalArgumentException("单池模式至少需要2人");
        }

        List<Person> shuffled = new ArrayList<>(people);
        Collections.shuffle(shuffled); // 打乱避免固定顺序

        Map<Person, List<Person>> assignment = new LinkedHashMap<>(shuffled.size());
        int n = shuffled.size();
        for (int i = 0; i < n; i++) {
            Person reviewer = shuffled.get(i);
            Person reviewee = shuffled.get((i + 1) % n); // 环形分配
            assignment.put(reviewer, Collections.singletonList(reviewee));
        }
        return assignment;
    }

    // ==================== 双池分配 ====================

    /**
     * 双池智能分配：确保被审查方100%覆盖，审查方可抽样
     *
     * @param poolA 池A（如前端组）
     * @param poolB 池B（如后端组）
     * @return Map<审查人, 被审查人列表> （一人可查多人）
     * @throws IllegalArgumentException 池子为空/含重复工号/双池存在交集
     */
    public static Map<Person, List<Person>> assignDualPool(
            List<Person> poolA,
            List<Person> poolB
    ) {
        validatePeople(poolA, "poolA");
        validatePeople(poolB, "poolB");
        validateNoOverlap(poolA, poolB);

        Map<Person, List<Person>> assignment = new LinkedHashMap<>();
        // A审查B：B池全员必须被查
        assignment.putAll(assignDirection(poolA, poolB, "A→B"));
        // B审查A：A池全员必须被查
        assignment.putAll(assignDirection(poolB, poolA, "B→A"));
        return assignment;
    }

    // ==================== 核心：单向分配逻辑 ====================

    /**
     * 分配方向：reviewers（候选审查方） → reviewees（被审查方，必须全覆盖）
     *
     * @param reviewers 候选审查人池
     * @param reviewees 被审查人池（结果中必须100%出现）
     * @param direction 标识方向（用于异常提示）
     * @return Map<实际参与的审查人, 被分配的被审查人列表>
     */
    private static Map<Person, List<Person>> assignDirection(
            List<Person> reviewers,
            List<Person> reviewees,
            String direction
    ) {
        if (reviewees.isEmpty()) {
            return new LinkedHashMap<>();
        }
        if (reviewers.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("方向[%s]：审查方为空，无法覆盖被审查方（%d人）",
                            direction, reviewees.size())
            );
        }

        // 深拷贝+打乱（避免修改原列表，保证随机性）
        List<Person> shuffledReviewees = new ArrayList<>(reviewees);
        Collections.shuffle(shuffledReviewees);
        Map<Person, List<Person>> assignment = new LinkedHashMap<>();

        if (reviewers.size() >= reviewees.size()) {
            // 大池审查小池：仅抽 |reviewees| 人，每人查1人（小池全员被查）
            List<Person> shuffledReviewers = new ArrayList<>(reviewers);
            Collections.shuffle(shuffledReviewers);
            List<Person> selectedReviewers = shuffledReviewers.subList(0, reviewees.size());
            for (int i = 0; i < selectedReviewers.size(); i++) {
                assignment.put(
                        selectedReviewers.get(i),
                        Collections.singletonList(shuffledReviewees.get(i))
                );
            }
        } else {
            // 小池审查大池：小池全员上，均衡分配（大池全员被查）
            List<Person> shuffledReviewers = new ArrayList<>(reviewers);
            Collections.shuffle(shuffledReviewers);
            for (int i = 0; i < shuffledReviewees.size(); i++) {
                Person reviewer = shuffledReviewers.get(i % shuffledReviewers.size());
                assignment.computeIfAbsent(reviewer, k -> new ArrayList<>())
                        .add(shuffledReviewees.get(i));
            }
        }
        return assignment;
    }

    // ==================== 校验工具 ====================

    /**
     * 校验人员池基础合法性
     */
    private static void validatePeople(List<Person> people, String poolName) {
        if (people == null) {
            throw new IllegalArgumentException(poolName + " 不能为 null");
        }
        if (people.isEmpty()) {
            throw new IllegalArgumentException(poolName + " 不能为空");
        }
        if (people.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException(poolName + " 包含 null 元素");
        }
        // 检查重复工号（工号作为唯一标识）
        Set<String> ids = new HashSet<>();
        for (Person p : people) {
            if (!ids.add(p.getEmployeeId())) {
                throw new IllegalArgumentException(
                        String.format("%s 存在重复工号: %s", poolName, p.getEmployeeId())
                );
            }
        }
    }

    /**
     * 校验双池无人员交集（基于工号）
     */
    private static void validateNoOverlap(List<Person> poolA, List<Person> poolB) {
        Set<String> poolAIds = poolA.stream()
                .map(Person::getEmployeeId)
                .collect(Collectors.toSet());
        List<String> overlapIds = poolB.stream()
                .map(Person::getEmployeeId)
                .filter(poolAIds::contains)
                .collect(Collectors.toList());
        if (!overlapIds.isEmpty()) {
            throw new IllegalArgumentException(
                    "双池存在交集人员（工号）: " + String.join(", ", overlapIds)
            );
        }
    }
}
