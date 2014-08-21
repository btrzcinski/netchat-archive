bin/echoserver: obj/main.o
	mkdir -p bin
	gcc-3.4 -lssl -lcrypto -o bin/echoserver obj/main.o

obj/main.o: src/main.c
	mkdir -p obj
	gcc-3.4 -c -o obj/main.o src/main.c

clean:
	rm -rf obj
	rm -rf bin

