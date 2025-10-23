package com.gdut;

/**
 * 抽象表达式类：定义表达式树的通用接口。
 */
public abstract class Expr {
    /**
     * 中缀输出：根据优先级添加括号。
     * @param parentPrec 父优先级
     * @param isRightChild 是否右孩子
     * @return 中缀字符串
     */
    public abstract String toInfix(int parentPrec, boolean isRightChild);

    /**
     * 获取规范字符串：用于唯一性检查。
     * @return 规范形式
     */
    public abstract String getCanonical();

    /**
     * 计算表达式值。
     * @return 分数结果
     */
    public abstract Fraction eval();

    /**
     * 验证表达式是否有效（无负数、真分数除法等）。
     * @return true 如果有效
     */
    public abstract boolean isValid();

    /**
     * 默认中缀调用：无父优先级。
     * @return 中缀字符串
     */
    public String toInfix() {
        return toInfix(0, false);
    }
}