package main

import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"log"
	"time"
	"web/grpc/helloworld"
)

func main() {
	conn,err:=grpc.Dial("127.0.0.1:1234",grpc.WithInsecure())
	if err!=nil {
		log.Fatal("dial:",err.Error())
	}
	defer conn.Close()

	c:=helloworld.NewGreeterClient(conn)

	ctx,cancel:=context.WithTimeout(context.Background(),time.Second)
	defer cancel()

	r,err:=c.SayHello(ctx,&helloworld.HelloRequest{Name:"world"})
	if err!=nil {
		log.Fatal("dial:",err.Error())
	}
	fmt.Println("greeting",r.Msg)
}
