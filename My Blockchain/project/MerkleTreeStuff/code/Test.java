package code;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.ArrayList;

public class Test {

    public static void main(String[] args){
        ArrayList<DataHash> dataList = new ArrayList<>();
        Scanner s = new Scanner(System.in);
        System.out.print("Enter the full path to the file to scan: ");
        String fPath = s.next();
        s.close();
        try {
            File dataText = new File(fPath);
            Scanner myReader = new Scanner(dataText);

            // while loop to read from text
            // split to get address and balance, then get hash value of those two add up
            // cerate a DataHash object and save this in the ArrayList of DataHash
            while (myReader.hasNextLine()) {
                String temp = myReader.nextLine();
                String[] line = temp.split(" ");
                String hashvalue = HashFunc.hash(temp);
                DataHash datahash = new DataHash(line[0],Integer.parseInt(line[1]),hashvalue);
                dataList.add(datahash);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        MerkleTree tree = new MerkleTree(dataList);
        System.out.println("Merkle Root:  " + tree.getRoot());
    }

}
