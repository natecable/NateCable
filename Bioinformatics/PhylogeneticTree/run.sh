#!/bin/bash

# Compile the Java program
javac ProjectThree.java

# Ask user for the name of a file
read -p "Enter the name of the matrix file (must be in same folder as this executable): " filename

# Ask user 
read -p "Enter the name of the output file: " outname

# Execute the program
java ProjectThree "$filename" > $outname
