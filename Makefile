BUILD_DIR := build

# Remove all build outputs and intermediate files.
clean:
	@ rm -rf $(BUILD_DIR)
	@ rm -rf gen

klox: generate_ast
	@ $(MAKE) -f util/java.make DIR=src PACKAGE=klox
	cd build/src
	jar --create --file=klox.jar --main-class=no.bok.craftinginterpreters.klox.Klox .
	cd ../..
	cp build/src/klox.jar klox.jar

# Kjoer ved aa kalle java -jar klox.jar <.klox-fil>
#Finn ut hvordan jeg gjør den til en ordentlig executable

build:
	javac src/no/bok/craftinginterpreters/klox/*.java -d build
# Compile and run the AST generator.
generate_ast:
	@ $(MAKE) -f util/java.make DIR=src PACKAGE=tool
	@ java -cp build/src no.bok.craftinginterpreters.tool.GenerateAst \
			src/no/bok/craftinginterpreters/klox