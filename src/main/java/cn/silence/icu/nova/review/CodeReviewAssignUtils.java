package cn.silence.icu.nova.review;

import cn.silence.icu.nova.review.model.Person;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2026/02/07 16:57 周六
 */
public class CodeReviewAssignUtils {

    private CodeReviewAssignUtils() {
    }

    // 单池分配：控制台打印清单
    public static void assignSinglePool(){
        List<Person> singlePool = null;
        try {
            singlePool = PoolConfigLoader.loadSinglePool("config/single_pool.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<Person, List<Person>> singleAssign = CodeReviewAssigner.assignSinglePool(singlePool);
        AssignmentPrinter.printAssignments(singleAssign);
    }

    public static void assignDualPool(){
        Map<String, List<Person>> dualPool = null;
        try {
            dualPool = PoolConfigLoader.loadDualPool("config/dual_pool.json");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<Person, List<Person>> dualAssign = CodeReviewAssigner.assignDualPool(dualPool.get("poolA"), dualPool.get("poolB"));
        AssignmentPrinter.printAssignments(dualAssign);
    }

//    public static void main(String[] args) {
//        assignSinglePool();
//        assignDualPool();
//    }
}
