%dummy facts for testing
/*
%taxi(id,avail,[pass],[lang],rating,longdist,type,descr)
taxi(100,yes,[1,4],[greek],9.2,yes,subcompact,'Πατήσια').
taxi(110,no,[1,4],[greek,english],8.3,yes,subcompact,'Φάληρο').
taxi(120,yes,[1,4],[greek],8,no,compact,'Βύρωνας').
taxi(130,yes,[1,4],[greek,english],9.1,no,large,'Μαρούσι').

taxi(140,yes,[1,4],[greek],7.3,no,large,'Φιλοθέη').
taxi(150,yes,[1,4],[greek],6.3,no,subcompact,'Βουλιαγμένης').
taxi(160,no,[5,8],[greek,english],8.8,no,minivan,'Νέος Κόσμος').
taxi(170,yes,[1,4],[greek,english],7.1,yes,large,'Αμπελόκηποι').
taxi(180,yes,[1,4],[greek],5,no,subcompact,'Γουδή').
taxi(190,yes,[5,8],[greek,english],6.6,yes,minivan,'Πετρούπολη').
taxi(200,yes,[1,4],[greek,english],8,yes,large,'Ζωγράφου').
taxi(210,yes,[1,4],[greek],9.2,yes,compact,'Γκύζη').
taxi(220,yes,[1,4],[greek,english],7.2,no,large,'Πεδίο Άρεως').
taxi(230,yes,[1,4],[greek,english],9.8,yes,large,'Παγκράτι').
taxi(240,no,[1,4],[greek,english],8.9,yes,large,'Καισαριανή').
taxi(250,yes,[5,8],[greek,english],9.5,yes,minivan,'Γκάζι').
        

%           p  lang luggage        
client(9.19,3,greek,0).
*/

/*
line(5168803,yes,pedestrian,[yes,3,60,nn,nn,nn,nn,nn],[nn,nn,nn,nn,nn,nn]).
client(9.19,3,greek,0).
taxi(100,yes,[1,4],[greek],9.2,yes,subcompact,'Πατήσια').
belongsTo(362369064,5168803).
belongsTo(838455793,5168803).
belongsTo(36644983,5168803).
belongsTo(838455934,5168803).
belongsTo(3916406201,5168803).
belongsTo(111,5168803).
belongsTo(36644980,5168803).

traffic(5168803,[[9.0,11.0,high],[13.0,15.0,medium],[17.0,19.0,high]]).

next(362369064,838455793,11,12).
next(362369064,111,21,22).

next(838455793,36644983,31,32).
next(838455793,36644980,34,35).

next(36644983,838455934,33,34).
next(838455934,3916406201,35,36).
next(3916406201,838455850).
next(838455850,360382103).
next(360382103,2514283314).
next(2514283314,1992827871).
*/

% *************************************************** RULES for taxis *********************************************

typeEval(minivan,0.2).
typeEval(subcompact,0.1).
typeEval(compact,0.3).
typeEval(large,0.4).



itfits(Cl,[X,Y]):-Cl>=X, Cl=<Y.
%    valto an thewrisoume oti kanei mono megales apostaseis
isLongDist(_,yes).
isLongDist(Dist,no):-Dist<30.

cancurry(_,minivan).
cancurry(L,subcompact):-L=<2.
cancurry(L,compact):-L=<3.
cancurry(L,large):-L=<4.
% tin kaleis isSuitable(Ids,<to distance tou A*>) sou epistrefei ola ta taksi pou pliroun tis proipotheseis
isSuitable(Tid,Dist):-taxi(Tid,yes,Capacity,LangList,_,Long,Type,_),client(_,Clients,Lang,Bagaz),
          member(Lang,LangList),itfits(Clients,Capacity),isLongDist(Dist,Long),
          cancurry(Bagaz,Type).



%kaleis tin final2(<lista me ids>,R)
%s epistrefei [[id1,Rating],[ ],[ ]]
 
rate(X,[X,R]):-taxi(X,_,_,_,R1,_,Type,_),typeEval(Type,R2), R is R1+R2.

final2(List,Res):-final(List,[],Res).

final([],Res,Res).
final([X|Y],Acc,Rev):-rate(X,X1),final(Y,[X1|Acc],Rev).

%vriskoume to zeugari <id,rating>
%me to minimum rating 
%(benoun adistrofa ston accumulator parakato)
my_min([], R, R). 
my_min([[X,Y]|Xs], [_,Y1], R):- Y =<  Y1, my_min(Xs, [X,Y], R). 
my_min([[_,Y]|Xs], [X1,Y1], R):- Y > Y1, my_min(Xs, [X1,Y1], R).
my_min([X|Xs], R):- my_min(Xs, X, R). 


%taksinomoume anadromika ta ids vasei tou rating
sortids2(L,R):-sortids(L,[],R).
sortids([],Res,Res).
sortids(Lista,Acc,Res):-my_min(Lista,[X1,Y1]),delete(Lista,[X1,Y1],Rest),sortids(Rest,[X1|Acc],Res).

% auti kaloume me orisma L: ta ids twn apodektwn taksi k R to apotelesma
ranking2(L,R):-final2(L,R1),sortids2(R1,R).

ranking1(L,R):-sortids2(L,R1),reverse(R1,R).

% *************************************************** RULES for streets *********************************************

%pedestrian,service -> permit + input_list
permit_list([motorway,trunk,primary,secondary,tertiary,unclassified,residential,motorway_link,trunk_link,primary_link,secondary_link,tertiary_link,living_street,track,bus_guideway,escape,raceway,road]).
prio_list([motorway,trunk,primary,secondary,tertiary,motorway_link,trunk_link,primary_link,secondary_link,tertiary_link]).
inputlist3([unclassified,residential,living_street,track,bus_guideway,escape,raceway,road]).

nn.

%kinisi tou dromoun
place(N,[[X,Y,Z]|T],R) :-
	  ( N>=X, N=<Y
	-> R=Z
	; place(N,T,R)
	).

place(_,[],R):-R=nn.
place2(N1,N2,R):-traffic(N2,Z),place(N1,Z,R). 


checkLineAccess([nn]).
checkLineAccess([X|Y]):-
				(X = nn
				->  checkLineAccess(Y)).



% call it like canMoveFromTo(id,_X1,Y1) to get cords of the nodes you can move to
%returns false otherwise

canMoveFromTo(X,Y,X1,Y1):-next(X,Y,X1,Y1),belongsTo(X,Line),line(Line,_,H,_,A),
                          permit_list(L1),member(H,L1),checkLineAccess(A).





%id,oneway,highway,
%[lit,lanes,maxspeed,tunnel,bridge,incline,busway,toll]
%   +   +      +                             
/*lit: yes: 0.0002
lanes: 1,2: 0.0001
		3,4: 0.0002
		>=5 : 0.0003

maxspeed: =<30: 0
		  30-60: 0.0002
		  >60 : 0.0003
traffic: low:0.0003
		 medium: 0.00015

*/

%[railway,boundary,access,natural,barrier,waterway]
%line(id,oneway,highway,[],[]).


%priority(lineid,client_time,Z)
%call it like priority(lid,_,Z).

%keeps important values from a line's prio list


mysplit([X,Y,Z],[X,Y,Z|_]).



priority(R,T,Z):- client(T,_,_,_),place2(T,R,Res),line(R,_,H,PL,_),
                  mysplit(Lista,PL),calc([H,Res|Lista],Values),suml(Values,Z),!.


%takes a list and returns list of values

calc([],[]):-!.
calc([nn|T],R):-calc(T,R).

calc([X|T],[0.0002|R]):-prio_list(Z),member(X,Z),calc(T,R).
calc([X|T],R):-inputlist3(Z),member(X,Z),calc(T,R).

calc([low|T],[0.0006|R]):-calc(T,R).
calc([medium|T],[0.0003|R]):-calc(T,R).
calc([high|T],[-0.0006|R]):-calc(T,R).
calc([yes|T],[0.0002|R]):- client(Time,_,_,_),Time>=19,calc(T,R).
calc([yes|T],R):-calc(T,R).
calc([X|T],[V|R]):- calc(T,R),number(X),(X=<2->V=0.0001;X<5->V=0.0002;X>=30,X<60->V=0.0002;X>=5->V=0.0003;X>=60->V=0.0003;V=0).

%sum the values up
suml([],0.0).
suml([R],R).
suml([X|Y],R1):- suml(Y,R), R1 is R+X.



