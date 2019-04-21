package main

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/pem"
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
	pubKey := privKey.PublicKey

	encodePrivKey := x509.MarshalPKCS1PrivateKey(privKey)
	block1:=pem.Block{
		Type:  "RSA PRIVATE KEY",
		Bytes: encodePrivKey,
	}
	pem.Encode(os.Stdout,&block1)

	encodePubvKey := x509.MarshalPKCS1PublicKey(&pubKey)
	block2:=pem.Block{
		Type:  "RSA PUBLIC KEY",
		Bytes: encodePubvKey,
	}
	pem.Encode(os.Stdout,&block2)
}


