#!/bin/bash
mkdir target
javac -d target $(find src -name "*.java")
