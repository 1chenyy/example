package main

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/pem"
	"fmt"
	"io/ioutil"
	"log"
	"os"
	_ "test/routers"
)

func main() {
	read:=rand.Reader
	privKey,err:=rsa.GenerateKey(read,1024)
	if err != nil{
		log.Fatalln(err.Error())
	}

	encodePrivKey := x509.MarshalPKCS1PrivateKey(privKey)
	block1:=pem.Block{
		Type:  "RSA PRIVATE KEY",
		Bytes: encodePrivKey,
	}
	file, err := os.Create("priv.pem")
	pem.Encode(file,&block1)

	key,err:=ioutil.ReadFile("priv.pem")
	block,_:=pem.Decode(key)
	if block ==nil {
		log.Fatalln("nil")
	}
	privkey2,err:=x509.ParsePKCS1PrivateKey(block.Bytes)
	fmt.Println(privKey.D.Cmp(privkey2.D)==0)
	fmt.Println(privKey.N.Cmp(privkey2.N)==0)
}


