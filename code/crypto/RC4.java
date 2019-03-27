package code.crypto;

public class RC4 {
    private int[] S = new int[256];
    RC4(byte[] keys) throws Exception {
        int keyLen = keys.length;
        if (keyLen <1 || keyLen > 256)
            throw new Exception("秘钥长度错误");
        int[] T = new int[256];
        for (int i = 0;i<256;i++){
            S[i] = i;
        }
        int j = 0;
        for (int i = 0;i<256;i++){
            j = (j + S[i] + keys[i % keyLen]) % 256;
            int temp  = S[i];
            S[i] = S[j];
            S[j] = temp;
        }
    }

    public byte[] encrypt(byte[] msgs){
        int i = 0,j = 0;
        byte[] out = new byte[msgs.length];
        for (int k = 0;k<msgs.length;k++){
            i = (i+1)%256;
            j = (j+S[i])%256;
            int temp  = S[i];
            S[i] = S[j];
            S[j] = temp;
            out[k] = (byte) (msgs[k] ^ S[(S[i] + S[j])%256]);
        }
        return out;
    }
}
