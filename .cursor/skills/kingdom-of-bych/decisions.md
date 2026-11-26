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

## Příkazy

Potvrzeno:

- Příkazy jsou ve skupinách: `/kbych time status`, `/kbych time setday <den>`, `/kbych time night`, `/kbych test reset`, `/kbych config reload`, `/kbych game depart`.
- Staré krátké tvary (`/kbych status`, `setday`, `night`, `reset`, `reload`) zůstávají kvůli command blokům.

## Epochy

Potvrzeno:

- Doby se přepínají podle **odehraného času** a **achievementů**, ne podle dne světa. (Staré dny 1 / 2–4 / od 5 už neplatí.)
- Achievement při přechodu má zvuk a odměnu.
- Středověk není světová epocha. Patří jednomu hráči a ve fázi 1 se nepočítá.
- Ve fázi 1 čas běží od založení světa. Výpočet má být na jednom místě. Až bude autobus, stejné místo se přepne na odlet. Do té doby to nepřepínat.
- Při přechodu na další dobu je vždy vlastní loading screen, aby se svět stihl načíst a hra se nezasekla. Platí i pro odemčení středověku jednomu hráči. Když to pro jednoho hráče technicky nepůjde, zeptej se, než to nahradíš jinak.
- Pořadí práce: nejdřív nové hodiny (odehraný čas), potom loading screen a hezčí oznámení.

`NEVYPLNĚNO` — zeptej se před kódem hodin:

- po kolika minutách odehraného času přijde bronz a po kolika železo (návrh byl asi 10 a +20, nepotvrzeno)
- co přesně je odměna u achievementu
- jestli se odehraný čas počítá jen když je hráč ve světě / online, nebo i jinak

## Spánek

Potvrzeno. Noc se přeskočí, až platí obojí najednou:

- V posteli je aspoň 50 procent hráčů (`sleep.required-player-percent` = 50).
- Spánek trvá aspoň 20 sekund (`sleep.required-seconds` = 20). Důvod: hráč si jen tak neodskočí a nepřeskočí celou noc.

Obě čísla patří do ovládacího souboru a půjdou změnit. Nula sekund by znamenala, že se čas navíc nečeká. Výchozí nula není.

## Výroba podle doby

Potvrzeno při zkoušce. Jde o výrobu i použití té věci.

- Doba kamenná: nic z bronzu, mědi ani železa. Tedy ani vozík, železné dveře, bleskosvod, měděné slitky a měděné bloky.
- Doba bronzová: měď a bronz jdou. Z železa pořád nejde nic, včetně vozíku a železných dveří. Redstone nejde.
- Doba železná: železo, měď i bronz jdou. Redstone pořád nejde.
- Středověk: redstone jde. Zatím ho nikdo nemá, odemkne se až vesnicí, každému zvlášť.
- Zlato, diamant a netherit jdou jen ve středověku. V ostatních dobách ne.
- Z kůže se dá vyrábět v každé době.
- Tlačítka jdou mačkat i pokládat v každý den.
- Křesadlo, ohniště a nalezený trident zůstávají povolené.
- Pec v době kamenné: minihra s myší (citlivost a klik vedle). Ohniště se neblokuje.
- Sedlo v době kamenné nejde použít.

Obecné pravidlo od Františka: pokročilejší věci se necraftí ani nepoužívají. Konkrétní věc, která není v seznamu zákazů ani výjimek, je `NEVYPLNĚNO`. Před přidáním dalšího zákazu se zeptej. Nezařazuj tam sám lodě, luky, štíty a podobně.

## Bronz

Potvrzeno:

- Bronzová zbroj je v boji jako železná.
- Každá zbroj hráče v boji trochu zpomaluje. Bronzová skoro vůbec. Diamantová asi jako železná. Přesná čísla při kódu, vše v ovládacím souboru.
- Z bronzu: nástroje, meč, celá zbroj, blok a malý lehký štít.
- Výroba: vlastní tavící pec (ne obyčejná pec). Přesný vzhled a okno je `NEVYPLNĚNO`.
- 1 bronzový slitek = 4 měď + 1 cín.
- Ruda cín v kameni jako železo, jiná barva. Četnost cínu přibližně jako měď (při celkově snížených rudech).
- Výskyt žil vs vanilla: diamant ~5 %, zlato ~30 %, železo ~40 %.
- Velikost žíly: diamant nejčastěji 1, občas 2, max 3. Zlato a železo jako vanilla.
- Celé diamantové brnění má být skoro nemožné.
- Všechny důležité herní hodnoty patří do ovládacího souboru.

`NEVYPLNĚNO` — zeptej se před kódem bronzu:

- jak přesně vypadá a funguje tavící pec
- přesná čísla zpomalení zbroje
- přesné vlastnosti malého lehkého štítu

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
