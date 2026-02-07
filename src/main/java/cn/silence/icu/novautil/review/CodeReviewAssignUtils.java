package cn.silence.icu.novautil.review;

import cn.silence.icu.novautil.review.config.PoolConfigLoader;
import cn.silence.icu.novautil.review.core.AssignmentExporter;
import cn.silence.icu.novautil.review.core.AssignmentPrinter;
import cn.silence.icu.novautil.review.core.CodeReviewAssigner;
import cn.silence.icu.novautil.review.model.Person;

import java.io.File;
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

    public static void assignSinglePoolPrint() throws IOException {
        List<Person> singlePool = PoolConfigLoader.loadSinglePool("config/single_pool.json");
        Map<Person, List<Person>> singleAssign = CodeReviewAssigner.assignSinglePool(singlePool);
        AssignmentPrinter.printAssignments(singleAssign);
    }

    public static void assignSinglePoolCsv() throws IOException {
        List<Person> singlePool = PoolConfigLoader.loadSinglePool("config/single_pool.json");
        Map<Person, List<Person>> singleAssign = CodeReviewAssigner.assignSinglePool(singlePool);
        AssignmentExporter.exportToCsv(singleAssign, System.getProperty("user.dir") + File.separator + ".uncommit/docs/");
    }

    public static void assignSinglePoolMarkdown() throws IOException {
        List<Person> singlePool = PoolConfigLoader.loadSinglePool("config/single_pool.json");
        Map<Person, List<Person>> singleAssign = CodeReviewAssigner.assignSinglePool(singlePool);
        AssignmentExporter.exportToMarkdown(singleAssign, System.getProperty("user.dir") + File.separator + ".uncommit/docs/");
    }

    public static void assignDualPoolPrint() throws IOException {
        Map<String, List<Person>> dualPool = PoolConfigLoader.loadDualPool("config/dual_pool.json");
        Map<Person, List<Person>> dualAssign = CodeReviewAssigner.assignDualPool(dualPool.get("poolA"), dualPool.get("poolB"));
        AssignmentPrinter.printAssignments(dualAssign);
    }

    public static void assignDualPoolCsv() throws IOException {
        Map<String, List<Person>> dualPool = PoolConfigLoader.loadDualPool("config/dual_pool.json");
        Map<Person, List<Person>> dualAssign = CodeReviewAssigner.assignDualPool(dualPool.get("poolA"), dualPool.get("poolB"));
        AssignmentExporter.exportToCsv(dualAssign, System.getProperty("user.dir") + File.separator + ".uncommit/docs/");

    }

    public static void assignDualPoolMarkdown() throws IOException {
        Map<String, List<Person>> dualPool = PoolConfigLoader.loadDualPool("config/dual_pool.json");
        Map<Person, List<Person>> dualAssign = CodeReviewAssigner.assignDualPool(dualPool.get("poolA"), dualPool.get("poolB"));
        AssignmentExporter.exportToMarkdown(dualAssign, System.getProperty("user.dir") + File.separator + ".uncommit/docs/");
    }
}
