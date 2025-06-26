# Rust LD_PRELOAD Function Override Demo

```bash
make run
```

`LD_PRELOAD` to override functions at runtime by intercepting dynamic library calls.

This example illustrates:
- Dynamic linking mechanics on Unix systems
- Function interposition using `LD_PRELOAD`
- Rust-C interoperability through the C ABI
- How shared libraries can modify program behavior at runtime
