package main

import (
	"context"
	"fmt"
	"google.golang.org/grpc"
	"log"
	"net"
	"web/grpc/helloworld"
)

type server struct {

}

func (s *server)SayHello(ctx context.Context, in *helloworld.HelloRequest) (*helloworld.HelloReply, error){
	fmt.Println("received:",in.Name)
	return &helloworld.HelloReply{Msg:"hello " + in.Name},nil
}

func main() {
	lis,err:=net.Listen("tcp",":1234")
	if err!=nil {
		log.Fatal("listen:",err.Error())
	}
	s:=grpc.NewServer()
	helloworld.RegisterGreeterServer(s,&server{})
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
