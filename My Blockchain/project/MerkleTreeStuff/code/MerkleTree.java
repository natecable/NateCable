package code;


import java.util.ArrayList;

public class MerkleTree {

    private Leaf root;

    public MerkleTree(ArrayList<DataHash> data){
        this.root = generateTree(data);
    }

    

    private static Leaf generateTree(ArrayList<DataHash> data) {

        ArrayList<Leaf> children = new ArrayList<>();

        for (DataHash d : data) {
            children.add(new Leaf(null, null, d));
        }




        ArrayList<Leaf> par = new ArrayList<>();

        while (children.size() != 1) {
            int index = 0;
            int length = children.size();
            while (index < length) {
                Leaf leftChild = children.get(index);
                Leaf rightChild = null;

                DataHash parentHash = null;
                if ((index + 1) < length) {
                    rightChild = children.get(index + 1);
                    parentHash = new DataHash(HashFunc.hash(leftChild.getHash() + rightChild.getHash()));
                } else {
                    parentHash = new DataHash(HashFunc.hash(leftChild.getHash()));
                }
                par.add(new Leaf(leftChild, rightChild, parentHash));
                index += 2;
            }
            children = par;
            par= new ArrayList<>();
        }
        return children.get(0);
    }

    public String getRoot(){
        return root.getHash();
    }

}
