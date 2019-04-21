package main

import (
	"crypto/ecdsa"
	"crypto/elliptic"
	"crypto/rand"
	"fmt"
	"log"
	_ "test/routers"
)

func main() {
	read:=rand.Reader
	priv,err:=ecdsa.GenerateKey(elliptic.P256(),read)
	if err != nil {
		log.Fatalln(err.Error())
	}
	r,s,err:=ecdsa.Sign(read,priv,[]byte("hello"))
	if err != nil {
		log.Fatalln(err.Error())
	}
	fmt.Println("签名结果")
	fmt.Println("r = ",r)
	fmt.Println("s = ",s)
	b := ecdsa.Verify(&priv.PublicKey,[]byte("hello"),r,s)
	if b {
		fmt.Println("签名验证成功")
	}else {
		fmt.Println("签名验证失败")
	}
}


