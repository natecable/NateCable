package code;

import java.math.*;

public class Block {
    
    public String preHead;
    public String root;
    public long timestamp;
    private String diffTarget;
    public int nonce;
    public String headerHash;

    public Block(String preHead, String root,int nonce){
        this.preHead = preHead;
        this.root = root;
        this.timestamp = System.currentTimeMillis() /1000L;
        this.diffTarget = findTarget("0.5");
        this.nonce = nonce;
        String check = nonce + root;
        String check_hash = HashFunc.hash(check);
        if (diffTarget.compareTo(check_hash) == 1 ||diffTarget.compareTo(check_hash) == 0 ){
            this.headerHash = hashHeader();
        }else{
            this.headerHash = null;
        }
    }

    public String getPreHead(){
        return preHead;
    }

    public String getRoot(){
        return root;
    }

    public long getTimestamp(){
        return timestamp;
    }

    public int getNonce(){
        return nonce;
    }

    public String getHeaderHash(){
        return headerHash;
    }

    public boolean setNonce(int nonce){
        this.nonce = nonce;
        String check = nonce + root;
        String check_hash = HashFunc.hash(check);
        if (diffTarget.compareTo(check_hash) == 1 ||diffTarget.compareTo(check_hash) == 0 ){
            this.headerHash = hashHeader();
            return true;
        }else{
            this.headerHash = null;
            return false;
        }
    }

    private String hashHeader(){
        String stringToHash = preHead + root  + Long.toString(timestamp) 
            + diffTarget + Integer.toString(nonce);
        String hashvalue = HashFunc.hash(stringToHash);
        return hashvalue;
    }

    private String findTarget(String  p_of_success){
        BigInteger max = new BigInteger("ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff",16);
        BigDecimal max_decimal = new BigDecimal(max);
        BigDecimal succ_rate =  new BigDecimal(p_of_success);
        BigDecimal result = max_decimal.multiply(succ_rate);
        BigInteger bounde = result.toBigInteger();
        String base16 = bounde.toString(16);
        return base16;
    }

    public String toString(){
        String out = "\tHash of previous block: " + preHead + "\n" + "\tRoot of the Merkle tree " 
            + root + "\n" + "\tTtimestamp: " + timestamp + "\n" + "\tDifficulty target: " 
            + diffTarget + "\n" + "\tNonce: " +nonce;
        return out;
    }

}