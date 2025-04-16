package main

import "golang.org/x/exp/mmap"

func ReadFileZeroCopy(path string) ([]byte, error) {
    r, err := mmap.Open(path)
    if err != nil {
        return nil, err
    }
    defer r.Close()

    data := make([]byte, r.Len())
    _, err = r.ReadAt(data, 0)
    return data, err
}
