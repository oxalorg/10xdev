#include <stdio.h>

// read strings
// eval it

// (let [x 10] (+ x 100))
struct Node {
    char *type; // e.g., "let", "def", etc.
    char *name; // variable name or function name
    int value;  // value for literals
    struct Node *next; // pointer to next node in the list
};

char* read_token(char *code) {
  while (*code == ' ' || *code == '\n' || *code == '\r') {
    code++;
  }
  if (*code == '(' || *code == ')') {
    return &code[0];
  }
  char token[256];
  int i = 0;
  while (*code != NULL && *code != '\0') {
    if (*code == ' ' || *code == '\n' || *code == '\r' || *code == '(' || *code == ')') {
      break;
    }
    token[i++] = *code++;
  }
  token[i] = 0;
  return strdup(token);
}

int tokenize(char *code) {
  switch (code[0]) {
    case '(':
      // Handle opening parenthesis
      return 1; // Placeholder for token type
    case ')':
      // Handle closing parenthesis
      return 2; // Placeholder for token type
    case '+':
      // Handle addition operator
      return 3; // Placeholder for token type
    case '-':
      // Handle subtraction operator
      return 4; // Placeholder for token type
    default:
      if (code[0] >= '0' && code[0] <= '9') {
        // Handle numeric literals
        return 5; // Placeholder for token type
      }
      // Handle identifiers or other tokens
      return 6; // Placeholder for token type
  }
}

int main() {
    // Example Clojure code as a string
    const char *clojure_code = "(let [x 10] (+ x 100))";

    // Print the Clojure code
    printf("Clojure code: %s\n", clojure_code);

    int result = eval(clojure_code);

    printf("Result of evaluating the Clojure code: %d\n", result);

    return 0;
}
