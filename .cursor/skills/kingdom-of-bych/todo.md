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
- [x] Minihra s pecí v době kamenné: citlivost myši a klik vedle; ohniště neblokované

## Připraveno, smí se začít

Nic, dokud František neřekne minuty a odměnu u odehraného času (viz Epochy v `decisions.md`).

Pořadí po odpovědi:

1. Nové hodiny (odehraný čas + achievementy)
2. Loading screen + hezčí oznámení

## Návrhy, nezačínat v kódu

### Oznámení doby

Vybráno **B** (odehraný čas + achievementy). Zapsáno v `decisions.md`. Minuty a odměna ještě **čeká**.

### Bronz jako věc ve hře

Většina je v `decisions.md` (poměr 4+1, rudy, věci z bronzu, tavící pec). Než se to programuje, František ještě doplní:

- jak přesně vypadá a funguje tavící pec
- přesná čísla zpomalení zbroje
- přesné vlastnosti malého lehkého štítu

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
