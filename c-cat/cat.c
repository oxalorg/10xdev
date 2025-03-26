#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>

#define BUF_SIZE 1024

int catfd(int fd) {
  char buf[BUF_SIZE] = {0};
  int c = 0;
  do {
    c = read(fd, buf, BUF_SIZE);
    for(int i = 0; i < c; i++) {
      printf("%c", buf[i]);
    }
  } while (c != 0);
  close(fd);
  return 0;
}

int cat1 (char* fp) {
  int fd = open(fp, O_RDONLY);
  if (fd == -1) {
    fprintf(stderr, "cat: %s No such file or directory.", fp);
    return 1;
  }

  return catfd(fd);
}

int main (int argc, char **argv) {
  if (argc < 2) {
    return catfd(0);
  }

  int return_code = 0;
  for(int i = 1; i < argc; i++) {
    return_code = cat1(argv[i]);
  }
  return return_code;
}
