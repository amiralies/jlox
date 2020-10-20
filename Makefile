.DEFAULT_GOAL := buildrun

build:
	javac -d bin/ -cp src src/io/siever/lox/Lox.java

run:
	java -cp bin/ io.siever.lox.Lox

buildrun:
	javac -d bin/ -cp src src/io/siever/lox/Lox.java
	java -cp bin/ io.siever.lox.Lox

.PHONY: build run buildrun
.SILENT: run buildrun
