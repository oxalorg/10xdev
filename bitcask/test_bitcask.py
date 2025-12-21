# /// script
# dependencies = ["pytest"]
# ///

import os
import tempfile
import shutil
import pytest
from bitcask import Bitcask

class TestBitcask:
    def setup_method(self):
        self.temp_dir = tempfile.mkdtemp()
        self.bitcask = Bitcask(self.temp_dir)
        self.bitcask.open()
    
    def teardown_method(self):
        if hasattr(self, 'bitcask'):
            self.bitcask.close()
        if hasattr(self, 'temp_dir') and os.path.exists(self.temp_dir):
            shutil.rmtree(self.temp_dir)
    
    def test_basic_put_get(self):
        key = b"test_key"
        value = b"test_value"
        
        self.bitcask.put(key, value)
        result = self.bitcask.get(key)
        
        assert result == value
    
    def test_put_get_string_keys(self):
        key = "test_key"
        value = "test_value"
        
        self.bitcask.put(key, value)
        result = self.bitcask.get(key)
        
        assert result == b"test_value"
    
    def test_get_nonexistent_key(self):
        result = self.bitcask.get(b"nonexistent")
        assert result is None
    
    def test_overwrite_key(self):
        key = b"test_key"
        value1 = b"value1"
        value2 = b"value2"
        
        self.bitcask.put(key, value1)
        self.bitcask.put(key, value2)
        
        result = self.bitcask.get(key)
        assert result == value2
    
    def test_delete_key(self):
        key = b"test_key"
        value = b"test_value"
        
        self.bitcask.put(key, value)
        assert self.bitcask.get(key) == value
        
        self.bitcask.delete(key)
        assert self.bitcask.get(key) is None
    
    def test_delete_nonexistent_key(self):
        self.bitcask.delete(b"nonexistent")
    
    def test_list_keys(self):
        keys = [b"key1", b"key2", b"key3"]
        
        for key in keys:
            self.bitcask.put(key, b"value")
        
        result_keys = self.bitcask.list_keys()
        assert set(result_keys) == set(keys)
    
    def test_list_keys_after_delete(self):
        keys = [b"key1", b"key2", b"key3"]
        
        for key in keys:
            self.bitcask.put(key, b"value")
        
        self.bitcask.delete(b"key2")
        result_keys = self.bitcask.list_keys()
        
        assert set(result_keys) == {b"key1", b"key3"}
    
    def test_fold(self):
        data = {b"key1": b"value1", b"key2": b"value2", b"key3": b"value3"}
        
        for key, value in data.items():
            self.bitcask.put(key, value)
        
        def collect_func(key, value, acc):
            acc[key] = value
            return acc
        
        result = self.bitcask.fold(collect_func, {})
        assert result == data
    
    def test_sync(self):
        self.bitcask.put(b"key", b"value")
        self.bitcask.sync()
    
    def test_file_rotation(self):
        small_bitcask = Bitcask(self.temp_dir + "_small", max_file_size=100)
        small_bitcask.open()
        
        try:
            for i in range(10):
                key = f"key_{i}".encode()
                value = f"long_value_{i}" * 10
                small_bitcask.put(key, value.encode())
            
            data_files = [f for f in os.listdir(small_bitcask.directory) if f.endswith('.data')]
            assert len(data_files) > 1
            
            for i in range(10):
                key = f"key_{i}".encode()
                result = small_bitcask.get(key)
                expected = f"long_value_{i}" * 10
                assert result == expected.encode()
        
        finally:
            small_bitcask.close()
            if os.path.exists(small_bitcask.directory):
                shutil.rmtree(small_bitcask.directory)
    
    def test_persistence(self):
        key = b"persistent_key"
        value = b"persistent_value"
        
        self.bitcask.put(key, value)
        self.bitcask.close()
        
        new_bitcask = Bitcask(self.temp_dir)
        new_bitcask.open()
        
        try:
            result = new_bitcask.get(key)
            assert result == value
        finally:
            new_bitcask.close()
    
    def test_merge_process(self):
        keys = []
        for i in range(20):
            key = f"key_{i}".encode()
            keys.append(key)
            self.bitcask.put(key, f"value_{i}".encode())
        
        for i in range(0, 20, 2):
            key = f"key_{i}".encode()
            self.bitcask.put(key, f"updated_value_{i}".encode())
        
        for i in range(1, 20, 4):
            key = f"key_{i}".encode()
            self.bitcask.delete(key)
        
        initial_files = len([f for f in os.listdir(self.bitcask.directory) if f.endswith('.data')])
        
        self.bitcask.merge()
        
        for i in range(20):
            key = f"key_{i}".encode()
            if i % 4 == 1:
                assert self.bitcask.get(key) is None
            elif i % 2 == 0:
                expected = f"updated_value_{i}".encode()
                assert self.bitcask.get(key) == expected
            else:
                expected = f"value_{i}".encode()
                assert self.bitcask.get(key) == expected
        
        hint_files = [f for f in os.listdir(self.bitcask.directory) if f.endswith('.hint')]
        assert len(hint_files) > 0
    
    def test_closed_operations(self):
        self.bitcask.close()
        
        with pytest.raises(RuntimeError):
            self.bitcask.put(b"key", b"value")
        
        with pytest.raises(RuntimeError):
            self.bitcask.get(b"key")
        
        with pytest.raises(RuntimeError):
            self.bitcask.delete(b"key")
        
        with pytest.raises(RuntimeError):
            self.bitcask.list_keys()
        
        with pytest.raises(RuntimeError):
            self.bitcask.fold(lambda k, v, a: a, {})
        
        with pytest.raises(RuntimeError):
            self.bitcask.sync()
        
        with pytest.raises(RuntimeError):
            self.bitcask.merge()
    
    def test_context_manager(self):
        with Bitcask(self.temp_dir + "_context") as bc:
            bc.open()
            bc.put(b"key", b"value")
            assert bc.get(b"key") == b"value"
        
        assert bc.closed == True
    
    def test_empty_values(self):
        self.bitcask.put(b"empty_key", b"")
        result = self.bitcask.get(b"empty_key")
        assert result == b""
    
    def test_large_values(self):
        key = b"large_key"
        value = b"x" * 10000
        
        self.bitcask.put(key, value)
        result = self.bitcask.get(key)
        
        assert result == value
    
    def test_binary_data(self):
        key = b"binary_key"
        value = bytes(range(256))
        
        self.bitcask.put(key, value)
        result = self.bitcask.get(key)
        
        assert result == value

if __name__ == "__main__":
    pytest.main([__file__, "-v"])