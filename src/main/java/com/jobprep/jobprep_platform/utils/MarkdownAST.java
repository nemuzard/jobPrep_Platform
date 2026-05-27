package com.jobprep.jobprep_platform.utils;

import java.util.ArrayList;
import java.util.List;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.Node;
import lombok.Getter;

@Getter
public class MarkdownAST {
    private final Document markdownAST;
    private final String markdownText;

    // init and parse markdown file
    public MarkdownAST(String markdownText){
        this.markdownText = markdownText;
        Parser parser = Parser.builder().build();
        this.markdownAST = parser.parse(markdownText);
    }

    // extract info
    public String extractIntroduction(int maxChars){
        var introText = new StringBuilder();
        for (Node node:markdownAST.getChildren()){
            if(node instanceof Heading || node instanceof Paragraph){
                String renderedText = getNodeText(node);
                int remainingChars = maxChars-introText.length();
                introText.append(renderedText,0,Math.min(remainingChars, renderedText.length())); 
                if (introText.length()>=maxChars){
                    break;
                }
            }
        }
        return introText.toString().trim()+"...";
    }

    //check if contains image url, if yes, return it
    public List<String> extractImages(){
        List<String> imageUrls = new ArrayList<>();
        for(Node node:markdownAST.getChildren()){
            if(node instanceof Image imageNode){
                imageUrls.add(imageNode.getUrl().toString());
            }
        
        }
        return imageUrls;
    }

    public boolean shouldCollapse(int maxChars){
        return hasImages()||markdownText.length()>maxChars;
    }
    private boolean hasImages(){
        return !extractImages().isEmpty();
    }

    public String getCollapsedMarkdown(){
        String introText = extractIntroduction(150);
        return introText+"...";
    }

    // get node text 
    private String getNodeText(Node node){
        var text = new StringBuilder();
        if(node instanceof Text){
            text.append(((Text) node).getChars());
        }

        for (Node child:node.getChildren()){
            text.append(getNodeText(child));
        }
        return text.toString();
    }

    //get heading
    public String getHeadingText(Heading headingNode){
        return headingNode.getText().toString().trim();
    }
    public String getListItemText(ListItem listItem){
        var sb = new StringBuilder();
        for (Node node = listItem.getFirstChild(); node != null; node = node.getNext()) {
            sb.append(node.getChars().toString());
        }
        return sb.toString().trim();
    }
}
