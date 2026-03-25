public class DruidFinalTest {
    public static void main(String[] args) throws Exception {
        String password = "xxxxxxxxxxxxxx";
        String[] keyPair = com.alibaba.druid.filter.config.ConfigTools.genKeyPair(512);
        String privateKey = keyPair[0];
        String publicKey = keyPair[1];
        String cipherText = com.alibaba.druid.filter.config.ConfigTools.encrypt(privateKey, password);
        System.out.println("password: " + cipherText);
        System.out.println("publicKey: " + publicKey);
    }
}