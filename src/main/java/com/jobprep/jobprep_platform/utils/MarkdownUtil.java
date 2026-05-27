package com.jobprep.jobprep_platform.utils;

public class MarkdownUtil {
    public static boolean needCollapsed(String markdown){
        MarkdownAST ast = new MarkdownAST(markdown);
        return ast.shouldCollapse(250);

    }
    public static String extractIntroduction(String markdown){
        MarkdownAST ast = new MarkdownAST(markdown);
        return ast.extractIntroduction(250);
    }
}
