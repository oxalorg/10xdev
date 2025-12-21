import collections

with open("input1.txt", 'r') as f:
    lines = f.readlines()

moves = [line.strip() for line in lines]

dial = 50
password = 0
password2 = 0

for move in moves:
    dir, amount = move[0], int(move[1:])
    old_dial = dial
    for _ in range(amount):
        if dir == 'L':
            dial -= 1
            dial = dial % 100
        else:
            dial += 1
            dial = dial % 100
        # print(dir, amount, " Dial from : ", old_dial, " --> ", dial)
        if dial == 0:
            password += 1

print(password)
