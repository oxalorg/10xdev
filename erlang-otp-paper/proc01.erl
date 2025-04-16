-module(proc01).
-export([start/0, loop/0, main/0]).

start() ->
    spawn(fun loop/0).

loop() ->
    io:format("Hello, World!~n"),
    timer:sleep(10000),
    loop().

main() ->
    Pid = start(),
    io:format("Press Enter to terminate the process...~n"),
    io:get_line(""),
    exit(Pid, normal).
