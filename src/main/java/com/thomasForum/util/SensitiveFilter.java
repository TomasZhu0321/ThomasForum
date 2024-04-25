package com.thomasForum.util;

import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);
    //Replacement Character
    private static final String REPLACEMENT = "***";
    //Root Node
    private TrieNode root = new TrieNode();

    //Construct Tire Data structure
    private class TrieNode{
        private boolean isEndOfWord = false;
        private Map<Character, TrieNode> subNodes = new HashMap<>();
        public boolean isEndOfWord(){
            return isEndOfWord;
        }
        public void setEndOfWord(boolean endOfWord) {
            isEndOfWord = endOfWord;
        }
        public void addSubNode(Character c, TrieNode node){
            subNodes.put(c,node);
        }
        public TrieNode getSubNode(Character c){
            return subNodes.get(c);
        }
    }
    @PostConstruct
    public void init(){
        try( InputStream in = this.getClass().getClassLoader().getResourceAsStream("sensitive_words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(in));){
            String sensitiveWord;
            while((sensitiveWord = reader.readLine())!=null){
                this.addSensitiveWords(sensitiveWord);
            }
        }catch (IOException e){
            logger.error("Can't get sensitive words" , e);
        }
    }
    /**
     * Filter Sensitive Words
     * @param text Text needing to filter
     * @return filterd text
     */
    public String filter(String text){
        if(StringUtils.isBlank(text)){
            return null;
        }
        //pointer 1: Trie
        TrieNode pointer1 = root;
        //pointer 2: low index
        int low = 0;
        //pointer 3: fast index
        int fast = 0;
        StringBuilder sb = new StringBuilder();
        while(fast <= text.length() - 1){
            // judge if is special character in between
            if (isSymbol(text.charAt(fast))) {
                if (pointer1 == root) {
                    sb.append(text.charAt(fast));
                    low ++;
                }
                fast ++;
                continue;
            }
            pointer1 = pointer1.getSubNode(text.charAt(fast));
            if(pointer1 ==null) { //is not sensitive word
                sb.append(text.charAt(low));
                low = low + 1;
                fast = low;
                pointer1 = root;
            }else if(pointer1.isEndOfWord){ //is sensitive word and come to end
                sb.append(this.REPLACEMENT);
                low = fast + 1;
                fast = low;
                pointer1 = root;
            }else { //haven't done sensitive traversal
                fast ++;
            }
        }
        sb.append(text.substring(low));
        return sb.toString();
    }

    // add a sensitive word to Tire.
    private void addSensitiveWords(String sensitiveWords){
        TrieNode tmpNode = root;
        for(int i = 0; i < sensitiveWords.length(); i ++){
            char c = sensitiveWords.charAt(i);
            TrieNode subNode = tmpNode.getSubNode(c);
            if(subNode == null){
                subNode = new TrieNode();
                tmpNode.addSubNode(c, subNode);
            }
            //pointer, point the subNode
            tmpNode = subNode;
            if (i == sensitiveWords.length() - 1){
                tmpNode.setEndOfWord(true);
            }
        }
    }
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
}
