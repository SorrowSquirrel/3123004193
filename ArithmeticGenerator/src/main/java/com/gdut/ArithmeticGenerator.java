package com.gdut;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * 主类：处理命令行参数、生成题目、批改答案。
 */
public class ArithmeticGenerator {
    private static final String[] OPS = {"+", "-", "*", "/"};  // 运算符数组

    /**
     * 主方法：解析参数，调用生成或批改。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        int numProblems = 10;  // 默认题目数
        int range = -1;        // 默认范围
        String exerciseFile = null;
        String answerFile = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n") && i + 1 < args.length) {
                numProblems = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-r") && i + 1 < args.length) {
                range = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-e") && i + 1 < args.length) {
                exerciseFile = args[++i];
            } else if (args[i].equals("-a") && i + 1 < args.length) {
                answerFile = args[++i];
            }
        }

        if (exerciseFile != null && answerFile != null) {
            grade(exerciseFile, answerFile);  // 批改模式
            return;
        }

        if (range == -1) {
            System.err.println("错误：生成模式需要 -r 参数。");
            System.err.println("用法：");
            System.err.println("  java ArithmeticGenerator -n <num> -r <range>");
            System.err.println("  java ArithmeticGenerator -e <exercises.txt> -a <answers.txt>");
            return;
        }

        generate(numProblems, range);  // 生成模式
    }

    /**
     * 生成题目：随机树、唯一性检查、输出文件。
     * @param n 题目数
     * @param r 范围
     */
    private static void generate(int n, int r) {
        List<Fraction> leaves = new ArrayList<>();  // 预生成所有可能叶子值
        // 自然数：0 到 r-1
        for (int i = 0; i < r; i++) {
            leaves.add(new Fraction(i, 1));
        }
        // 真分数：分母2~r，分子1~den-1
        for (int den = 2; den <= r; den++) {
            for (int num = 1; num < den; num++) {
                leaves.add(new Fraction(num, den));
            }
        }
        // 混合数：整数1~r-1 + 真分数
        for (int whole = 1; whole < r; whole++) {
            for (int den = 2; den <= r; den++) {
                for (int num = 1; num < den; num++) {
                    leaves.add(new Fraction(whole * den + num, den));
                }
            }
        }

        Set<String> uniqueCanonicals = new HashSet<>();  // 唯一规范Set
        List<Expr> problems = new ArrayList<>();  // 题目列表
        Random rand = new Random();  // 随机数生成器
        int attempts = 0;
        final int MAX_ATTEMPTS = 1000000;  // 最大尝试次数

        while (problems.size() < n && attempts < MAX_ATTEMPTS) {
            attempts++;
            Expr expr = generateExpr(3, leaves, rand);  // 生成树（最多3运算符）
            if (expr.isValid()) {  // 验证有效
                String can = expr.getCanonical();
                if (!uniqueCanonicals.contains(can)) {  // 唯一检查
                    uniqueCanonicals.add(can);
                    problems.add(expr);
                }
            }
        }

        if (problems.size() < n) {
            System.err.println("警告：仅生成 " + problems.size() + " 个唯一题目（目标: " + n + "）。");
        }

        // 输出到文件
        try (PrintWriter exerWriter = new PrintWriter(new FileWriter("Exercises.txt"));
             PrintWriter ansWriter = new PrintWriter(new FileWriter("Answers.txt"))) {
            for (int i = 0; i < problems.size(); i++) {
                Expr e = problems.get(i);
                exerWriter.println((i + 1) + ". " + e.toInfix() + " =");  // 题目格式
                ansWriter.println((i + 1) + ". " + e.eval().toString());   // 答案格式
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("生成 " + problems.size() + " 个题目到 Exercises.txt 和 Answers.txt");
    }

    /**
     * 递归生成表达式树：rem 表示剩余运算符槽。
     * @param rem 剩余运算符数
     * @param leaves 叶子值列表
     * @param rand 随机生成器
     * @return 表达式树
     */
    private static Expr generateExpr(int rem, List<Fraction> leaves, Random rand) {
        if (rem == 0 || rand.nextDouble() < 0.4) {  // 40% 概率或 rem=0 为叶子
            return new Leaf(leaves.get(rand.nextInt(leaves.size())));
        }
        String op = OPS[rand.nextInt(OPS.length)];  // 随机运算符
        int leftRem = rand.nextInt(rem);  // 随机分配左剩余
        int rightRem = rem - 1 - leftRem;  // 右剩余
        Expr left = generateExpr(leftRem, leaves, rand);
        Expr right = generateExpr(rightRem, leaves, rand);
        return new Binary(op, left, right);
    }

    /**
     * 批改功能：解析文件、计算比对、输出 Grade.txt。
     * @param exerFile 题目文件
     * @param ansFile 答案文件
     */
    static void grade(String exerFile, String ansFile) {
        try {
            List<String> exerLines = Files.readAllLines(Paths.get(exerFile));  // 读题目行
            List<String> ansLines = Files.readAllLines(Paths.get(ansFile));    // 读答案行
            List<Integer> correct = new ArrayList<>();  // 正确编号
            List<Integer> wrong = new ArrayList<>();    // 错误编号

            for (int i = 0; i < Math.min(exerLines.size(), ansLines.size()); i++) {
                String exerLine = exerLines.get(i).trim();
                String ansLine = ansLines.get(i).trim();

                // 解析题目：提取 "expr"
                String[] exerParts = exerLine.split("\\.", 2);
                if (exerParts.length < 2) continue;
                String[] exprSplit = exerParts[1].trim().split("=", 2);
                if (exprSplit.length < 1) continue;
                String exprStr = exprSplit[0].trim();

                // 解析答案：提取 "ans"
                String[] ansParts = ansLine.split("\\.", 2);
                if (ansParts.length < 2) continue;
                String ansStr = ansParts[1].trim();

                Fraction expected = Fraction.parse(ansStr);  // 预期答案
                try {
                    Parser parser = new Parser(exprStr);  // 解析字符串到树
                    Expr expr = parser.parse();
                    Fraction computed = expr.eval();  // 计算
                    if (computed.equals(expected)) {
                        correct.add(i + 1);
                    } else {
                        wrong.add(i + 1);
                    }
                } catch (Exception e) {
                    wrong.add(i + 1);  // 解析错误计错
                }
            }

            // 输出 Grade.txt
            try (PrintWriter gradeWriter = new PrintWriter(new FileWriter("Grade.txt"))) {
                String correctStr = correct.isEmpty() ? "" : correct.stream().map(String::valueOf).reduce((a, b) -> a + ", " + b).get();
                String wrongStr = wrong.isEmpty() ? "" : wrong.stream().map(String::valueOf).reduce((a, b) -> a + ", " + b).get();
                gradeWriter.println("Correct: " + correct.size() + " (" + correctStr + ")");
                gradeWriter.println("Wrong: " + wrong.size() + " (" + wrongStr + ")");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 内嵌静态类：递归下降解析器，将中缀字符串解析为 Expr 树。
     */
    static class Parser {
        private String s;  // 输入字符串
        private int pos;   // 当前位置

        /**
         * 构造函数：初始化解析器。
         * @param input 输入字符串
         */
        public Parser(String input) {
            this.s = input;
            this.pos = 0;
        }

        /**
         * 入口：解析全表达式。
         * @return Expr 树
         */
        public Expr parse() {
            return parseAdd();
        }

        /**
         * 解析加减层：Mul { (+|-) Mul }（左结合）。
         * @return 加减表达式
         */
        private Expr parseAdd() {
            Expr e = parseMul();
            while (true) {
                String op = peekNextOp();
                if (op == null || (!op.equals("+") && !op.equals("-"))) break;
                consumeOp(op);
                Expr right = parseMul();
                e = new Binary(op, e, right);
            }
            return e;
        }

        /**
         * 解析乘除层：Atom { (*|/) Mul }（左结合）。
         * @return 乘除表达式
         */
        private Expr parseMul() {
            Expr e = parseAtom();
            while (true) {
                String op = peekNextOp();
                if (op == null || (!op.equals("*") && !op.equals("/"))) break;
                consumeOp(op);
                Expr right = parseMul();
                e = new Binary(op, e, right);
            }
            return e;
        }

        /**
         * 解析原子：数字或 (expr)。
         * @return 原子表达式
         */
        private Expr parseAtom() {
            skipSpaces();
            if (pos >= s.length()) throw new RuntimeException("意外结束");
            if (s.charAt(pos) == '(') {
                pos++;  // 消费 (
                Expr e = parseAdd();
                skipSpaces();
                if (pos < s.length() && s.charAt(pos) == ')') {
                    pos++;  // 消费 )
                } else {
                    throw new RuntimeException("缺少 )");
                }
                return e;
            } else {
                String numStr = parseNumberStr();
                return new Leaf(Fraction.parse(numStr));
            }
        }

        /**
         * 窥视下一个运算符（不消费）。
         * @return op 或 null
         */
        private String peekNextOp() {
            skipSpaces();
            if (pos >= s.length()) return null;
            char ch = s.charAt(pos);
            if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
                return String.valueOf(ch);
            }
            return null;
        }

        /**
         * 消费运算符。
         * @param op 预期 op
         */
        private void consumeOp(String op) {
            skipSpaces();
            if (pos < s.length() && s.charAt(pos) == op.charAt(0)) {
                pos++;
            } else {
                throw new RuntimeException("预期运算符: " + op);
            }
        }

        /**
         * 跳过空格。
         */
        private void skipSpaces() {
            while (pos < s.length() && s.charAt(pos) == ' ') pos++;
        }

        /**
         * 解析数字字符串（包括混合数）。
         * @return 数字字符串
         */
        private String parseNumberStr() {
            skipSpaces();
            StringBuilder sb = new StringBuilder();
            while (pos < s.length()) {
                char ch = s.charAt(pos);
                if (ch == ' ' || ch == ')' || (ch == '+' || ch == '-' || ch == '*' || ch == '/')) {
                    break;
                }
                sb.append(ch);
                pos++;
            }
            if (sb.length() == 0) throw new RuntimeException("预期数字");
            return sb.toString();
        }
    }
}