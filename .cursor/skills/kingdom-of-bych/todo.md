# Úkoly Kingdom of Bych

Soubor pro Františka i pro agenty. Jeden agent bere jeden úkol. Hotový úkol odškrtne a nové rozhodnutí zapíše do [decisions.md](decisions.md).

`NEVYPLNĚNO` se nesmí doplnit odhadem. Řádek označený **čeká** se neprogramuje, dokud František neodpoví. Řádek **návrh** není rozhodnutí.

## Jak pouštět víc agentů

Projekt má vlastní git ve složce hry. Každý agent pracuje na vlastní větvi. Dva agenti nesmí psát do stejných souborů.

| Úkol | Větev | Složka, do které smí psát |
| --- | --- | --- |
| Minihra s pecí | `furnace` | `src/main/java/com/bych/epoch` a klientská složka, jen pec |
| Lobby bez autobusu | `lobby` | `src/main/java/com/bych/game` |

Epochy, oznámení, playtime, bronz, vesnice a autobus se nezačínají, dokud František nevybere návrh níž. Tyhle úkoly sahají do stejných hodin hry.

## Hotovo

- [x] Ovládací soubor, spánek 50 % a 20 sekund, epochy podle dne
- [x] `/kbych status`, `setday`, `night`, `reset`, `reload`
- [x] Výroba podle doby: kámen bez mědi, bronzu a železa, bronz bez železa a redstonu, železo bez redstonu, zlato diamant a netherit až ve středověku, kůže pořád, tlačítka pořád
- [x] Noc aktuálního dne příkazem `kbych night`
- [x] Příkazy do skupin: `/kbych time …`, `/kbych test reset`, `/kbych config reload` (staré krátké tvary zůstaly)
- [x] Lobby: world border asi 32 bloků od středu a `/kbych game depart` (hláška Odlet!, uvolnění borderu, den 1). Let autobusu ještě není.

## Připraveno, smí se začít

### 1. Minihra s pecí v době kamenné

Už je rozhodnuté. Když hráč v době kamenné pec otevře, v okně se mění citlivost myši a klik dopadne jinam. Nestačí jen věta v chatu. Ohniště se neblokuje. Po změně napsat, jak to vyzkoušet.

## Návrhy, nezačínat v kódu

František vybere jednu možnost. Do `decisions.md` se zapíše až jeho věta.

### Oznámení doby, vybrat jednu cestu

Dnes doba naskočí podle dne světa a do chatu přijde jedna věta.

- **A. Nechat dny.** Jen zkrášlit oznámení: velký nápis, zvuk, krátká odměna. Délky dob se nemění.
- **B. Přejít na odehraný čas a achievementy.** Návrh z zkoušky: asi 10 minut hry bronz, dalších 20 minut železo. Achievement má zvuk a odměnu. Přesné minuty a odměna jsou pořád **čeká**, dokud je František neřekne. Tahle cesta zruší počítání podle dne, proto se nesmí míchat s úkolem lobby ani s načítací obrazovkou, dokud není vybraná.

Načítací obrazovka při změně doby je rozhodnutá, ale čeká na tuhle volbu. Jinak by se napojila na hodiny, které se možná změní.

### Bronz jako věc ve hře

Směr je rozhodnutý: zbroj jako železo, skoro bez zpomalení, ostatní zbroj hráče zpomaluje, rudy mají být vzácné. Než se to programuje, František doplní:

- jak se bronz vyrábí, pec a poměr mědi a cínu
- kde a jak často je cín
- o kolik která zbroj zpomaluje
- jak velké jsou žíly

Nápad s mapou nebo s nástrojem Chunky není vybrané řešení.

### Obsazení vesnice

První vesnice jsou chýše s ohništěm uprostřed. Středověk dostane hráč, který vesnici obsadí nebo založí. Daň je 10 % každých 7 dní. Než se to programuje, František doplní, co přesně znamená vesnici obsadit.

## Čeká na odpověď

Agent se zeptá a pak teprve píše kód.

- Padák: jak vypadá a jak padá
- Autobus: přesné bloky za sekundu. Dojem je střední rychlost, číslo chybí
- Mapa: speciální blok, nebo kamkoliv. Jak se značí prozkoumané okolí
- Armády, stráže, karavany: v kolika blocích se z dat stanou postavy
- Šablony budov a kdo je přidává
- Jak je chráněná pokladnice
- Co vezou karavany
- Jak se vycvičuje armáda
