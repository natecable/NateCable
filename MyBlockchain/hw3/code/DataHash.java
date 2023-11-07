package code;

public class DataHash {
        
    //user address on blockchain
    String address;
    
    //user account balance
    int balance;

    //hash String of bothe address and balance
    String hashValue;

    public DataHash(String address, int balance, String hashValue) {
        this.address = address;
        this.balance = balance;
        this.hashValue = hashValue;
    }

    public DataHash(String hashVal){
        this.address = null;
        this.balance = 0;
        this.hashValue = hashVal;
    }

    public String toString(){
        return address + " " + balance + "\n";
    }

    public String getHashVal(){
        return hashValue;
    }

}
