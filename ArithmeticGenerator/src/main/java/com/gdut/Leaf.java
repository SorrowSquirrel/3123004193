package com.gdut;

/**
 * 叶子节点类：表示常量分数。
 */
public class Leaf extends Expr {
    private Fraction value;  // 存储的分数值

    /**
     * 构造函数：创建叶子节点。
     * @param value 分数
     */
    public Leaf(Fraction value) {
        this.value = value;
    }

    /**
     * 中缀输出：直接返回字符串。
     */
    @Override
    public String toInfix(int parentPrec, boolean isRightChild) {
        return value.toString();
    }

    /**
     * 规范形式：直接字符串。
     */
    @Override
    public String getCanonical() {
        return value.toString();
    }

    /**
     * 计算：返回自身值。
     */
    @Override
    public Fraction eval() {
        return value;
    }

    /**
     * 叶子总是有效。
     */
    @Override
    public boolean isValid() {
        return true;
    }
}