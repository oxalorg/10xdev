import sqlite3
import time

# Function to test SQLite insert speed
# num_inserts: number of insert operations to perform
# batch_size: number of inserts per transaction
# use_pragma: whether to apply performance PRAGMA settings

def test_sqlite_write_speed(db_name, num_inserts=10000, batch_size=1000, use_pragma=True):
    conn = sqlite3.connect(db_name)
    cur = conn.cursor()

    # Create a test table
    cur.execute('CREATE TABLE IF NOT EXISTS test (id INTEGER PRIMARY KEY AUTOINCREMENT, data TEXT)')
    conn.commit()

    if use_pragma:
        # Speed up SQLite by disabling synchronous mode and using memory journal
        cur.execute('PRAGMA synchronous = OFF')
        cur.execute('PRAGMA journal_mode = MEMORY')
        #cur.execute('PRAGMA temp_store = MEMORY')

    start_time = time.time()

    for i in range(0, num_inserts, batch_size):
        cur.execute('BEGIN TRANSACTION')
        for j in range(batch_size):
            if i + j >= num_inserts:
                break
            cur.execute('INSERT INTO test (data) VALUES (?)', (f'Sample data {i + j}',))
        cur.execute('COMMIT')

    duration = time.time() - start_time
    writes_per_sec = num_inserts / duration

    conn.close()

    return writes_per_sec

# Run test
num_inserts = 10000
batch_size = 1
speed = test_sqlite_write_speed('./speed.db', num_inserts, batch_size, True)
print(f"SQLite write speed: {speed:.2f} writes/second")
