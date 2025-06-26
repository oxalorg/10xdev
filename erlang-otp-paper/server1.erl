-module(server1).

-export([start/3, stop/1, rpc/2]).


start(Name, F, State) ->
    register(Name, spawn(fun () ->
				 loop(Name, F, State)
			 end)).

stop(Name) ->
     Name ! stop.

rpc(Name, Query) ->
    Name ! {self(), Query},
    receive
	{Name, Reply} -> Reply
    end.

loop(Name, F, State) ->
    receive
	stop -> void;
	{Pid, Query} ->
	    {Reply, State1} = F(Query, State),
	    Pid ! {Name, Reply},
	    loop(Name, F, State1)
	end.
