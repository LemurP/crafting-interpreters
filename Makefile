BUILD_DIR := build

default: klox
# Remove all build outputs and intermediate files.
clean:
	@ rm -rf $(BUILD_DIR)
	@ rm -rf gen
	@ rm -f klox.jar

klox:
	@ $(MAKE) -f util/java.make DIR=src PACKAGE=klox
	cd build/src &&	jar --create --file=klox.jar --main-class=no.bok.craftinginterpreters.klox.Klox .
	cp build/src/klox.jar klox.jar

# Run dart pub get on tool directory.
get:
	@ cd ./tool; dart pub get

test: klox
	dart tool/bin/test.dart chap13_inheritance --interpreter ./klox
# Compile and run the AST generator.
generate_ast:
	@ $(MAKE) -f util/java.make DIR=src PACKAGE=tool
	@ java -cp build/src no.bok.craftinginterpreters.tool.GenerateAst \
			src/no/bok/craftinginterpreters/klox

.PHONY: klox test generate_ast get