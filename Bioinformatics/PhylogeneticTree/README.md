This program takes a matrix as input as formatted by the Clustal Matrix Generation Script.
Example: 
   5
3MXE_A|PDB
3MXE_B|PDB0.0
3PJ6_A|PDB0.14  0.14
3QIN_A|PDB0.95  0.95  0.95
3QIO_A|PDB0.95  0.95  0.95  0.0
This is a valid input, as lines are split by "|PDB" and whitespace

To run this, the input file must be in the same directory as these Java files.

You can then run the bash script run.sh by "bash run.sh".

This script will compile the program, then ask for the name of the input file, then for the name of the file you want to output to (can be a new file)

It will then execute the code and display the UPGMA Tree and Neighbor Joining Tree, and the relevant data