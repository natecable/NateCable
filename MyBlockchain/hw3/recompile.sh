javac code/*.java
rm -R code/classes
mkdir code/classes
jar cmfv code/Manifest.txt MBS.jar code/*.class
mv code/*.class code/classes/
