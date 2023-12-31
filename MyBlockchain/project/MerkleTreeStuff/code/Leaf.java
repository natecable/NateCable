package code;

public class Leaf{
    //Leaf Structure
    private Leaf left;
    private Leaf right;
    private DataHash data;

    public Leaf(Leaf l, Leaf r, DataHash data) {
        this.left = l;
        this.right = r;
        this.data = data;
    }

    public Leaf getLeft() {
        return left;
    }

    public Leaf getRight() {
        return right;
    }

    public void setLeft(Leaf l) {
            this.left = l;
        }

    public void setRight(Leaf r) {
        this.right = r;
    }

    public DataHash getData() {
        return data;
    }
    
    public void setData(DataHash data){
        this.data = data;
    }

    public String getHash(){
        return data.getHashVal();
    }
}