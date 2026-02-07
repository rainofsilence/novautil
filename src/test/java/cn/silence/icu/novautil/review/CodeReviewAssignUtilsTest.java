package cn.silence.icu.novautil.review;

import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author rainofsilence
 * @version 1.0.0
 * @since 2026/02/07 17:19 周六
 */
class CodeReviewAssignUtilsTest {


    @Test
    void assignSinglePoolPrint() {
        try {
            CodeReviewAssignUtils.assignSinglePoolPrint();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void assignSinglePoolCsv() {
        try {
            CodeReviewAssignUtils.assignSinglePoolCsv();
        } catch (IOException e) {}
    }

    @Test
    void assignSinglePoolMarkdown() {
        try {
            CodeReviewAssignUtils.assignSinglePoolMarkdown();
        } catch (IOException e) {}
    }

    @Test
    void assignDualPoolPrint() {
        try {
            CodeReviewAssignUtils.assignDualPoolPrint();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void assignDualPoolCsv() {
        try {
            CodeReviewAssignUtils.assignDualPoolCsv();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void assignDualPoolMarkdown() {
        try {
            CodeReviewAssignUtils.assignDualPoolMarkdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
