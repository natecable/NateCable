import java.io.*;
import java.util.*;

public class ProjectThree {
    static ArrayList<String> names;
    static double[][] dist;
    public static void main(String[] args) throws Exception{
        names = new ArrayList<String>();
        Matrix matrix = fromFile(args[0]);
        dist = matrix.distances;
        UPGMA u = new UPGMA(matrix, names);
        NewickParser n = new NewickParser();
        String newi = u.run();
        Node root = n.parse(newi);
        NeighborJoining nj = new NeighborJoining(dist, names);
        Node root2 = nj.computeTree();

        TreePrinter t = new TreePrinter();
        System.out.println("UPGMA Tree \n\n");
        t.print(root);
        System.out.println("\n\nUPGMA Newick String: \n" + newi);
        System.out.printf("%-10s   %-10s\t%-5s\t\t %-5s%n", "From", "To", "Length", "Height");
        System.out.printf("%-10s   %-10s\t%-5s\t\t %-5s%n", "-----", "---", "-------", "-------");
        printDetail(root);
        System.out.println("\n\n\nNeighbor Joining Tree \n\n");
        t.print(root2);
        System.out.printf("%n%n%-10s   %-10s\t%-5s\t\t %-5s%n", "From", "To", "Length", "Height");
        System.out.printf("%-10s   %-10s\t%-5s\t\t %-5s%n", "-----", "---", "-------", "-------");
        printDetail(root2);
        
    }


    public static Matrix fromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int n = Integer.parseInt(reader.readLine().trim());
        Matrix r = new Matrix(n);
        String[] labels = new String[n];
        for(int i = 0; i < n; i++) {
            //String[] line = reader.readLine().split("\\s+");
            String[] line = reader.readLine().split("\\|PDB|\\s+");
            labels[i] = line[0];
            names.add(line[0]);
            if(i == 0){
                continue;
            }else{
                for(int j = 0; j < i; j++) {
                    r.setDistance(i, j, Double.parseDouble(line[j + 1]));
                } 
            }
            
        }
        reader.close();
        return r;
    }

    public static void pr(Node root){
        if(root.leftChild != null){
            System.out.print("L");
            pr(root.leftChild);
        }
        if(root.rightChild != null){
            pr(root.rightChild);
            System.out.print("R");
        }
        System.out.print(root.label + " -> ");
    }

    public static void printDetail(Node node) {
        if (node == null) {
            return;
        }
    
        if (node.leftChild != null) {
            System.out.printf("%-10s   %-10s\t%.5f\t\t %.5f%n", node.label, node.leftChild.label, node.leftChild.length, node.leftChild.height);
            printDetail(node.leftChild);
        }
    
        if (node.rightChild != null) {
            System.out.printf("%-10s   %-10s\t%.5f\t\t %.5f%n", node.label, node.rightChild.label, node.rightChild.length, node.rightChild.height);
            printDetail(node.rightChild);
        }
    }
    


}
