// public class DataHash {
        
//     //user address on blockchain
//     String address;
    
//     //user account balance
//     int balance;

//     //hash String of bothe address and balance
//     String hashValue;

//     public DataHash(String address, int balance, String hashValue) {
//         this.address = address;
//         this.balance = balance;
//         this.hashValue = hashValue;
//     }

//     public DataHash(String hashVal){
//         this.address = null;
//         this.balance = 0;
//         this.hashValue = hashVal;
//     }

//     public String toString(){
//         return address + "\n" + balance + "\n" + hashValue + "\n";
//     }

//     public String getHashVal(){
//         return hashValue;
//     }

// }

// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.math.BigInteger;
// import java.nio.charset.StandardCharsets;



// public class HashFunc {
//     public static byte[] getSHA(String get) throws NoSuchAlgorithmException{
//         MessageDigest messd = MessageDigest.getInstance("SHA-256");
//         return messd.digest(get.getBytes(StandardCharsets.UTF_8));
//     }

//     public static String toHexString(byte[] hash){
//         BigInteger number = new BigInteger(1, hash);
//         StringBuilder hexString = new StringBuilder(number.toString(16));
//         while (hexString.length() < 64){
//             hexString.insert(0, '0');
//         }
//         return hexString.toString();
//     }

//     public static String hash(String toHash){
//         try{
//             return toHexString(getSHA(toHash));
//         }catch(Exception e){
//             System.out.println("FATAL HASHING ERROR, SHUTTING DOWN");
//             System.exit(1);
//         }
//         return "";
//     }
// }


// public class Leaf{
//     //Leaf Structure
//     private Leaf left;
//     private Leaf right;
//     private DataHash data;

//     public Leaf(Leaf l, Leaf r, DataHash data) {
//         this.left = l;
//         this.right = r;
//         this.data = data;
//     }

//     public Leaf getLeft() {
//         return left;
//     }

//     public Leaf getRight() {
//         return right;
//     }

//     public void setLeft(Leaf l) {
//             this.left = l;
//         }

//     public void setRight(Leaf r) {
//         this.right = r;
//     }

//     public DataHash getData() {
//         return data;
//     }
    
//     public void setData(DataHash data){
//         this.data = data;
//     }

//     public String getHash(){
//         return data.getHashVal();
//     }
// }

// public class MerkleTree {

//     private Leaf root;

//     public MerkleTree(ArrayList<DataHash> data){
//         this.root = generateTree(data);
//     }

    

//     private static Leaf generateTree(ArrayList<DataHash> data) {

//         ArrayList<Leaf> children = new ArrayList<>();

//         for (DataHash d : data) {
//             children.add(new Leaf(null, null, d));
//         }




//         ArrayList<Leaf> par = new ArrayList<>();

//         while (children.size() != 1) {
//             int index = 0;
//             int length = children.size();
//             while (index < length) {
//                 Leaf leftChild = children.get(index);
//                 Leaf rightChild = null;

//                 if ((index + 1) < length) {
//                     rightChild = children.get(index + 1);
//                 } else {
//                     rightChild = new Leaf(null, null, leftChild.getData());
//                 }

//                 DataHash parentHash = new DataHash(HashFunc.hash(leftChild.getHash() + rightChild.getHash()));
//                 par.add(new Leaf(leftChild, rightChild, parentHash));
//                 index += 2;
//             }
//             children = par;
//             par= new ArrayList<>();
//         }
//         return children.get(0);
//     }

//     public String getRoot(){
//         return root.getHash();
//     }

// }

// import java.io.File;  // Import the File class
// import java.io.FileNotFoundException;  // Import this class to handle errors
// import java.util.Scanner; // Import the Scanner class to read text files
// import java.util.ArrayList;

// public class Test {

//     public static void main(String[] args){
//         ArrayList<DataHash> dataList = new ArrayList<>();
//         Scanner s = new Scanner(System.in);
//         System.out.print("Enter the full path to the file to scan: ");
//         String fPath = s.next();
//         s.close();
//         try {
//             File dataText = new File(fPath);
//             Scanner myReader = new Scanner(dataText);

//             // while loop to read from text
//             // split to get address and balance, then get hash value of those two add up
//             // cerate a DataHash object and save this in the ArrayList of DataHash
//             while (myReader.hasNextLine()) {
//                 String temp = myReader.nextLine();
//                 String[] line = temp.split(" ");
//                 String hashvalue = HashFunc.hash(temp);
//                 DataHash datahash = new DataHash(line[0],Integer.parseInt(line[1]),hashvalue);
//                 dataList.add(datahash);
//             }
//             myReader.close();
//         } catch (FileNotFoundException e) {
//             System.out.println("An error occurred.");
//             e.printStackTrace();
//         }

//         MerkleTree tree = new MerkleTree(dataList);
//         System.out.println("Merkle Root:  " + tree.getRoot());
//     }

// }
