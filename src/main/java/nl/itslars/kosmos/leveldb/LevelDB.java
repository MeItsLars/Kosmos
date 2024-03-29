package nl.itslars.kosmos.leveldb;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import java.nio.charset.StandardCharsets;

public class LevelDB implements AutoCloseable {

    private int id;

    private LevelDB(int id) {
        if (id == -1) {
            checkError();
            throw new IllegalArgumentException("Invalid id");
        }
        this.id = id;
        checkError();
    }

    private static void checkError() {
        String s = GoLevelDB.leveldb_error();
        if (s != null && !s.isEmpty()) {
            throw new RuntimeException(s);
        }
    }

    public static void shrink(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        GoLevelDB.leveldb_shrink_file(path.getBytes(StandardCharsets.UTF_8));
    }

    public static LevelDB open(String path) {
        return open(path, null);
    }

    public static LevelDB open(String path, Options options) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        return new LevelDB(GoLevelDB.leveldb_open(path.getBytes(StandardCharsets.UTF_8), options == null ? -1 : options.id));
    }

    public void close() {
        if (id == -1) {
            return;
        }
        GoLevelDB.leveldb_close(id);
        id = -1;
        checkError();
    }

    public void shrink() {
        if (id == -1) {
            return;
        }
        GoLevelDB.leveldb_shrink(id);
        checkError();
    }

    public void put(byte[] key, byte[] value) {
        if (id == -1) {
            throw new IllegalStateException("Database is closed");
        }
        Memory keyMem = new Memory(Math.max(key.length, 1));
        if (key.length > 0) {
            keyMem.write(0, key, 0, key.length);
        }
        Memory valueMem = new Memory(Math.max(value.length, 1));
        if (value.length > 0) {
            valueMem.write(0, value, 0, value.length);
        }
        GoLevelDB.leveldb_put(id, keyMem, key.length, valueMem, value.length);
        checkError();
    }

    public void delete(byte[] key) {
        if (id == -1) {
            throw new IllegalStateException("Database is closed");
        }
        Memory keyMem = new Memory(key.length);
        keyMem.write(0, key, 0, key.length);
        GoLevelDB.leveldb_delete(id, keyMem, key.length);
        checkError();
    }

    public byte[] get(byte[] key) {
        if (id == -1) {
            throw new IllegalStateException("Database is closed");
        }
        Memory keyMem = new Memory(key.length);
        keyMem.write(0, key, 0, key.length);
        PointerByReference valueSize = new PointerByReference();
        Pointer value = GoLevelDB.leveldb_get(id, keyMem, key.length, valueSize);
        if (value == null) {
            return null;
        }
        byte[] valueBytes = value.getByteArray(0, valueSize.getPointer().getInt(0));
        GoLevelDB.leveldb_free(value);
        if (valueBytes.length == 0) {
            checkError();
        }
        return valueBytes;
    }

    public boolean has(byte[] key) {
        if (id == -1) {
            throw new IllegalStateException("Database is closed");
        }
        Memory keyMem = new Memory(key.length);
        keyMem.write(0, key, 0, key.length);
        int i = GoLevelDB.leveldb_has(id, keyMem, key.length);
        return i == 1;
    }

    public static void repair(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path cannot be null");
        }
        GoLevelDB.leveldb_repair(path.getBytes(StandardCharsets.UTF_8));
        checkError();
    }

    public Iterator iterator() {
        if (id == -1) {
            throw new IllegalStateException("Database is closed");
        }
        return new Iterator(GoLevelDB.leveldb_iterator_create(id));
    }

    public static Options createOptions() {
        return new Options(GoLevelDB.leveldb_options_create());
    }

    public enum CompressionType {
        NONE(0),
        SNAPPY(1),
        ZLIB(2),
        RAW_ZLIB(4);

        private final int id;

        CompressionType(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    public static class Options implements AutoCloseable {
        private int id;

        private Options(int id) {
            if (id == -1) {
                throw new IllegalArgumentException("Invalid id");
            }
            this.id = id;
            checkError();
        }

        public void setCompression(int compression) {
            if (id == -1) {
                throw new IllegalStateException("Options are closed");
            }
            GoLevelDB.leveldb_options_set_compression(id, compression);
            checkError();
        }

        public void setCompressionLevel(int compressionLevel) {
            if (id == -1) {
                throw new IllegalStateException("Options are closed");
            }
            GoLevelDB.leveldb_options_set_compression_level(id, compressionLevel);
            checkError();
        }

        public void setBlockSize(int blockSize) {
            if (id == -1) {
                throw new IllegalStateException("Options are closed");
            }
            GoLevelDB.leveldb_options_set_block_size(id, blockSize);
            checkError();
        }

        public void close() {
            if (id == -1) {
                return;
            }
            GoLevelDB.leveldb_options_destroy(id);
            id = -1;
            checkError();
        }
    }

    public static class Iterator implements AutoCloseable {

        private int id;

        private Iterator(int id) {
            if (id == -1) {
                throw new IllegalArgumentException("Invalid id");
            }
            this.id = id;
            checkError();
        }

        public boolean next() {
            if (id == -1) {
                throw new IllegalStateException("Iterator is closed");
            }
            int i = GoLevelDB.leveldb_iterator_next(id);
            checkError();
            return i == 1;
        }

        public byte[] key() {
            if (id == -1) {
                throw new IllegalStateException("Iterator is closed");
            }
            PointerByReference size = new PointerByReference();
            Pointer key = GoLevelDB.leveldb_iterator_key(id, size);
            checkError();
            if (key == null) {
                return null;
            }
            byte[] keyBytes = key.getByteArray(0, size.getPointer().getInt(0));
            GoLevelDB.leveldb_free(key);
            checkError();
            return keyBytes;
        }

        public byte[] value() {
            if (id == -1) {
                throw new IllegalStateException("Iterator is closed");
            }
            PointerByReference size = new PointerByReference();
            Pointer value = GoLevelDB.leveldb_iterator_value(id, size);
            checkError();
            if (value == null) {
                return null;
            }
            byte[] valueBytes = value.getByteArray(0, size.getPointer().getInt(0));
            GoLevelDB.leveldb_free(value);
            checkError();
            return valueBytes;
        }

        public void seekToFirst() {
            if (id == -1) {
                throw new IllegalStateException("Iterator is closed");
            }
            GoLevelDB.leveldb_iterator_seek_to_first(id);
            checkError();
        }

        public void close() {
            if (id == -1) {
                return;
            }
            GoLevelDB.leveldb_iterator_destroy(id);
            id = -1;
            checkError();
        }

    }

}
