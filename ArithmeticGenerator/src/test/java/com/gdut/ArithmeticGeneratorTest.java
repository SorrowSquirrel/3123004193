package com.gdut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ArithmeticGenerator 集成测试。
 * 使用反射访问私有 generate，无需修改代码。
 * grade 使用临时文件直接调用。
 * 正确处理反射异常。
 */
public class ArithmeticGeneratorTest {

    /**
     * 测试用例8：通过反射运行 generate 并检查文件输出。
     * @throws Exception 反射异常
     */
    @Test
    void testGenerateBasicNoReflection() throws Exception {
        // 使用反射调用私有静态 generate(int, int)
        Method method = ArithmeticGenerator.class.getDeclaredMethod("generate", int.class, int.class);
        method.setAccessible(true);
        try {
            method.invoke(null, 1, 2);  // 生成1题，r=2
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause instanceof Exception) {
                    throw (Exception) cause;  // 重新抛出底层异常
                } else {
                    throw new RuntimeException("意外的非 Exception 原因", cause);
                }
            } else {
                throw new RuntimeException("意外的 InvocationTargetException 无原因", e);
            }
        }

        File exerFile = new File("Exercises.txt");
        assertTrue(exerFile.exists(), "应生成 Exercises.txt");
        List<String> lines = Files.readAllLines(exerFile.toPath());
        assertEquals(1, lines.size(), "应生成1行");
        assertTrue(lines.get(0).matches("\\d+\\. .* ="), "格式：编号. 表达式 =");

        // 清理
        exerFile.delete();
        new File("Answers.txt").delete();
    }

    /**
     * 测试用例9：批改正确/错误答案。
     * @param tempDir 临时目录
     * @throws IOException IO异常
     */
    @Test
    void testGradeCorrectWrong(@TempDir Path tempDir) throws IOException {
        Path exerPath = tempDir.resolve("exer.txt");
        Files.writeString(exerPath, "1. 1 + 1 =\n2. 1 + 2 =");

        Path ansPath = tempDir.resolve("ans.txt");
        Files.writeString(ansPath, "1. 2\n2. 4");  // 第二题错（应为3）

        ArithmeticGenerator.grade(exerPath.toString(), ansPath.toString());

        File gradeFile = new File("Grade.txt");
        assertTrue(gradeFile.exists(), "应生成 Grade.txt");
        List<String> gradeLines = Files.readAllLines(gradeFile.toPath());
        assertTrue(gradeLines.get(0).contains("Correct: 1 (1)"), "一个正确");
        assertTrue(gradeLines.get(1).contains("Wrong: 1 (2)"), "一个错误");

        // 清理
        gradeFile.delete();
    }

    /**
     * 测试用例10：缺少 -r 参数的错误处理。
     * @throws Exception 反射异常
     */
    @Test
    void testMainNoRParameter() throws Exception {
        // 使用反射调用 main 无参数
        Method mainMethod = ArithmeticGenerator.class.getDeclaredMethod("main", String[].class);
        mainMethod.setAccessible(true);
        String[] args = {};  // 无参数
        try {
            mainMethod.invoke(null, new Object[]{args});
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause != null) {
                if (cause instanceof Exception) {
                    throw (Exception) cause;
                } else {
                    throw new RuntimeException("意外的非 Exception 原因", cause);
                }
            } else {
                throw new RuntimeException("意外的 InvocationTargetException 无原因", e);
            }
        }

        // 验证无生成
        assertFalse(new File("Exercises.txt").exists(), "无 -r 不生成");
        assertFalse(new File("Answers.txt").exists(), "无 -r 不生成");
    }
}