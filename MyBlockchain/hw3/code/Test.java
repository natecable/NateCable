package code;

import java.io.File;  // Import the File class
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class Test {

    public static void main(String[] args){
        MerkleTree treeTest = null;
        ArrayList<Block> blockchain = new ArrayList<>();
        ArrayList<MerkleTree> merkleTrees = new ArrayList<>();
        ArrayList<String> inputFile = new ArrayList<>();
        String preHeadTemp = "0";
        Scanner s = new Scanner(System.in);
        boolean flage = true;
        do{
            System.out.print("\nEnter the full path to a file to scan to add to the blockchain (Enter \"s\" to stop): \n");
            String fPath = s.next();
            inputFile.add(fPath);
            if(fPath.equals("s")){
                flage = false;
            }else {
                ArrayList<DataHash> dataList = readFile(fPath);
                MerkleTree mtree = new MerkleTree(dataList);
                int nonce = 0;
                Block mblock = new Block(preHeadTemp,mtree.getRoot(),nonce);
                while(mblock.getHeaderHash() == null){
                    nonce++;
                    mblock.setNonce(nonce);
                }
                preHeadTemp = mblock.getHeaderHash();
                merkleTrees.add(mtree);
                blockchain.add(mblock);
            }
        }while(flage);
        s.close();
        // printBlockchain(blockchain,merkleTrees);
        writeOutput(blockchain,merkleTrees,inputFile.get(0));
    }

    public static ArrayList<DataHash> readFile(String fPath){
        ArrayList<DataHash> dataList = new ArrayList<>();
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
        return dataList;
    }

    public static void printBlockchain(ArrayList<Block> blockchain,ArrayList<MerkleTree> merkleTrees){
        for(int i = blockchain.size()-1; i >= 0 ; i--){
            System.out.println("BEGIN BLOCK");
            System.out.println("BEGIN HEADER");
            System.out.println("BEGIN HEADER\n" + blockchain.get(i).toString());
            System.out.println("END HEADER");
            System.out.print(merkleTrees.get(i).toString());// to string for data
            System.out.println("END BLOCK");
            System.out.println();
        }
    }

    public static void writeOutput(ArrayList<Block> blockchain,ArrayList<MerkleTree> merkleTrees,String fileName){
        List<String> tok = Arrays.asList(fileName.split("[/.]"));
        String fname = tok.get(tok.size() - 2);
        String output = fname.concat(".block.out");
        try(FileWriter myWriter = new FileWriter(output);) {
            for(int i = blockchain.size()-1; i >= 0 ; i--){
                myWriter.write("BEGIN BLOCK\n");
                myWriter.write("BEGIN HEADER\n" + blockchain.get(i).toString() + "\n");
                myWriter.write("END HEADER\n");
                myWriter.write(merkleTrees.get(i).toString());// to string for data
                myWriter.write("END BLOCK\n\n");
            }
            System.out.println("Successfully wrote to the Output File: " + output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
