.PHONY: main

main:
	clang --target=wasm32 -emit-llvm -c -S add.c
	llc -march=wasm32 add.ll
	llc -march=wasm32 -filetype=obj add.ll
