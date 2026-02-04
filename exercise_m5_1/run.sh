#!/bin/bash
# Compile
javac -cp ".:../mysql/java/ch01_DBTestApp/mysql-connector-java-8.0.15.jar" -d out src/*.java src/model/*.java src/dao/*.java src/util/*.java

# Run
java -cp "out:../mysql/java/ch01_DBTestApp/mysql-connector-java-8.0.15.jar" Main
