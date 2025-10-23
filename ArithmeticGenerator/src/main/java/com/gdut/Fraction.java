package com.gdut;

import java.util.Objects;

/**
 * 分数类，用于处理自然数、真分数和混合数的运算、比较和字符串转换。
 */
public class Fraction {
    private long num;  // 分子
    private long den;  // 分母

    /**
     * 构造函数：创建分数并约分标准化。
     * @param num 分子
     * @param den 分母
     */
    public Fraction(long num, long den) {
        if (den == 0) throw new IllegalArgumentException("分母不能为零");
        long g = gcd(Math.abs(num), Math.abs(den));  // 计算最大公约数
        this.num = num / g;
        this.den = den / g;
        if (this.den < 0) {  // 确保分母为正
            this.den = -this.den;
            this.num = -this.num;
        }
    }

    /**
     * 私有方法：计算两个数的最大公约数（欧几里德算法）。
     * @param a 正整数a
     * @param b 正整数b
     * @return 最大公约数
     */
    private static long gcd(long a, long b) {
        while (b != 0) {
            long t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    /**
     * 加法运算：通分后相加。
     * @param other 另一个分数
     * @return 和
     */
    public Fraction add(Fraction other) {
        long newNum = this.num * other.den + other.num * this.den;
        long newDen = this.den * other.den;
        return new Fraction(newNum, newDen);
    }

    /**
     * 减法运算：通分后相减。
     * @param other 另一个分数
     * @return 差
     */
    public Fraction subtract(Fraction other) {
        long newNum = this.num * other.den - other.num * this.den;
        long newDen = this.den * other.den;
        return new Fraction(newNum, newDen);
    }

    /**
     * 乘法运算：交叉相乘。
     * @param other 另一个分数
     * @return 积
     */
    public Fraction multiply(Fraction other) {
        long newNum = this.num * other.num;
        long newDen = this.den * other.den;
        return new Fraction(newNum, newDen);
    }

    /**
     * 除法运算：转换为乘法。
     * @param other 另一个分数
     * @return 商
     */
    public Fraction divide(Fraction other) {
        if (other.num == 0) throw new IllegalArgumentException("除零错误");
        long newNum = this.num * other.den;
        long newDen = this.den * other.num;
        return new Fraction(newNum, newDen);
    }

    /**
     * 比较两个分数的大小。
     * @param other 另一个分数
     * @return 1（大于）、0（等于）、-1（小于）
     */
    public int compareTo(Fraction other) {
        long diff = this.num * other.den - other.num * this.den;
        if (diff > 0) return 1;
        if (diff < 0) return -1;
        return 0;
    }

    /**
     * 是否大于另一个分数。
     * @param other 另一个分数
     * @return true 如果大于
     */
    public boolean greaterThan(Fraction other) {
        return compareTo(other) > 0;
    }

    /**
     * 是否大于或等于另一个分数。
     * @param other 另一个分数
     * @return true 如果 >=
     */
    public boolean greaterOrEqual(Fraction other) {
        return compareTo(other) >= 0;
    }

    /**
     * 是否为零。
     * @return true 如果分子为0
     */
    public boolean isZero() {
        return num == 0;
    }

    /**
     * 获取分母。
     * @return 分母
     */
    public long getDenominator() {
        return den;
    }

    /**
     * 是否为整数。
     * @return true 如果分母为1
     */
    public boolean isInteger() {
        return den == 1;
    }

    /**
     * 比较两个分数是否相等（自定义）。
     * @param other 另一个分数
     * @return true 如果相等
     */
    public boolean equals(Fraction other) {
        if (other == null) return false;
        return this.num * other.den == other.num * this.den;
    }

    /**
     * Object 的 equals 方法重载。
     * @param obj 对象
     * @return true 如果相等
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Fraction)) return false;
        return equals((Fraction) obj);
    }

    /**
     * hashCode 重载，确保 equals 一致。
     * @return hash 值
     */
    @Override
    public int hashCode() {
        return Objects.hash(num, den);
    }

    /**
     * 转换为字符串：整数/真分数/混合数格式。
     * @return 字符串表示，如 "3/5" 或 "2'3/4"
     */
    public String toString() {
        if (den == 1) return String.valueOf(num);
        long whole = num / den;
        long rem = num % den;
        if (whole == 0) return rem + "/" + den;
        if (rem == 0) return String.valueOf(whole);
        return whole + "'" + rem + "/" + den;
    }

    /**
     * 静态方法：从字符串解析分数。
     * @param s 字符串，如 "3/5" 或 "2'3/8"
     * @return 分数对象
     */
    public static Fraction parse(String s) {
        if (s.contains("'")) {
            String[] parts = s.split("'");
            long whole = Long.parseLong(parts[0]);
            String fracPart = parts[1];
            String[] frac = fracPart.split("/");
            long fNum = Long.parseLong(frac[0]);
            long fDen = Long.parseLong(frac[1]);
            return new Fraction(whole * fDen + fNum, fDen);
        } else if (s.contains("/")) {
            String[] parts = s.split("/");
            return new Fraction(Long.parseLong(parts[0]), Long.parseLong(parts[1]));
        } else {
            return new Fraction(Long.parseLong(s), 1);
        }
    }
}