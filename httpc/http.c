#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netdb.h>

#define BACKLOG 20
#define BUFFER_SIZE 1024

int socket_create(int family, int socktype, int protocol) {
  int sockfd = socket(family, socktype, protocol);
  if (sockfd <= 0) {
    perror("Could not setup a tcp socket");
    exit(EXIT_FAILURE);
  }
  return sockfd;
}

int socket_server_listen(char *port) {
  struct addrinfo hints, *result;
  memset(&hints, 0, sizeof hints);
  // TODO: only IPV4 for now, add IPV6 later
  hints.ai_family = AF_INET;
  hints.ai_socktype = SOCK_STREAM;
  hints.ai_flags = AI_PASSIVE;
  getaddrinfo(NULL, port, &hints, &result);

  int sockfd = socket_create(result->ai_family, result->ai_socktype, result->ai_protocol);
  if (bind(sockfd, result->ai_addr, result->ai_addrlen) == -1) {
    perror("Could not bind");
    exit(EXIT_FAILURE);
  }

  if (listen(sockfd, BACKLOG) == -1) {
    perror("Could not listen");
    exit(EXIT_FAILURE);
  }
  printf("Listening on port %s\n", port);
  fflush(stdout);
  return sockfd;
}

int main() {
    char buffer[BUFFER_SIZE] = {0};
    const char *response = "HTTP/1.1 200 OK\nContent-Type: text/plain\nContent-Length: 13\n\nHello world";
    int server_fd = socket_server_listen("7123");

    while(1) {
      struct sockaddr_storage their_addr;
      socklen_t addr_size = sizeof their_addr;
      int incoming_fd = accept(server_fd, (struct sockaddr *)&their_addr, (socklen_t *)&addr_size);
      read(incoming_fd, buffer, BUFFER_SIZE);
      printf("Received request:\n%s\n", buffer);
      write(incoming_fd, response, strlen(response));
      close(incoming_fd);
    };

    return 0;
}
