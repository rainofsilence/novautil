package cn.silence.icu;

import java.io.IOException;
import java.util.Map;

/**
 * @author Eych4o
 * @version 1.0.0
 * @since 2026/02/05 20:11 周四
 *///TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        try {
            // ===== 单池模式 =====
            var singlePool = JsonConfigLoader.loadSinglePool("config/single_pool.json");
            Map<Person, Person> singleAssign = CodeReviewAssigner.assignSinglePool(singlePool);
            System.out.println("===== 单池分配结果 =====");
            singleAssign.forEach((rev, tar) ->
                    System.out.printf("✅ %s 检查 %s%n", rev, tar));

            // ===== 双池模式 =====
            var dualPools = JsonConfigLoader.loadDualPool("config/dual_pool.json");
            Map<Person, Person> dualAssign = CodeReviewAssigner.assignDualPool(
                    dualPools.get("poolA"), dualPools.get("poolB"));
            System.out.println("\n===== 双池分配结果 =====");
            dualAssign.forEach((rev, tar) ->
                    System.out.printf("✅ %s 检查 %s%n", rev, tar));

        } catch (IOException e) {
            System.err.println("❌ 配置文件加载失败: " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("❌ 分配逻辑错误: " + e.getMessage());
            System.exit(1);
        }
    }
}
