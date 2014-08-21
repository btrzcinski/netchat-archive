#include <stdio.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <string.h>

#define PORT 45287
#define BACKLOG 100
#define MAXMSGLEN 1024

int handle_client(struct sockaddr_in client_conn, int client_socketfd, int list_socketfd)
{
	char buf[MAXMSGLEN];
	if(fork() != 0)
	{
		close(client_socketfd);
		return 0;
	}
	while(1)
	{
		int len = recv(client_socketfd, &buf, MAXMSGLEN - 1, 0);
		if(len == -1)
		{
			close(client_socketfd);
			printf("There was an error recieving data.");
			return -2;
		}
		if(len == 0)
		{
				printf("Client disconnected.\n");
				close(client_socketfd);
				close(list_socketfd);
				return 1;
		}
		buf[len] = '\0';
		printf("Client said: %s\n", buf);
		send(client_socketfd, buf, len, 0);
	}

}

int main()
{
	struct sockaddr_in sai;

	int socketfd = socket(PF_INET, SOCK_STREAM, 0);
	if(socketfd == -1)
	{
		printf("There was an error creating the specified socket.");
		return 1;
	}
	
	sai.sin_family = PF_INET;
	sai.sin_port = htons(PORT);
	sai.sin_addr.s_addr = htonl(INADDR_ANY);

	memset(&(sai.sin_zero), '\0', 8);

	int bind_error_code = bind(socketfd, (struct sockaddr*)&sai, sizeof(sai));
	if(bind_error_code != 0)
	{
		printf("There was an error binding to the specified socket on the specified port.");
		return 2;
	}

	int listen_error_code = listen(socketfd, BACKLOG);

	if(listen_error_code != 0)
	{
		printf("There was an error listening with the specified socket on the specified port.");
		return 3;
	}
	
	printf("Accepting connections.\n");

	while(1)
	{
		struct sockaddr_in client_addr;
		unsigned int size = sizeof(struct sockaddr_in);
		int newsfd = accept(socketfd, (struct sockaddr*)&client_addr.sin_addr, &size);
		if(newsfd == -1)
			printf("There was an error while the client was trying to connect.\n");
		else
			printf("New connection recieved from: %s\n", inet_ntoa(client_addr.sin_addr));
		
		int handle_ret = handle_client(client_addr, newsfd, socketfd);
		if(handle_ret != 1 && handle_ret != 0)
			printf("There was an error handling the client. :(\n\n");
		if(handle_ret == 1)
			return 0;
	}
	
	return 0;
}
