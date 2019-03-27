package code.crypto;

import java.util.ArrayList;

public class PlayFair {
    char[][] table = new char[5][5]; //密码表

    PlayFair(String key){
        generateTable(key);//根据关键字生成密码表
    }

    private void generateTable(String key){
        key = key.replaceAll(" ","").toUpperCase();
        char[] keys = key.toCharArray();
        int count = 0;
        char alphabet = 'A';
        ArrayList<Character> list = new ArrayList<>();
        for (int i = 0;i<5;i++){
            for (int j = 0;j<5;j++){
                while(count < keys.length && list.contains(keys[count])){ //寻找关键字中不重复的字母
                    count++;
                }
                if (count < keys.length){
                    table[i][j] = keys[count];
                    list.add(keys[count]);
                    count++;
                }else{
                    while (alphabet <= 'Z' && (list.contains(alphabet) || alphabet == 'J')){ //按顺序从字母表中填充
                        alphabet++;
                    }
                    table[i][j] = alphabet;
                    alphabet++;
                }
            }
        }
    }

    public String encryption(String msg){ //加密算法
        msg = msg.replaceAll(" ","").toUpperCase();
        char[] msgs = msg.toCharArray();
        StringBuffer result = new StringBuffer();
        for (int i = 0;i<msgs.length;i++){
            char a = msgs[i];//获取分组第一个字母
            i++;
            char b;
            if (i<msgs.length){ //判读是否越界
                if (msgs[i] == a){ //是否重复
                    if (a == 'X'){ //若是X重复，添加Q
                        b = 'Q';
                    }else{
                        b = 'X';
                    }
                    i--;
                }else{
                    b = msgs[i];
                }
            }else{ //越界，就是最后只剩一个字母
                if (a == 'X'){ //若最后一个是X，补Q
                    b = 'Q';
                }else{
                    b = 'X';
                }
            }
            int[] locA = find(a); //寻找分组第一个字母位置
            int[] locB = find(b); //寻找分组第二个字母位置
            if(locA[0] == locB[0]){ //若在同一行
                a = locA[1]+1<5?table[locA[0]][locA[1]+1]:table[locA[0]][0];
                b = locB[1]+1<5?table[locB[0]][locB[1]+1]:table[locB[0]][0];
            }else if(locA[1] == locB[1]){ //若在同一列
                a = locA[0]+1<5?table[locA[0]+1][locA[1]]:table[0][locA[1]];
                b = locB[0]+1<5?table[locB[0]+1][locB[1]]:table[0][locB[1]];
            }else{ //不在同一行同一列，行替换
                a = table[locA[0]][locB[1]];
                b = table[locB[0]][locA[1]];
            }
            result.append(a);
            result.append(b);
        }
        return result.toString();
    }

    public String decrypt(String msg){ //解密算法
        msg = msg.replaceAll(" ","").toUpperCase();
        char[] msgs = msg.toCharArray();
        if (msgs.length%2!=0){ //密文不是偶数个，报错
            return "error: The length of ciphertext is odd";
        }
        StringBuffer result = new StringBuffer();
        for (int i = 0;i<msgs.length;i++){
            char a = msgs[i];
            i++;
            char b = msgs[i];
            int[] locA = find(a);//寻找分组第一个字母位置
            int[] locB = find(b);//寻找分组第二个字母位置
            if(locA[0] == locB[0]){ //若在同一行
                a = locA[1]-1>-1?table[locA[0]][locA[1]-1]:table[locA[0]][4];
                b = locB[1]-1>-1?table[locB[0]][locB[1]-1]:table[locB[0]][4];
            }else if(locA[1] == locB[1]){ //若在同一列
                a = locA[0]-1>-1?table[locA[0]-1][locA[1]]:table[4][locA[1]];
                b = locB[0]-1>-1?table[locB[0]-1][locB[1]]:table[4][locB[1]];
            }else{ //不在同一行同一列
                a = table[locA[0]][locB[1]];
                b = table[locB[0]][locA[1]];
            }
            result.append(a);
            result.append(b);
        }
        return result.toString();
    }

    private int[] find(char c){ //寻找字母在表中位置
        if (c == 'J')//对于J当做I处理
            c = 'I';
        for (int i = 0;i<5;i++){
            for (int j = 0;j<5;j++){
                if (table[i][j] == c)
                    return new int[]{i,j};
            }
        }
        return new int[]{-1,-1};
    }

    public static void main(String[] args) {//测试代码
        PlayFair p = new PlayFair("PLAYFAIREXAMPLE");
        String msg = p.encryption("MYNAMEISTOM");
        System.out.println("ciphertext：" + msg);
        System.out.println("plaintext：" + p.decrypt(msg));
    }
}
