package cn.silence.icu;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Eych4o
 * @version 1.0.0
 * @since 2026/02/05 20:30 周四
 */
public class CodeReviewAssigner {// 单池分配：输入Person列表，返回分配映射（检查者 -> 被检查者）

    public static Map<Person, Person> assignSinglePool(List<Person> people) {
        validatePeople(people, "单池");
        if (people.size() < 2)
            throw new IllegalArgumentException("单池模式至少需要2人");

        List<Person> shuffled = new ArrayList<>(people);
        Collections.shuffle(shuffled);

        Map<Person, Person> assignment = new LinkedHashMap<>();
        int n = shuffled.size();
        for (int i = 0; i < n; i++) {
            assignment.put(shuffled.get(i), shuffled.get((i + 1) % n));
        }
        return assignment;
    }

    // 双池分配
    public static Map<Person, Person> assignDualPool(List<Person> poolA, List<Person> poolB) {
        validatePeople(poolA, "池A");
        validatePeople(poolB, "池B");
        if (!Collections.disjoint(poolA, poolB)) {
            Set<Person> intersect = new HashSet<>(poolA);
            intersect.retainAll(poolB);
            throw new IllegalArgumentException("双池存在交集人员: " +
                    intersect.stream().map(Person::getEmployeeId).collect(Collectors.joining(",")));
        }

        Map<Person, Person> assignment = new LinkedHashMap<>();
        assignment.putAll(assignCross(poolA, poolB));
        assignment.putAll(assignCross(poolB, poolA));
        return assignment;
    }

    // ===== 私有工具方法 =====
    private static void validatePeople(List<Person> list, String poolName) {
        if (list == null || list.isEmpty())
            throw new IllegalArgumentException(poolName + "不能为空");
        if (hasDuplicateEmployeeId(list))
            throw new IllegalArgumentException(poolName + "存在重复工号");
    }

    private static boolean hasDuplicateEmployeeId(List<Person> list) {
        return list.size() != list.stream().map(Person::getEmployeeId).distinct().count();
    }

    private static Map<Person, Person> assignCross(List<Person> from, List<Person> to) {
        List<Person> shuffledFrom = new ArrayList<>(from);
        List<Person> shuffledTo = new ArrayList<>(to);
        Collections.shuffle(shuffledFrom);
        Collections.shuffle(shuffledTo);

        Map<Person, Person> map = new LinkedHashMap<>();
        for (int i = 0; i < shuffledFrom.size(); i++) {
            map.put(shuffledFrom.get(i), shuffledTo.get(i % shuffledTo.size()));
        }
        return map;
    }
}
