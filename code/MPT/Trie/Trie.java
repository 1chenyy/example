package code.MPT;

import java.util.ArrayList;
import java.util.Stack;


//定义树
public class Trie {
    public static final int SIZE  = 26; //存储英文单词，不区分大小写，共26个字母
    private TrieNode root; //根节点
    Trie(){
        root = new TrieNode();
    }

    //插入字符串
    public void insert(String str){ 
        if (str == null || str.length() == 0)
            return;
        TrieNode node = root;
        char[] words = str.toLowerCase().toCharArray();
        for (int i = 0,len = words.length;i<len;i++){
            int pos = words[i]-'a'; 
            if (node.childs[pos] == null){ //判断节点的某个孩子是否存在
                node.childs[pos] = new TrieNode();
                node.childs[pos].val = words[i];
            }else{
                node.childs[pos].num++; //经过该孩子的字符串加1
            }
            node = node.childs[pos];//访问下一个孩子
        }
        node.isEnd = true;//字符串存储完毕，根节点到该节点表示完整的字符串
    }

    //统计相同前缀的字符串数
    public int countPrefix(String prefix){ 
        if (prefix == null || prefix.length() == 0)
            return -1;
        TrieNode node = root;
        char[] words = prefix.toLowerCase().toCharArray();
        for (int i = 0,len = words.length;i<len;i++){
            int pos = words[i]-'a';
            if (node.childs[pos] == null)
                return 0;
            else
                node = node.childs[pos];
        }
        return node.num; 
    }

    //获取相同前缀的所有字符串
    public ArrayList<String> print(String prefix){
        if (prefix == null || prefix.length() == 0)
            return null;
        TrieNode node = root;
        char[] words = prefix.toLowerCase().toCharArray();
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0,len = words.length;i<len;i++){
            int pos = words[i]-'a';
            if (node.childs[pos] == null)
                return null;
            else
                node = node.childs[pos];
        }
        traverse(node,prefix,result);
        return result;
    }

    //递归遍历某个节点所有孩子组成的字符串
    public void traverse(TrieNode node,String prefix,ArrayList<String> result){
        if (!node.isEnd){
            for (TrieNode child : node.childs)
                if (child!=null)
                    traverse(child,prefix+child.val,result);
        }else{
            result.add(prefix);
        }
    }

    //判断某个字符串是否存在
    public boolean has(String str){
        if (str == null || str.length() == 0)
            return false;
        TrieNode node = root;
        char[] words = str.toLowerCase().toCharArray();
        for (int i = 0,len = words.length;i<len;i++){
            int pos = words[i]-'a';
            if (node.childs[pos]!=null){
                node = node.childs[pos];
            }else{
                return false;
            }
        }
        return node.isEnd;
    }
}