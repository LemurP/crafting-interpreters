BUILD_DIR := build

default: klox
# Remove all build outputs and intermediate files.
clean:
	@ rm -rf $(BUILD_DIR)
	@ rm -rf gen
	@ rm klox.jar

klox:
	@ $(MAKE) -f util/java.make DIR=src PACKAGE=klox
	cd build/src &&	jar --create --file=klox.jar --main-class=no.bok.craftinginterpreters.klox.Klox .
	cp build/src/klox.jar klox.jar

# Kjoer ved aa kalle java -jar klox.jar <.klox-fil>
#Finn ut hvordan jeg gjør den til en ordentlig executable

test: klox
	cd ../craftinginterpreters && dart tool/bin/test.dart chap12_classes --interpreter ../klox/klox
# Compile and run the AST generator.
generate_ast:
	@ $(MAKE) -f util/java.make DIR=src PACKAGE=tool
	@ java -cp build/src no.bok.craftinginterpreters.tool.GenerateAst \
			src/no/bok/craftinginterpreters/klox