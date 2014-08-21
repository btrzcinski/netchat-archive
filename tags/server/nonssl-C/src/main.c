#include <sys/types.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <stdio.h>
#include <stdlib.h>
#include <netinet/in.h>
#include <errno.h>
#include <netdb.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <signal.h>
#include <string.h>

#define SERVERPORT 45287

int sockfd;

void sigint_handler(int sig)
{
	printf("Caught sigint, cleaning up.\n");
	close(sockfd);
	exit(0);
}

void handle_client(int clientfd)
{
	close(clientfd);
	return;
}

int main(int argc, char **argv)
{
	signal(SIGINT,sigint_handler);
	fd_set master;
	fd_set read_fds;
	fd_set accept_fds;
	int fdmax;

	FD_ZERO(&master);
	FD_ZERO(&read_fds);

	struct sockaddr_in my_addr, client_addr;
	socklen_t sin_size;

	sockfd = socket(PF_INET, SOCK_STREAM, 0);
	if(sockfd == -1)
	{
		perror("socket");
		exit(1);
	}
	
	my_addr.sin_family = AF_INET;
	my_addr.sin_port = htons(SERVERPORT);
	my_addr.sin_addr.s_addr = INADDR_ANY;
	memset(&(my_addr.sin_zero), '\0', 8);

	if(bind(sockfd, (struct sockaddr *)&my_addr, sizeof(struct sockaddr)) == -1)
	{
		perror("bind");
		exit(1);
	}

	int yes=1;
	if (setsockopt(sockfd,SOL_SOCKET,SO_REUSEADDR,&yes,sizeof(int)) == -1) {
		perror("setsockopt");
		exit(1);
	}

	if(listen(sockfd,10) == -1)
	{
		perror("listen");
		exit(1);
	}

	printf("Accepting and handling connections on port %d.\n",SERVERPORT);

	FD_SET(sockfd,&master);
	fdmax = sockfd;

	int client, i;
	for(;;) {
		read_fds = master;
		if (select(fdmax+1, &read_fds, NULL, NULL, NULL) == -1) {
			perror("select");
			exit(1);
		}

		for(i = 0; i <= fdmax; i++) {
			if(FD_ISSET(i, &read_fds)) {
				if(i == sockfd)
				{
					client = accept(sockfd, (struct sockaddr *)&client_addr, &sin_size);
					if(client < 0)
					{
						perror("accept");
						printf("sockfd was %d at the time.\n",sockfd);
						continue;
					}
					printf("Accepted client (fd#%d): %s\n",client,inet_ntoa(client_addr.sin_addr));
					FD_SET(client, &master);
					if(client > fdmax)
						fdmax = client;
				}
				else
				{
					ssize_t received, sent, sent_total;
					size_t bufsize = 4096;
					char buffer[4096];
					received = recv(i, (void*)buffer, bufsize, 0);
					if(!received)
					{
						printf("Socket with fd %d disconnected.\n",i);
						FD_CLR(i, &master);
						close(i);
					}
					sent_total = 0;
					while(!(sent_total >= received))
					{
						sent = send(i, buffer+sent_total, received-sent_total, 0);
						if(sent == -1)
						{
							FD_CLR(i, &master);
							close(i);
						}
						sent_total += sent;
					}
				}
			}
		}
	}

	exit(0);
}
