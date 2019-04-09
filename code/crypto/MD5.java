public class MD5 {
    public String md5(String msg){
        byte[] msgBytes = msg.getBytes();
        int msgBytesLen = msgBytes.length;//原始信息长度，单位bit
        int numBlock = ((msgBytesLen + 8)>>>6) + 1; //总块数=(原始长度+8bit)/64bit + 1
        int totalLen = numBlock << 6; //补全后的长度 = 块数 * 64bit
        byte[] padBytes = new byte[totalLen - msgBytesLen]; //需要补的bit数
        padBytes[0] = (byte) 0x80; //补一个1和若干个0
        long msgLen = (long)msgBytesLen << 3; //计算出多少bit，长度*8
        for(int i = 0;i<8;i++){
            padBytes[padBytes.length - 8 + i] = (byte) msgLen;//从低位开始写入长度
            msgLen >>>= 8;
        }
        int a = A,b =B,c = C,d = D;
        int[] buffer  = new int[16]; //每块512位
        for (int i = 0;i<numBlock;i++){
            int index = i << 6;
            for (int j = 0;j<64;j++,index++)
                //index是msg的游标，j是buffer的游标，一个int类型存4个byte类型
                //从低位开始存
                buffer[j >>> 2] = ((int) ((index < msgBytesLen) ? msgBytes[index]
                        : padBytes[index - msgBytesLen]) << 24)
                        | (buffer[j >>> 2] >>> 8);
            int tempa = a; //记录abcd的临时变量
            int tempb = b;
            int tempc = c;
            int tempd = d;
            for (int j = 0;j<64;j++) { //64小轮
                int pType = j >>> 4; //判断是第几大轮
                int f = 0; //P运算的值
                int bufferIndex = j; //buffer的游标
                switch (pType){
                    case 0:
                        f = (b &c) | (~b & d);
                        break;
                    case 1:
                        f = (b & d) | (c & ~d);
                        bufferIndex = (bufferIndex*5+1)&0x0f; //明文子组和轮数的关系
                        break;
                    case 2:
                        f = b ^ c ^ d;
                        bufferIndex = (bufferIndex * 3 + 5) & 0x0F;
                        break;
                    case 3:
                        f = c ^ (b | ~d);
                        bufferIndex = (bufferIndex * 7) & 0x0F;
                        break;
                }
                //运算
                int temp = b + Integer.rotateLeft(a + f + buffer[bufferIndex] + T[j], S[(pType << 2) | (j & 3)]);
                //交换abcd
                a = d;
                d = c;
                c = b;
                b = temp;
            }
            //分组处理完之后abcd各自累加
            a += tempa;
            b += tempb;
            c += tempc;
            d += tempd;
        }
        //abcd写到16个byte中
        byte[] out = new byte[16];
        int count = 0;
        for (int i = 0;i<4;i++){
            int n = (i == 0) ? a : ((i == 1) ? b : ((i == 2) ? c : d));
            for (int j = 0;j<4;j++){
                out[count++] = (byte)n;
                n>>>=8;
            }
        }
        //转为十六进制表示
        StringBuffer sb = new StringBuffer();
        for (byte bout : out){
            sb.append(HEX[(bout>>>4)&0xf]);
            sb.append(HEX[bout&0xf]);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        MD5 md5 = new MD5();
        System.out.println(md5.md5(""));
        System.out.println(md5.md5("a"));
        System.out.println(md5.md5("abc"));
        System.out.println(md5.md5("12345678901234567890123456789012345678901234567890123456789012345678901234567890"));
    }

    public static final int A = 0x67452301;
    public static final int B = 0xefcdab89;
    public static final int C = 0x98badcfe;
    public static final int D = 0x10325476;
    private static final int T[] = {
            0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee,
            0xf57c0faf, 0x4787c62a, 0xa8304613, 0xfd469501, 0x698098d8,
            0x8b44f7af, 0xffff5bb1, 0x895cd7be, 0x6b901122, 0xfd987193,
            0xa679438e, 0x49b40821, 0xf61e2562, 0xc040b340, 0x265e5a51,
            0xe9b6c7aa, 0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8,
            0x21e1cde6, 0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905,
            0xfcefa3f8, 0x676f02d9, 0x8d2a4c8a, 0xfffa3942, 0x8771f681,
            0x6d9d6122, 0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60,
            0xbebfbc70, 0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05,
            0xd9d4d039, 0xe6db99e5, 0x1fa27cf8, 0xc4ac5665, 0xf4292244,
            0x432aff97, 0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92,
            0xffeff47d, 0x85845dd1, 0x6fa87e4f, 0xfe2ce6e0, 0xa3014314,
            0x4e0811a1, 0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
    };
    /* 常量T的计算
    private static final int[] T = new int[64];
    static
    {
        for (int i = 0; i < 64; i++)
            T[i] = (int) (long) ((1L << 32) * Math.abs(Math.sin(i + 1)));
    }
    */
    public static final int S[] = new int[]{
            7, 12, 17, 22, 5, 9, 14, 20, 4,
            11, 16, 23, 6, 10, 15, 21
    };
    public static final String[] HEX = new String[]{
            "0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"
    };
}
