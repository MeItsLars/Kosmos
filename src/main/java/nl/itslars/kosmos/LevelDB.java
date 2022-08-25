package nl.itslars.kosmos;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import java.util.Arrays;

public class LevelDB {

    private int id;

    private LevelDB(int id) {
        if (id == -1) {
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

    public static LevelDB open(String path) {
        return open(path, null);
    }

    public static LevelDB open(String path, Options options) {
        return new LevelDB(GoLevelDB.leveldb_open(path, options == null ? -1 : options.id));
    }

    public void close() {
        GoLevelDB.leveldb_close(id);
        id = -1;
        checkError();
    }

    public void put(byte[] key, byte[] value) {
        Memory keyMem = new Memory(key.length);
        keyMem.write(0, key, 0, key.length);
        Memory valueMem = new Memory(value.length);
        valueMem.write(0, value, 0, value.length);
        GoLevelDB.leveldb_put(id, keyMem, key.length, valueMem, value.length);
        checkError();
    }

    public void delete(byte[] key) {
        Memory keyMem = new Memory(key.length);
        keyMem.write(0, key, 0, key.length);
        GoLevelDB.leveldb_delete(id, keyMem, key.length);
        checkError();
    }

    public byte[] get(byte[] key) {
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
        Memory keyMem = new Memory(key.length);
        keyMem.write(0, key, 0, key.length);
        int i = GoLevelDB.leveldb_has(id, keyMem, key.length);
        return i == 1;
    }

    public static void repair(String path) {
        GoLevelDB.leveldb_repair(path);
        checkError();
    }

    public Iterator iterator() {
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

    public static class Options {
        private int id;

        private Options(int id) {
            if (id == -1) {
                throw new IllegalArgumentException("Invalid id");
            }
            this.id = id;
            checkError();
        }

        public void setCompression(int compression) {
            GoLevelDB.leveldb_options_set_compression(id, compression);
            checkError();
        }

        public void close() {
            GoLevelDB.leveldb_options_destroy(id);
            id = -1;
            checkError();
        }
    }

    public static class Iterator {

        private int id;

        private Iterator(int id) {
            if (id == -1) {
                throw new IllegalArgumentException("Invalid id");
            }
            this.id = id;
            checkError();
        }

        public boolean next() {
            int i = GoLevelDB.leveldb_iterator_next(id);
            checkError();
            return i == 1;
        }

        public byte[] key() {
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
            GoLevelDB.leveldb_iterator_seek_to_first(id);
            checkError();
        }

        public void close() {
            GoLevelDB.leveldb_iterator_destroy(id);
            id = -1;
            checkError();
        }

    }

}
