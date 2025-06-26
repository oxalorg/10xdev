-module(server2).

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
	{Name, crash} -> exit(rpc);
	{Name, ok, Reply} -> Reply
    after 10000 -> exit(timeout)
    end.

loop(Name, F, State) ->
    receive
	stop -> void;
	{Pid, Query} ->
	    case(catch F(Query, State)) of
		{'EXIT', Why} ->
		    log_error(Name, Query, Why),
		    Pid ! {Name, crash},
		    loop(Name, F, State);
		{Reply, State1} ->
		    Pid ! {Name, ok, Reply},
		    loop(Name, F, State1)
		end
	end.

log_error(Name, Query, Why) ->
    io:format("Server ~p query ~p caused exception ~p~n",
	      [Name, Query, Why]).
