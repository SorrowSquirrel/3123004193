package com.gdut;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 分数类的测试：验证运算和解析。
 * 无需修改代码，验证核心数学逻辑。
 */
public class FractionTest {

    /**
     * 测试用例1：真分数加法。
     */
    @Test
    void testAddProperFractions() {
        Fraction a = new Fraction(1, 6);
        Fraction b = new Fraction(1, 8);
        Fraction result = a.add(b);
        Fraction expected = new Fraction(7, 24);
        assertEquals(expected, result, "1/6 + 1/8 应为 7/24");
    }

    /**
     * 测试用例2：减法无负数结果。
     */
    @Test
    void testSubtractNoNegative() {
        Fraction a = new Fraction(3, 4);
        Fraction b = new Fraction(1, 4);
        assertTrue(a.greaterOrEqual(b), "3/4 >= 1/4");
        Fraction result = a.subtract(b);
        Fraction expected = new Fraction(1, 2);
        assertEquals(expected, result, "3/4 - 1/4 应为 1/2");
    }

    /**
     * 测试用例3：除法结果为真分数。
     */
    @Test
    void testDivideToProperFraction() {
        Fraction a = new Fraction(1, 2);
        Fraction b = new Fraction(1, 3);
        Fraction result = a.divide(b);
        assertFalse(result.isInteger(), "结果不应为整数");
        Fraction expected = new Fraction(3, 2);
        assertEquals(expected, result, "1/2 / 1/3 应为 3/2");
    }

    /**
     * 测试用例4：混合数字符串解析。
     */
    @Test
    void testMixedNumberParse() {
        Fraction result = Fraction.parse("2'1/2");
        Fraction expected = new Fraction(5, 2);
        assertEquals(expected, result, "2'1/2 应解析为 5/2");
    }
}