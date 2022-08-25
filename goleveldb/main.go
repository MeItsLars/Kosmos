package main

// #include <stdlib.h>
import "C"
import (
	"github.com/df-mc/goleveldb/leveldb"
	"github.com/df-mc/goleveldb/leveldb/iterator"
	"github.com/df-mc/goleveldb/leveldb/opt"
	"github.com/df-mc/goleveldb/leveldb/util"
	"sync"
	"unsafe"
)

var lock = sync.RWMutex{}
var pointers = map[int]interface{}{}
var pointerCounter = 0
var lastError error

func GetPointer(id C.int) interface{} {
	lock.RLock()
	defer lock.RUnlock()
	return pointers[int(id)]
}

func SetPointer(id C.int, ptr interface{}) {
	lock.RLock()
	defer lock.RUnlock()
	pointers[int(id)] = ptr
}

func DeletePointer(id C.int) {
	lock.RLock()
	defer lock.RUnlock()
	delete(pointers, int(id))
}

func AllocatePointer(ptr interface{}) C.int {
	pointerCounter++
	SetPointer(C.int(pointerCounter), ptr)
	return C.int(pointerCounter)
}

func main() {
}

//export leveldb_shrink
func leveldb_shrink(id C.int) {
	// This method is not yet fully implemented.
	db := GetPointer(id).(*leveldb.DB)
	newIterator := db.NewIterator(nil, nil)
	for newIterator.Next() {
		err := db.Put(newIterator.Key(), newIterator.Value(), nil)
		if err != nil {
			panic(err)
		}
	}
	if newIterator.Error() != nil {
		lastError = newIterator.Error()
		return
	}
	newIterator.Release()
	err := db.CompactRange(util.Range{})
	if err != nil {
		lastError = err
		return
	}
}

//export leveldb_free
func leveldb_free(ptr unsafe.Pointer) {
	C.free(ptr)
}

//export leveldb_error
func leveldb_error() *C.char {
	defer func() {
		lastError = nil
	}()
	if lastError != nil {
		return C.CString(lastError.Error())
	}
	return nil
}

//export leveldb_options_create
func leveldb_options_create() C.int {
	return AllocatePointer(opt.Options{})
}

//export leveldb_options_destroy
func leveldb_options_destroy(id C.int) {
	DeletePointer(id)
}

//export leveldb_options_set_compression
func leveldb_options_set_compression(id C.int, compression C.uint) {
	options := GetPointer(id).(opt.Options)
	options.Compression = opt.Compression(uint(compression))
}

//export leveldb_options_set_block_size
func leveldb_options_set_block_size(id C.int, compression C.int) {
	options := GetPointer(id).(opt.Options)
	options.BlockSize = int(compression)
}

//export leveldb_open
func leveldb_open(path *C.char, options C.int) C.int {
	var o *opt.Options
	if options != -1 {
		ptr := GetPointer(options).(opt.Options)
		o = &ptr
	}
	db, err := leveldb.OpenFile(C.GoString(path), o)
	if err != nil {
		lastError = err
		return -1
	}
	return AllocatePointer(db)
}

//export leveldb_close
func leveldb_close(id C.int) {
	db := GetPointer(id).(*leveldb.DB)
	err := db.Close()
	if err != nil {
		lastError = err
		return
	}
	DeletePointer(id)
}

//export leveldb_compact
func leveldb_compact(id C.int) {
	db := GetPointer(id).(*leveldb.DB)
	err := db.CompactRange(util.Range{})
	if err != nil {
		lastError = err
	}
}

//export leveldb_iterator_create
func leveldb_iterator_create(id C.int) C.int {
	db := GetPointer(id).(*leveldb.DB)
	iter := db.NewIterator(nil, nil)
	if iter.Error() != nil {
		lastError = iter.Error()
		iter.Release()
		return -1
	}
	return AllocatePointer(iter)
}

//export leveldb_iterator_destroy
func leveldb_iterator_destroy(id C.int) {
	iter := GetPointer(id).(iterator.Iterator)
	iter.Release()
	DeletePointer(id)
}

//export leveldb_iterator_next
func leveldb_iterator_next(id C.int) C.int {
	iter := GetPointer(id).(iterator.Iterator)
	if iter.Next() {
		return C.int(1)
	}
	if iter.Error() != nil {
		lastError = iter.Error()
	}
	return C.int(0)
}

//export leveldb_iterator_seek_to_first
func leveldb_iterator_seek_to_first(id C.int) {
	iter := GetPointer(id).(iterator.Iterator)
	iter.First()
	if iter.Error() != nil {
		lastError = iter.Error()
	}
}

//export leveldb_iterator_key
func leveldb_iterator_key(id C.int, size *C.int) unsafe.Pointer {
	iter := GetPointer(id).(iterator.Iterator)
	key := iter.Key()
	*size = C.int(len(key))
	return C.CBytes(key)
}

//export leveldb_iterator_value
func leveldb_iterator_value(id C.int, size *C.int) unsafe.Pointer {
	iter := GetPointer(id).(iterator.Iterator)
	value := iter.Value()
	*size = C.int(len(value))
	return C.CBytes(value)
}

//export leveldb_get
func leveldb_get(id C.int, key unsafe.Pointer, keySize C.int, valueSize *C.int) unsafe.Pointer {
	db := GetPointer(id).(*leveldb.DB)
	goKey := C.GoBytes(key, keySize)
	value, err := db.Get(goKey, nil)
	if err != nil {
		if err != leveldb.ErrNotFound {
			lastError = err
		}
		return nil
	}
	*valueSize = C.int(len(value))
	return C.CBytes(value)
}

//export leveldb_has
func leveldb_has(id C.int, key unsafe.Pointer, keySize C.int) C.int {
	db := GetPointer(id).(*leveldb.DB)
	goKey := C.GoBytes(key, keySize)
	has, err := db.Has(goKey, nil)
	if err != nil {
		if err != leveldb.ErrNotFound {
			lastError = err
		}
		return C.int(0)
	}
	if has {
		return C.int(1)
	}
	return C.int(0)
}

//export leveldb_put
func leveldb_put(id C.int, key unsafe.Pointer, keySize C.int, value unsafe.Pointer, valueSize C.int) {
	db := GetPointer(id).(*leveldb.DB)
	goKey := C.GoBytes(key, keySize)
	goValue := C.GoBytes(value, valueSize)
	err := db.Put(goKey, goValue, nil)
	if err != nil {
		lastError = err
	}
}

//export leveldb_delete
func leveldb_delete(id C.int, key unsafe.Pointer, keySize C.int) {
	db := GetPointer(id).(*leveldb.DB)
	goKey := C.GoBytes(key, keySize)
	err := db.Delete(goKey, nil)
	if err != nil {
		lastError = err
	}
}

//export leveldb_repair
func leveldb_repair(path *C.char) {
	db, err := leveldb.RecoverFile(C.GoString(path), nil)
	if err != nil {
		lastError = err
	}
	err = db.Close()
	if err != nil {
		lastError = err
	}
}
