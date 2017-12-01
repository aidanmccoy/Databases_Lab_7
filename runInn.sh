#!/bin/sh
echo "Compiling..."
javac *.java

echo "Running..."
java -cp mysql-connector-java-5.1.44/mysql-connector-java-5.1.44-bin.jar:. InnReservations
