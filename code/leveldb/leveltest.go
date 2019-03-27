package main

import (
	"fmt"
	"github.com/syndtr/goleveldb/leveldb"
	"github.com/syndtr/goleveldb/leveldb/util"
	"log"
	"strconv"
)


func main() {
	db, err := leveldb.OpenFile("db", nil)
	if(err!=nil){
		log.Fatalln(err.Error())
	}
	defer db.Close()
	//准备数据
	//randomPut(db)

	//迭代
	//iterator(db)

	//从某个位置开始迭代
	seekIterator(db,[]byte("key0"))

	//子集迭代
	//subsetIterator(db)

	//前缀迭代
	//prefixIterator(db,"b")

	//批量写入
	//batchWrite(db)
}

func iterator(db *leveldb.DB){
	iter:=db.NewIterator(nil,nil)
	for iter.Next(){
		fmt.Println(string(iter.Key()),"---",string(iter.Value()))
	}
	iter.Release()
	err := iter.Error()
	fmt.Println(err)
}

func seekIterator(db *leveldb.DB,key []byte){
	iter:=db.NewIterator(nil,nil)
	for ok:=iter.Seek(key);ok;ok=iter.Next(){
		fmt.Println(string(iter.Key()),"---",string(iter.Value()))
	}
	iter.Release()
	err := iter.Error()
	fmt.Println(err)
}

func subsetIterator(db *leveldb.DB){
	iter:=db.NewIterator(&util.Range{Start:[]byte("key2"),Limit:[]byte("key6")},nil)
	for iter.Next(){
		fmt.Println(string(iter.Key()),"---",string(iter.Value()))
	}
	iter.Release()
	err := iter.Error()
	fmt.Println(err)
}

func prefixIterator(db *leveldb.DB,prefix string){
	iter:=db.NewIterator(util.BytesPrefix([]byte(prefix)),nil)
	for iter.Next(){
		fmt.Println(string(iter.Key()),"---",string(iter.Value()))
	}
	iter.Release()
	err := iter.Error()
	fmt.Println(err)
}

func randomPut(db *leveldb.DB){
	for i:=0; i<10; i++ {
		db.Put([]byte("key"+strconv.Itoa(i)),[]byte("value"+strconv.Itoa(i)),nil)
	}
}

func batchWrite(db *leveldb.DB)  {
	batch:=new(leveldb.Batch)
	for i:=0; i<10; i++ {
		batch.Put([]byte("batch"+strconv.Itoa(i)),[]byte("batch"+strconv.Itoa(i)))
	}
	err:=db.Write(batch,nil)
	if err!=nil {
		fmt.Println(err)
	}
}