bin/echoserver: obj/main.o
	mkdir -p bin
	gcc -lssl -lcrypto -o bin/echoserver obj/main.o

obj/main.o: src/main.c
	mkdir -p obj
	gcc -c -o obj/main.o src/main.c

clean:
	rm -rf obj
	rm -rf bin

