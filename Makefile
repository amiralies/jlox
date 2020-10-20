.DEFAULT_GOAL := run

build:
	javac -d bin/ -cp src src/io/siever/jlox/Lox.java
run:
	javac -d bin/ -cp src src/io/siever/jlox/Lox.java
	java -cp bin/ io.siever.jlox.Lox

.PHONY: build run
.SILENT: run
