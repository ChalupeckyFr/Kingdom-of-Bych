# Rozhodnutí Kingdom of Bych

Soubor pro všechny agenty. Platí jen to, co je tady napsané jako potvrzené. `NEVYPLNĚNO` znamená zeptej se Františka, až se ten systém bude dělat. Nevymýšlej to.

Technické preference použij. Když v praxi nedávají smysl, zeptej se, jestli je smíš nepoužít. Nejsou to zákony a neporušuj je potichu.

## Už platí z dřívějšího zadání

- Minecraft 1.21.1, Fabric Loader, Fabric API, Java 21.
- Server rozhoduje. Vzdálené armády, vesnice a karavany se nepočítají jako fyzické entity, dokud nejsou blízko hráče. Upřesnění vzdálenosti je níž v oddílu Optimalizace.
- Oddíl = 1 vlajkonoš + nejvýš 9 vojáků. Složitou cestu počítá jen vlajkonoš. Vojáci drží formaci kolem něj.
- Formace jsou šipka, obdélník a kruh.
- Povolání oddílu: útok na blízko, luk nebo kuše, kopí na větší dálku. Celý oddíl má jedno povolání.
- Hra začíná v lobby s world borderem, pak autobus (šablona bloků na plynule se pohybující entitě) a seskok padákem. Herní čas od odletu autobusu. Ve fázi 1 autobus ještě není, viz Epochy.
- Středověk se odemyká každému hráči zvlášť, když obsadí nebo založí vesnici.
- Mírumilovná vesnice: obchod výměnou surovin, bydlení na přiděleném pozemku.
- Nepřátelská vesnice: zavírá brány a střílí.
- Stavební stanice: blok s NPC stavitelem, hopper zezadu z truhel, hráč vybere šablonu, stavba jde z dodaných surovin.
- Mezi městy jezdí karavany s ochrankou a vzácnými surovinami.

## Komunikace a kód

- S Františkem česky, jednoduše.
- Kód anglicky, balíček `com.bych`. Nepřejmenovávat na `com.kingdomofbych`.
- Podrobné komentáře ve zdroji česky.
- Chat a okna pro hráče česky.
- Fungující kód nemazat bez souhlasu.

## Epochy

Potvrzeno:

- Den 1 je doba kamenná.
- Dny 2, 3 a 4 jsou doba bronzová.
- Od dne 5 trvale doba železná.
- Středověk není světová epocha. Patří jednomu hráči a ve fázi 1 se nepočítá.
- Ve fázi 1 čas běží od založení světa. Výpočet má být na jednom místě. Až bude autobus, stejné místo se přepne na odlet. Do té doby to nepřepínat.
- Při přechodu na další dobu je vždy vlastní loading screen, aby se svět stihl načíst a hra se nezasekla. Platí i pro odemčení středověku jednomu hráči. Když to pro jednoho hráče technicky nepůjde, zeptej se, než to nahradíš jinak.

## Spánek

Potvrzeno. Noc se přeskočí, až platí obojí najednou:

- V posteli je aspoň 50 procent hráčů (`sleep.required-player-percent` = 50).
- Spánek trvá aspoň 20 sekund (`sleep.required-seconds` = 20). Důvod: hráč si jen tak neodskočí a nepřeskočí celou noc.

Obě čísla patří do ovládacího souboru a půjdou změnit. Nula sekund by znamenala, že se čas navíc nečeká. Výchozí nula není.

## Doba kamenná, zákazy

Potvrzené zákazy. Nesmí se craftit ani používat:

- železné nástroje, zbraně a brnění
- zlaté nástroje, zbraně a brnění
- diamantové nástroje, zbraně a brnění
- netheritové nástroje, zbraně a brnění
- bronzové nástroje
- měděné nástroje
- sedla
- pece
- cokoliv z redstonu

Povolené výjimky:

- ohniště (campfire)
- křesadlo
- trident, když ho hráč najde (smí ho použít)

Když hráč pec v době kamenné otevře (pec, tavicí pec, udírna), v okně se mění citlivost myši a klik dopadne jinam. Nestačí jen chat. Ohniště se neblokuje. Minihra je hotová.

Obecné pravidlo od Františka: pokročilejší věci se necraftí ani nepoužívají. Konkrétní věc, která není v seznamu zákazů ani výjimek, je `NEVYPLNĚNO`. Před přidáním dalšího zákazu se zeptej. Nezařazuj tam sám lodě, luky, štíty a podobně.

## Bronz

Potvrzeno:

- Bronzová zbroj je v boji jako železná.
- Každá zbroj hráče v boji trochu zpomaluje. Bronzová skoro vůbec.
- Surovin na jinou zbroj má být tak málo, že je velmi těžké ji získat. Mají se omezit žíly rud.

`NEVYPLNĚNO` — zeptej se před laděním rud a bronzu:

- o kolik která zbroj zpomaluje
- maximální velikost žíly a které rudy
- jestli omezení udělá předgenerování světa (nápad s nástrojem Chunky a výměnou rud) nebo jiná mapa. Obojí je jen nápad, ne vybrané řešení.

## Mapa, roh a bitva

Potvrzeno:

- Craftěná mapa je skutečný předmět. Ukazuje blízké okolí hráče, jeho armádu a jeho vlajkonoše.
- Položí se na blok. Po kliknutí se přehraje animace, že se nad ni hráč sklonil, objeví se tematický kurzor a armáda se dá ovládat.
- Na mapě se mění seskupení oddílů. Jednotky se dají táhnout tam, kam mají jít, když začne bitva.
- Formaci (šipka, obdélník, kruh) určuje hráč, když určuje vlajkonoše. Na bitvu ji může na mapě dočasně změnit.
- Bitvu startuje roh. Každý hráč si roh musí označit. Od té chvíle ho má ve zvláštním slotu vedle brnění, jako u pasu.
- Druhé zatroubení bitvu ukončí. Jednotky se stáhnou a čekají na další rozkaz.
- Civilizaci na mapě vidí jen hráč, který ji sám objevil.
- Kupce (karavanu) hráč vidí jen když jede od civilizace, kterou objevil, k jiné civilizaci, kterou taky objevil.

`NEVYPLNĚNO` — zeptej se před mapou:

- jestli se mapa pokládá na speciální blok, nebo kamkoliv. František řekl, že to se může vymyslet později.
- jak se značí prozkoumané okolí

## Optimalizace vzdálenosti

Potvrzeno:

- Budovy a infrastruktura se načtou už s chunkem, aby bylo vidět, kam hráč jde.
- Entity, NPC a stráže vzniknou až v nějaké vzdálenosti od hráče. Po odchodu se zase stanou daty.
- Přesná vzdálenost v blocích je `NEVYPLNĚNO`. Zeptej se před armádami, vesnicemi a karavanami.

## Lobby a autobus

Potvrzeno:

- World border v lobby je malý, asi 32 bloků od středu.
- Odlet spustí hráč s oprávněním, příkazem `/kbych game depart`.
- Zatím příkaz jen napíše „Odlet!“, uvolní lobby border a nastaví den 1. Let autobusu ještě není.
- Autobus má působit středně rychle, ať cesta netrvá věčnost a není to jen prohlídka.
- Autobus kreslit jako plynulé bloky na jedné řídící entitě.

`NEVYPLNĚNO` — zeptej se před autobusem: přesné bloky za sekundu.

`NEVYPLNĚNO` — zeptej se před padákem: jak padák vypadá a jak padá.

## Vesnice, daň, civilizace, stavitel

Potvrzeno:

- Daň je 10 procent předmětů, každých 7 dní, z truhel na přiděleném pozemku, do královské pokladnice pod hlavní budovou.
- První podoba civilizace jsou jen chýše s ohništěm uprostřed.
- Stavitel pokládá bloky postupně, ne celou budovu najednou.
- Trvalé věci (epocha světa, středověk jednotlivých hráčů, pokladnice) se ukládají do dat světa.

`NEVYPLNĚNO` — zeptej se až u daného systému:

- jak se bronz vyrábí (pec, poměr mědi a cínu)
- kde a jak často se generuje cín
- jak vypadají šablony budov a kdo je přidává
- co přesně znamená obsadit vesnici
- jak je pokladnice chráněná
- co přesně vezou karavany
- jak se vycvičuje armáda

## Technické preference

Potvrzené, s právem se zeptat, když nedávají smysl:

- data světa pro trvalý stav
- autobus jako plynulé bloky na jedné entitě
- stavba po blocích
- vzdálené živé věci jako data, budovy s chunkem (viz Optimalizace)
- nemazat fungující kód bez souhlasu

Nezávazné návrhy z příloh, které František nepotvrdil:

- balíček `com.kingdomofbych` (neplatí, platí `com.bych`)
- zákaz měnit verze jako nedotknutelné pravidlo (verze neměň sám, ale když to nejde, zeptej se)
- výhradně CustomPayload. Až se bude dělat síť, zvol způsob, který v Minecraftu 1.21.1 dává smysl, a když bys ho chtěl nepoužít, zeptej se.
