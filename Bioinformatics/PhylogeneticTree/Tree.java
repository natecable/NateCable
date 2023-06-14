import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Tree {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        System.out.println("Please input the name of your file that is in this directory: ");

        String inputFilePath = s.next();
        s.close();
        List<Sequence> sequences = readInputFile(inputFilePath);

        //List<Sequence> upgmaTree = computeUPGMATree(sequences);
        //System.out.print("UPGMA Tree");
       // printTree(upgmaTree, "UPGMA Method");

        List<Sequence> neighborJoiningTree = computeNeighborJoiningTree(sequences);
        printTree(neighborJoiningTree, "Neighbor Joining Method");
        // Output Neighbor Joining tree
    }

    // Read input file method (unchanged from previous response)
    private static List<Sequence> readInputFile(String fileName) {
        List<Sequence> sequences = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            int matrixSize = Integer.parseInt(br.readLine().trim());
            for (int i = 0; i < matrixSize; i++) {
                String[] tokens = br.readLine().split("\\s+");
                String name = tokens[0];
                double[] distances = new double[matrixSize];
                for (int j = 1; j < tokens.length; j++) {
                    distances[j - 1] = Double.parseDouble(tokens[j]);
                }
                sequences.add(new Sequence(name, distances));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sequences;
    }
    


    private static List<Sequence> computeUPGMATree(List<Sequence> sequences) {
        while (sequences.size() > 1) {
            int[] minIndex = findMinDistanceIndex(sequences);
            int i = minIndex[0];
            int j = minIndex[1];

            double[] newDistances = new double[sequences.size() - 1];
            for (int k = 0, index = 0; k < sequences.size(); k++) {
                if (k != i && k != j) {
                    double dij = sequences.get(i).distances[j];
                    double dik = sequences.get(i).distances[k];
                    double djk = sequences.get(j).distances[k];
                    newDistances[index++] = (dik + djk - dij) / 2;
                }
            }

            Sequence newSequence = new Sequence("(" + sequences.get(i).name + "," + sequences.get(j).name + ")", newDistances);
            newSequence.clusterSize = sequences.get(i).clusterSize + sequences.get(j).clusterSize;

            sequences.remove(j);
            sequences.remove(i);
            sequences.add(newSequence);

            updateDistances(sequences);
        }

        return sequences;
    }

    private static int[] findMinDistanceIndex(List<Sequence> sequences) {
        int minI = 0;
        int minJ = 1;
        double minDistance = sequences.get(minI).distances[minJ];

        for (int i = 0; i < sequences.size(); i++) {
            for (int j = i + 1; j < sequences.size(); j++) {
                double distance = sequences.get(i).distances[j];
                if (distance < minDistance) {
                    minDistance = distance;
                    minI = i;
                    minJ = j;
                }
            }
        }

        return new int[]{minI, minJ};
    }

    private static void updateDistances(List<Sequence> sequences) {
        for (int i = 0; i < sequences.size(); i++) {
            for (int j = i + 1; j < sequences.size(); j++) {
                double sum = 0;
                int count = 0;
                for (int k = 0; k < sequences.get(i).distances.length; k++) {
                    if (sequences.get(i).distances[k] != 0 && sequences.get(j).distances[k] != 0) {
                        sum += sequences.get(i).distances[k] + sequences.get(j).distances[k];
                        count++;
                    }
                }
                double avg = sum / count;
                sequences.get(i).distances[j] = avg;
                sequences.get(j).distances[i] = avg;
            }
        }
    }

    private static List<Sequence> computeNeighborJoiningTree(List<Sequence> sequences) {
        int numNodes = sequences.size();
        double[][] distances = new double[numNodes][numNodes];
        double[] separationSums = new double[numNodes];
        int[] separationCounts = new int[numNodes];

        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                distances[i][j] = sequences.get(i).distances[j];
                separationSums[i] += distances[i][j];
                separationCounts[i]++;
            }
        }

        while (numNodes > 2) {
            double minSeparation = Double.POSITIVE_INFINITY;
            int minI = 0;
            int minJ = 1;
            for (int i = 0; i < numNodes; i++) {
                for (int j = i + 1; j < numNodes; j++) {
                    double separation = (separationSums[i] + separationSums[j] - 2 * distances[i][j]) * (separationCounts[i] * separationCounts[j]);
                    if (separation < minSeparation) {
                        minSeparation = separation;
                        minI = i;
                        minJ = j;
                    }
                }
            }

            double[] newRow = new double[numNodes - 1];
            for (int k = 0, index = 0; k < numNodes; k++) {
                if (k != minI && k != minJ) {
                    newRow[index++] = (distances[minI][k] + distances[minJ][k] - distances[minI][minJ]) / 2;
                }
            }

            Sequence newSequence = new Sequence("(" + sequences.get(minI).name + "," + sequences.get(minJ).name + ")", newRow);
            newSequence.clusterSize = sequences.get(minI).clusterSize + sequences.get(minJ).clusterSize;

            sequences.remove(minJ);
            sequences.remove(minI);
            sequences.add(newSequence);

            for (int i = 0; i < numNodes; i++) {
                for (int j = i + 1; j < numNodes; j++) {
                    distances[i][j] = newRow[j - 1];
                    distances[j][i] = newRow[j - 1];
                }
            }

            numNodes--;

            for (int i = 0; i < numNodes; i++) {
                separationSums[i] = 0;
                separationCounts[i] = 0;
                for (int j = 0; j < numNodes; j++) {
                    if (i != j) {
                        separationSums[i] += distances[i][j];
                        separationCounts[i]++;
                    }
                }
            }
        }

        double[] finalDistances = new double[]{(distances[0][1] + separationSums[0] - separationSums[1]) / 2, (distances[0][1] + separationSums[1] - separationSums[0]) / 2};
        Sequence finalSequence = new Sequence("(" + sequences.get(0).name + "," + sequences.get(1).name + ")", finalDistances);
        finalSequence.clusterSize = sequences.get(0).clusterSize + sequences.get(1).clusterSize;

        List<Sequence> result = new ArrayList<>();
        result.add(finalSequence);
        return result;
    }

    public static void printTree(List<Sequence> tree, String methodName) {
        if (tree == null || tree.isEmpty()) {
            System.out.println("Empty tree.");
            return;
        }
    
        System.out.println(methodName + " method\n");
        System.out.println("Negative branch lengths allowed\n");
    
        // Print the tree structure
        for (Sequence node : tree) {
            if (node.distances != null) {
                System.out.printf("%s%8s", " ", node.name);
                for (double distance : node.distances) {
                    System.out.printf("%8.5f", distance);
                }
                System.out.println();
            }
        }
    
        // Print the "From", "To", "Length", and "Height" table
        System.out.println("\n\nFrom     To            Length          Height");
        System.out.println("----     --            ------          ------");
    
        int nodeId = 1;
        for (Sequence node : tree) {
            if (node.distances != null) {
                for (int i = 0; i < node.distances.length; i++) {
                    double distance = node.distances[i];
                    if (distance != 0.0) {
                        System.out.printf("%4d%8s%12.5f%12.5f%n", nodeId, tree.get(i).name, distance, distance);
                    }else{
                        System.out.printf("Zero %f%n", distance); 
                    }
                }
                nodeId++;
            }else{
                System.out.println("NULL");
            }
        }
    }
    
    

}

class Sequence {
    String name;
    double[] distances;
    int clusterSize;

    public Sequence(String name, double[] distances) {
        this.name = name;
        this.distances = distances;
        this.clusterSize = 1;
    }
}

