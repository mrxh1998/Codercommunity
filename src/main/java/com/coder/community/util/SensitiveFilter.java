package com.coder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    TreeNode root = new TreeNode();
    private static final String REPLACE = "***";
    @PostConstruct
    public void init(){
        try (
                InputStream resourceStream = this.getClass().getClassLoader().getResourceAsStream("SensitiveWords.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream));
                ) {
            String keyword;
            while((keyword = reader.readLine())!=null){
                this.addKeyWord(keyword);
            }
        }catch (IOException e) {
            logger.error("加载敏感词文件失败"+e.getMessage());
        }
    }
    //敏感词过滤
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        StringBuilder newText = new StringBuilder();
        TreeNode tree = root;
        int i = 0;
        int j = 0;
        while(i < text.length()){
            char c = text.charAt(i);
            if(tree.getTreeNode(c) == null){
                i++;
                newText.append(c);;
            }
            else{
                j = i;
                while(j < text.length()){
                    while(j < text.length()&&isSymbol(text.charAt(j))){
                        j++;
                    }
                    if((tree = tree.getTreeNode(text.charAt(j))) == null){
                        break;
                    }
                    if(tree.isKeyWordEnd()){
                        newText.append(REPLACE);
                        i = j + 1;
                        tree = root;
                        break;
                    }
                    else{
                        j++;
                    }
                }
                if(j >= text.length()||tree==null){
                    newText.append(c);
                    i++;
                    tree = root;
                }
            }
        }
        return newText.toString();
    }
    public boolean isSymbol(char c){
        return (c<0x2E80||c > 0x9FFF);
    }
    //添加敏感词
    private void addKeyWord(String keyword){
        TreeNode temp = root;
        for(int i = 0; i < keyword.length();i++){
            char c = keyword.charAt(i);
            TreeNode treeNode = temp.getTreeNode(c);
            if(treeNode == null){
                treeNode = new TreeNode();
                temp.addChild(c,treeNode);
            }
            temp = treeNode;;
            if(i == keyword.length()-1){
                temp.setKeyWordEnd(true);
            }
        }
    }
}

class TreeNode{
    private boolean isKeyWordEnd = false;
    private Map<Character,TreeNode> child = new HashMap<>();

    public boolean isKeyWordEnd() {
        return isKeyWordEnd;
    }

    public void setKeyWordEnd(boolean keyWordEnd) {
        isKeyWordEnd = keyWordEnd;
    }

    public void addChild(Character key,TreeNode child){
        this.child.put(key,child);
    }
    public TreeNode getTreeNode(Character key){
        return this.child.get(key);
    }
}