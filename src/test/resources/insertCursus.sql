#Onderstaand in comment is hoe je ingeeft als het om single table gaat
#insert into cursussen(naam, van, tot, soort)
#values ('testGroep', '2018-01-01', '2018-01-01', 'G');
#insert into cursussen(naam, duurtijd, soort) values ('testIndividueel', 3, 'I');

#Hieronder is dus voor de seperate columns bij inheritance classes TECHNIEK:
# Je steekt waarde in abstracte class
# Je steekt waarde in subclas (zoek het id op door subquery, gegeven ad waarde die je in abstracte class stak)
#Hier gaat uiteindelijk iets in groepscursus
insert into cursussen(naam) values('testGroep');
insert into groepscursussen(id, van, tot)
values((select id from cursussen where naam = 'testGroep'),'2018-01-01','2018-01-01');
#Hier gaat uiteindelijk iets in individuele cursus
insert into cursussen(naam) values('testIndividueel');
insert into individuelecursussen(id, duurtijd)
values((select id from cursussen where naam = 'testIndividueel'), 3);
