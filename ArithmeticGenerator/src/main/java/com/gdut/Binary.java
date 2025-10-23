package com.gdut;

/**
 * 二元运算节点类：表示运算符连接的子表达式。
 */
public class Binary extends Expr {
    private String op;  // 运算符：+ - * /
    private Expr left;  // 左子表达式
    private Expr right; // 右子表达式

    /**
     * 构造函数：创建二元节点。
     * @param op 运算符
     * @param left 左子树
     * @param right 右子树
     */
    public Binary(String op, Expr left, Expr right) {
        this.op = op;
        this.left = left;
        this.right = right;
    }

    /**
     * 私有方法：获取优先级（+ - :1, * / :2）。
     * @return 优先级
     */
    private int getPrec() {
        if (op.equals("+") || op.equals("-")) return 1;
        return 2;
    }

    /**
     * 私有方法：是否可交换（+ * 是）。
     * @return true 如果可交换
     */
    private boolean isComm() {
        return op.equals("+") || op.equals("*");
    }

    /**
     * 中缀输出：递归子树，添加括号。
     */
    @Override
    public String toInfix(int parentPrec, boolean isRightChild) {
        int myPrec = getPrec();
        String leftStr = left.toInfix(myPrec, false);
        String rightStr = right.toInfix(myPrec, true);
        String inner = leftStr + " " + op + " " + rightStr;
        boolean needParen = (myPrec < parentPrec) || (myPrec == parentPrec && isRightChild);
        return needParen ? "(" + inner + ")" : inner;
    }

    /**
     * 规范形式：递归 + 全括号 + 交换排序（如果可交换）。
     */
    @Override
    public String getCanonical() {
        String leftCan = left.getCanonical();
        String rightCan = right.getCanonical();
        String opStr = " " + op + " ";
        String s = "(" + leftCan + opStr + rightCan + ")";
        if (isComm()) {
            String alt = "(" + rightCan + opStr + leftCan + ")";
            return s.compareTo(alt) < 0 ? s : alt;
        }
        return s;
    }

    /**
     * 计算值：递归子树后运算。
     */
    @Override
    public Fraction eval() {
        Fraction l = left.eval();
        Fraction r = right.eval();
        switch (op) {
            case "+": return l.add(r);
            case "-": return l.subtract(r);
            case "*": return l.multiply(r);
            case "/": return l.divide(r);
            default: throw new IllegalArgumentException("未知运算符: " + op);
        }
    }

    /**
     * 验证有效性：递归子树 + 检查减法无负、除法真分数。
     */
    @Override
    public boolean isValid() {
        if (!left.isValid() || !right.isValid()) return false;
        Fraction lVal = left.eval();
        Fraction rVal = right.eval();
        if (op.equals("-")) {
            return lVal.greaterOrEqual(rVal);
        } else if (op.equals("/")) {
            if (rVal.isZero()) return false;
            Fraction quot = lVal.divide(rVal);
            return !quot.isInteger();
        }
        return true;
    }
}