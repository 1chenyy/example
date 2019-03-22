package code.MPT;

//定义节点
public class TrieNode {
    public int num ; //经过该节点的有效字符串数量
    public TrieNode[] childs; //节点的孩子
    //是否是最后一个节点，若是表示从根节点到该节点表示一个完整的字符串，否则仅仅是一个前缀
    public boolean isEnd; 
    public char val; //该节点表示的字母
    TrieNode(){
        num=1;
        childs = new TrieNode[Trie.SIZE];
        isEnd = false;
    }
}