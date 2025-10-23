package com.gdut;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Expr 树操作测试：规范和中缀输出。
 * 手动构建树，无需调用私有方法。
 */
public class ExprTest {

    /**
     * 测试用例5：规范形式唯一性（交换 +）。
     */
    @Test
    void testCanonicalUniqueness() {
        Expr e1 = new Binary("+", new Leaf(new Fraction(1, 1)), new Leaf(new Fraction(2, 1)));
        Expr e2 = new Binary("+", new Leaf(new Fraction(2, 1)), new Leaf(new Fraction(1, 1)));
        String can1 = e1.getCanonical();
        String can2 = e2.getCanonical();
        assertEquals(can1, can2, "1 + 2 和 2 + 1 应有相同规范形式");
    }

    /**
     * 测试用例6：中缀输出带括号。
     */
    @Test
    void testInfixWithParens() {
        Expr innerSub = new Binary("-", new Leaf(new Fraction(3, 1)), new Leaf(new Fraction(2, 1)));
        Expr outerAdd = new Binary("+", new Leaf(new Fraction(1, 1)), innerSub);
        String infix = outerAdd.toInfix();
        assertEquals("1 + (3 - 2)", infix, "应为低优先右孩子加括号");
    }

    /**
     * 测试用例7：最多3运算符的 eval 计算。
     */
    @Test
    void testEvalWithMultipleOps() {
        Expr div = new Binary("/", new Leaf(new Fraction(3, 1)), new Leaf(new Fraction(4, 1)));
        Expr mul = new Binary("*", new Leaf(new Fraction(2, 1)), div);
        Expr add = new Binary("+", new Leaf(new Fraction(1, 1)), mul);
        Fraction result = add.eval();
        Fraction expected = new Fraction(5, 2);
        assertEquals(expected, result, "1 + 2 * (3 / 4) 应为 5/2");
    }
}