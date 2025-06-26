#[no_mangle]
extern "C" fn call_me() {
    println!("Original call_me function");
}

fn main() {
    unsafe {
        call_me();
    }
}