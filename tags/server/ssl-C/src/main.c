/* ALL RIGHTS ARE RESERVED FOR THIS CODE
 * YOU MAY NOT REDISTRIBUTE, MODIFY, OR USE THIS CODE WITHOUT THE PERMISSION OF THE NETCHAT DEVELOPMENT TEAM.
 *
 * Copyright(C) 2006 Barnett Trzcinski. All rights reserved.
 */

#include <sys/types.h>
#include <sys/socket.h>
#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <unistd.h>
#include <signal.h>
#include <string.h>

#include <openssl/bio.h>
#include <openssl/ssl.h>
#include <openssl/err.h>

#define SERVERPORT 45287

BIO * sbio; /* server (SSL) BIO */
BIO * abio; /* accept BIO */
BIO * bbio; /* buffer BIO */

void sigint_handler(int sig)
{
	printf("Cleaning up.\n");
	BIO_free_all(abio);
	exit(0);
} /* end sigint_handler */

int main(int argc, char **argv)
{
	int verbose_bool = 0;
	if(argc > 1)
	{
		if(strcmp(argv[1],"--verbose") == 0)
		{
			verbose_bool = 1;
		}
	}
	/* variable declaration */
	BIO * cbio; /* client BIO */
	char buf[1024];
	unsigned read, written, tmp;

	/* adds a handler for SIGINT (ctrl+c) */
	signal(SIGINT,sigint_handler);
	/* adds a handler for SIGTERM (kill without -9) */
	/* SIGKILL cannot be caught */
	signal(SIGTERM,sigint_handler);
	
	/* openssl initialization */

	SSL_library_init();
	SSL_load_error_strings();
	ERR_load_BIO_strings();
	ERR_load_crypto_strings();
	OpenSSL_add_all_algorithms();

	/* load SSL stuff */
	SSL_CTX *ctx = SSL_CTX_new(SSLv23_server_method());

	if(!ctx)
	{
		fprintf(stderr,"Error creating new SSL_CTX\n");
		ERR_print_errors_fp(stderr);
		return 1;
	}
	
	if(verbose_bool) {
		printf("Loading certificates and private key...");
		fflush(stdout);
	}
	
	if(!SSL_CTX_use_certificate_file(ctx,"certs/server.pem",SSL_FILETYPE_PEM))
	{
		fprintf(stderr,"Error loading certificate file\n");
		ERR_print_errors_fp(stderr);
		return 1;
	}
	
	if(verbose_bool) {
		printf("[cert] ");
		fflush(stdout);
	}
	
	if(!SSL_CTX_use_PrivateKey_file(ctx,"keys/server.pem",SSL_FILETYPE_PEM))
	{
		fprintf(stderr,"Error loading private key file\n");
		ERR_print_errors_fp(stderr);
		return 1;
	}
	
	if(verbose_bool) {
		printf("[privkey] ");
		fflush(stdout);
	}
	
	if(!SSL_CTX_check_private_key(ctx)) {
		fprintf(stderr,"Error checking private key on CTX\n");
               ERR_print_errors_fp(stderr);
               return 0;
        }

	if(verbose_bool) {
		printf("[verify] ");
		fflush(stdout);
		printf("OK\n");
	}


	/* set up connection string for BIO
	 * form --> "host:port" 
	 * where either can be * indicating that any host/port will be used */
	char acceptportbuf[8]; /* buffer length 8: maximum is when "*:65535" which is strlen 7 but total 8 */
	sprintf(acceptportbuf,"*:%d",SERVERPORT);

	if(verbose_bool) {
		printf("Establishing SSL BIOs...");
		fflush(stdout);
	}

	sbio = BIO_new_ssl(ctx,0);
	SSL *ssl;
	BIO_get_ssl(sbio, &ssl);
	if(!ssl)
	{
		fprintf(stderr,"Problems with SSL pointer\n");
		return 1;
	}
	SSL_set_mode(ssl, SSL_MODE_AUTO_RETRY);
	
	if(verbose_bool) {
		printf("OK\n");
		printf("Establishing accept BIO...");
	}

	abio = BIO_new_accept(acceptportbuf);

	if(verbose_bool) {
		printf("OK\n");
		printf("Chaining accept and SSL BIOs...");
	}

	BIO_set_accept_bios(abio,sbio);
	
	if(verbose_bool)
		printf("OK\n");

	/* first call to do_accept just sets it up */
	if(BIO_do_accept(abio) <= 0) {
		fprintf(stderr, "Error setting up accept\n");
		ERR_print_errors_fp(stderr);
		exit(1);
	} /* endif(BIO_do_accept(b) <= 0) */

	printf("Server started and accepting connections (connstring: %s).\n",acceptportbuf);
	/* infinite loop for new connections */
	for(;;)
	{
		/* grab new connection */
		if(BIO_do_accept(abio) <= 0) {
			fprintf(stderr, "Error accepting connection\n");
			ERR_print_errors_fp(stderr);
			exit(1);
		} /* endif(BIO_do_accept(b) <= 0) */
		printf("Connection accepted...");
		fflush(stdout);
		cbio = BIO_pop(abio);
		printf("SSL handshake...");
		fflush(stdout);
		if(BIO_do_handshake(cbio) <= 0) {
			fprintf(stderr, "Error in SSL handshake\n");
	               ERR_print_errors_fp(stderr);
		       BIO_free_all(cbio);
        		continue;
		}
		printf("Connection established\n");
		/* do our I/O loop, for now with a max of 1K transferred back/forth */
		int connbroken = 0;
		/* inner for(;;) for clients */
		for(;;)
		{
			/* little flag for broken connection */
			if(connbroken)
				break; /* breaks the inner for(;;) above */
			/* check for read error */
			if((read = BIO_read(cbio, buf, 1024)) <= 0)
			{
				fprintf(stderr, "Error during read: client disconnected\n");
				BIO_free_all(cbio);
				connbroken = 1;
				continue;
			} /* endif((read = BIO...) <= 0) */
			/* write loop to ensure complete write; may send incomplete message */
			written = 0;
			while(written < read)
			{
				tmp = BIO_write(cbio, buf, read-written);
				if(tmp <= 0)
				{
					fprintf(stderr, "Error during write: client disconnected\n");
					BIO_free_all(cbio);
					connbroken = 1;
					break; /* break the while(written < read) loop, which immediately heads to
					        * if(connbroken) */
				} /* endif(tmp <= 0) */
				written += tmp;
			} /* endwhile(written < read) */
		} /* endfor(;;) */
	} /* endfor(;;) */

	return 0;
} /* end main */

