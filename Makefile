.DEFAULT_GOAL := buildrun

build:
	javac -d bin/ -cp src src/io/siever/jlox/Lox.java

run:
	java -cp bin/ io.siever.jlox.Lox

buildrun:
	javac -d bin/ -cp src src/io/siever/jlox/Lox.java
	java -cp bin/ io.siever.jlox.Lox

.PHONY: build run buildrun
.SILENT: run buildrun
