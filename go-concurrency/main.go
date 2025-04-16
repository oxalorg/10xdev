package main

import (
	"fmt"
	// "sync"
)

func main() {
	data, err := ReadFileZeroCopy("./go.mod")
	if err != nil {
		fmt.Printf("Error reading file: %v\n", err)
		return
	}
	fmt.Printf("File contents: %s\n", string(data))
}

// func finishRequest(timeout int) {
// 	// var result int;
// 	var delta int = -1;
// 	state := 4 << 32
// 	state += 3
// 	// 32 - 4
// 	// 32 - 3
// 	// var wg sync.WaitGroup;
// 	fmt.Printf("%b\n", int64(1));
// 	fmt.Printf("%b\n", uint64(1));
// 	fmt.Printf("%b\n", -2);
// 	fmt.Printf("%b\n", delta);
// 	fmt.Printf("%b\n", uint64(delta));
// 	fmt.Printf("%b\n", uint64(delta) << 32);
// 	fmt.Printf("-------\n");
// 	fmt.Printf("%b\n", state);
// 	fmt.Printf("%b\n", state >> 32);
// 	fmt.Printf("%b\n", int32(state >> 32));
// 	fmt.Printf("%b\n", uint32(state));

// 	// not 0001 ->

// 	// 00000010
// 	// 11111101
// 	// 11111111
// 	// 00000010
// }
