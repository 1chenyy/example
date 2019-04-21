package main

import (
	"crypto/dsa"
	"crypto/rand"
	"fmt"
	"log"
	_ "test/routers"
)

func main() {
	read:=rand.Reader
	privKey:=new(dsa.PrivateKey)
	err:=dsa.GenerateParameters(&privKey.Parameters,read,dsa.L1024N160)
	if err!=nil {
		log.Fatalln(err.Error())
	}
	err=dsa.GenerateKey(privKey,read)
	if err!=nil {
		log.Fatalln(err.Error())
	}
	fmt.Println("私钥：X =",privKey.X)
	fmt.Println("公钥：Y =",privKey.PublicKey.Y)
	fmt.Println("公共参数：")
	fmt.Println("p = ",privKey.PublicKey.P)
	fmt.Println("q = ",privKey.PublicKey.Q)
	fmt.Println("g = ",privKey.PublicKey.G)
	r,s,err:=dsa.Sign(read,privKey,[]byte("helloworld"))
	if err!=nil {
		log.Fatalln(err.Error())
	}
	fmt.Println("签名结果：\nr=",r)
	fmt.Println("s=",s)

	b:=dsa.Verify(&privKey.PublicKey,[]byte("helloworld"),r,s)
	if b {
		fmt.Println("签名验证成功")
	}else {
		fmt.Println("签名验证失败")
	}

}


