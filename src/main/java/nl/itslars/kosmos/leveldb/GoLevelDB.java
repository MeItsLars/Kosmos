package nl.itslars.kosmos.leveldb;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class GoLevelDB {

    static {
        Native.register("goleveldb");
    }

    public static native void leveldb_free(Pointer ptr);
    public static native String leveldb_error();
    public static native int leveldb_options_create();
    public static native void leveldb_options_destroy(int options);
    public static native void leveldb_options_set_compression(int options, int compression);
    public static native void leveldb_options_set_block_size(int options, int blockSize);
    public static native int leveldb_open(String path, int options);
    public static native void leveldb_close(int db);
    public static native void leveldb_compact(int db);
    public static native void leveldb_shrink(int db);
    public static native int leveldb_iterator_create(int iterator);
    public static native void leveldb_iterator_destroy(int iterator);
    public static native int leveldb_iterator_next(int iterator);
    public static native void leveldb_iterator_seek_to_first(int iterator);
    public static native Pointer leveldb_iterator_key(int iterator, PointerByReference size);
    public static native Pointer leveldb_iterator_value(int iterator, PointerByReference size);
    public static native Pointer leveldb_get(int db, Pointer key, int keySize, PointerByReference valueSize);
    public static native int leveldb_has(int db, Pointer key, int keySize);
    public static native void leveldb_put(int db, Pointer key, int keySize, Pointer value, int valueSize);
    public static native void leveldb_delete(int db, Pointer key, int keySize);
    public static native void leveldb_repair(String path);

}
