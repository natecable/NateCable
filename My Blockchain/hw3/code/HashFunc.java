package code;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;



public class HashFunc {
    public static byte[] getSHA(String get) throws NoSuchAlgorithmException{
        MessageDigest messd = MessageDigest.getInstance("SHA-256");
        return messd.digest(get.getBytes(StandardCharsets.UTF_8));
    }

    public static String toHexString(byte[] hash){
        BigInteger number = new BigInteger(1, hash);
        StringBuilder hexString = new StringBuilder(number.toString(16));
        while (hexString.length() < 64){
            hexString.insert(0, '0');
        }
        return hexString.toString();
    }

    public static String hash(String toHash){
        try{
            return toHexString(getSHA(toHash));
        }catch(Exception e){
            System.out.println("FATAL HASHING ERROR, SHUTTING DOWN");
            System.exit(1);
        }
        return "";
    }
}
