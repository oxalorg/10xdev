.PHONY: main

main:
	clang --target=wasm32 -emit-llvm -c -S add.c
	# creates add.ll, llvm ir
	llc -march=wasm32 add.ll
	# creates add.o, valid wasm module containing all compiled C code
	llc -march=wasm32 -filetype=obj add.ll
	# creates add.s, LLVM's assembly format for WebAssembly
	# is human-readable and somewhat similar to WAT
	llc -march=wasm32 add.ll
	wasm-objdump -x add.o
	wasm-ld --no-entry --export-all -o add.wasm add.o
