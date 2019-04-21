package main

import (
	"crypto"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha256"
	"encoding/hex"
	"fmt"
	"log"
	_ "test/routers"
)

func main() {
	read:=rand.Reader
	privKey,err:=rsa.GenerateKey(read,1024)
	if err != nil{
		log.Fatalln(err.Error())
	}
	pubKey := privKey.PublicKey

	text := "helloworld5645你好"


	signature1,err:=rsa.SignPKCS1v15(read,privKey,crypto.Hash(0),[]byte(text))
	if err != nil{
		log.Fatalln(err.Error())
	}
	fmt.Println("signature1:",hex.EncodeToString(signature1))
	hashed := sha256.Sum256([]byte(text))
	signature2,err:=rsa.SignPKCS1v15(read,privKey,crypto.SHA256,hashed[:])
	if err != nil{
		log.Fatalln(err.Error())
	}
	fmt.Println("signature2:",hex.EncodeToString(signature2))

	err=rsa.VerifyPKCS1v15(&pubKey,crypto.Hash(0),[]byte(text),signature1)
	if err != nil {
		fmt.Println("签名验证失败")
	}else {
		fmt.Println("签名验证成功")
	}
	err=rsa.VerifyPKCS1v15(&pubKey,crypto.SHA256,hashed[:],signature2)
	if err != nil {
		fmt.Println("签名验证失败")
	}else {
		fmt.Println("签名验证成功")
	}
}


