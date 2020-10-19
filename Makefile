.DEFAULT_GOAL := run

build:
	javac -d bin/ -cp src src/io/siever/jlox/Main.java
run:
	javac -d bin/ -cp src src/io/siever/jlox/Main.java
	java -cp bin/ io.siever.jlox.Main

.PHONY: build run
.SILENT: run
