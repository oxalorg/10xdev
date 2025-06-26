compile:
	gcc -shared -fPIC override.c -o liboverride.so
	rustc main.rs -o main_binary

run: compile
	./main_binary
	LD_PRELOAD="./liboverride.so" ./main_binary
