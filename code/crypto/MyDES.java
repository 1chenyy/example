package code.crypto;

public class MyDES {
    private long[] encryptKeys = new long[16];
    private long[] decryptKeys = new long[16];
    MyDES(byte[] bytes) throws Exception {
        if (bytes.length!=8)
            throw new Exception("密钥长度错误");
        generateSubKey(bytes);
    }

    public byte[] encrypt(byte[] bytes) throws Exception {
        if(bytes.length != 8)
            throw new Exception("输入块长度错误");
        return des(bytes,encryptKeys);
    }

    public byte[] decrypt(byte[] bytes) throws Exception {
        if(bytes.length != 8)
            throw new Exception("输入块长度错误");
        return des(bytes,decryptKeys);
    }

    private byte[] des(byte[] bytes,long[] subkeys){
        long msg = bytesToLong(bytes);
        long IPResult = permute(IP,msg,64);
        int l = (int) (IPResult >>> 32);
        int r = (int) (IPResult & 0xffffffff);
        int temp = 0;
        for (int i = 0;i<16;i++){
            temp = r;
            r = l ^ f(r, subkeys[i]);
            l = temp;
        }

        long FPResult = permute(FP,((r & 0xffffffffL) << 32) | (l & 0xffffffffL),64);
        return longTobytes(FPResult);
    }

    private byte[] longTobytes(long src) {
        byte[] result = new byte[8];
        for (int i = 0; i<8;i++){
            result[i] = (byte) ((src >> (64 - i*8 - 8)) & 0xffL);
        }
        return result;
    }

    private int f(int src, long subkey) {
        long rExpand = permute(EXPAND,src&0xffffffffL,32);
        long sIn = rExpand ^ subkey;
        long sOut = sBox(sIn);
        int pResult = (int) permute(P,sOut,32);
        return pResult;
    }

    private long sBox(long sIn) {
        long result = 0;
        int r = 0,c = 0;
        for (int i = 0; i< 8;i++){
            byte input = (byte) (sIn & 0x3f);
            r = ((input & 0x20)>>>4) | (input &0x1);
            c = (input & 0x1e) >>> 1;
            result |= (S[7-i][r][c] & 0xffL) << (i*4);
            sIn >>>= 6;
        }
        return result;
    }

    private void generateSubKey(byte[] keyBytes) {
        long key = bytesToLong(keyBytes);
        long pc1Result = permute(PC1,key,64);
        int l = (int) (pc1Result >>> 28);
        int r = (int) (pc1Result & 0xfffffff);

        for (int i = 0; i<16;i++){
            l = (l << KEY_ROTATE[i]) | (l >>> (28-KEY_ROTATE[i]));
            r = (r << KEY_ROTATE[i]) | (r >>> (28-KEY_ROTATE[i]));
            long temp = ((l & 0xfffffffL) << 28) | (r & 0xfffffffL);
            encryptKeys[i] = permute(PC2,temp,56);
            decryptKeys[15-i] = encryptKeys[i];
        }
    }

    private long permute(int[] table, long src,int size) {
        long result = 0L;
        int len = table.length;
        for(int i = 0; i<len;i++){
            result = (result << 1) | (src >>> (size - table[i]) & 0x1);
        }
        return result;
    }

    private long bytesToLong(byte[] keyBytes) {
        long result = 0L;
        int len  = keyBytes.length;
        for (int i = 0;i<len;i++){
            result |= (keyBytes[i] & 0xffL) << (len*8 - i*8 -8);
        }
        return result;
    }


    public static void main(String[] args) throws Exception {
        MyDES myDES = new MyDES("12345678".getBytes());
        byte[] enc = myDES.encrypt("abcdefgh".getBytes());
        System.out.println(byteToHexString(enc));
        byte[] dec = myDES.decrypt(enc);
        System.out.println(new String(dec));
    }

    private static String byteToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes){
            sb.append(HEXTABLE[(b>>>4)&0xf]);
            sb.append(HEXTABLE[b&0xf]);
        }
        return sb.toString();
    }

    private static final int[] PC1 = {
            57, 49, 41, 33, 25, 17, 9, 1,
            58, 50, 42, 34, 26, 18, 10, 2,
            59, 51, 43, 35, 27, 19, 11, 3,
            60, 52, 44, 36, 63, 55, 47, 39,
            31, 23, 15, 7, 62, 54, 46, 38,
            30, 22, 14, 6, 61, 53, 45, 37,
            29, 21, 13, 5, 28, 20, 12, 4
    };

    private static final int[] PC2 = {
            14, 17, 11, 24, 1, 5, 3, 28,
            15, 6, 21, 10, 23, 19, 12, 4,
            26, 8, 16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55, 30, 40,
            51, 45, 33, 48, 44, 49, 39, 56,
            34, 53, 46, 42, 50, 36, 29, 32
    };

    private static final int[] KEY_ROTATE = {
        1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1
    };

    private static final int[] IP = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
    };

    private static final int[] FP = {
            40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25
    };

    private static final int[] EXPAND = {
            32, 1, 2, 3, 4, 5, 4, 5,
            6, 7, 8, 9, 8, 9, 10, 11,
            12, 13, 12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21, 20, 21,
            22, 23, 24, 25, 24, 25, 26, 27,
            28, 29, 28, 29, 30, 31, 32, 1
    };

    private static final int[][][] S = {
            {
                    {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7},
                    {0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8},
                    {4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0},
                    {15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13}
            },
            {
                    {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10},
                    {3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5},
                    {0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15},
                    {13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9},
            },
            {
                    {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8},
                    {13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1},
                    {13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7},
                    {1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12},
            },
            {
                    {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15},
                    {13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9},
                    {10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4},
                    {3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14},
            },
            {
                    {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
                    {14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6},
                    {4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14},
                    {11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3},
            },
            {
                    {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
                    {10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8},
                    {9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6},
                    {4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13},
            },
            {
                    {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
                    {13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6},
                    {1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2},
                    {6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12},
            },
            {
                    {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7},
                    {1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2},
                    {7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8},
                    {2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11},
            },
    };

    private static final int[] P = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25
    };

    private static final String[] HEXTABLE = new String[]{
            "0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"
    };
}
