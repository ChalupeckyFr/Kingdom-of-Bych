---
name: kingdom-of-bych
description: >-
  Rules and confirmed design decisions for the Kingdom of Bych Minecraft 1.21.1
  Fabric mod. Use for every task in this project: Java code, config, epochs,
  sleep, armies, map, villages, bus, packages, and any gameplay detail. Requires
  reading decisions.md and asking František before inventing unspecified rules.
---

# Kingdom of Bych

## Nevyplněné rozhodnutí

Místo vymýšlení věcí samostatně se agent vždy zeptá na věci, které nebyly stanovené, pokud nebude vyzván k generování vlastního nápadu.

Před změnou hry si přečti [decisions.md](decisions.md), jen oddíly systému, na kterém děláš. Potvrzené věty platí. Řádek `NEVYPLNĚNO` se nesmí doplnit odhadem. Vlastní nápad smí agent navrhnout jen když o něj František výslovně požádá, a do kódu ho nedá, dokud nedostane souhlas.

## Technická preference není zákon

František technické volby nedokáže vždy posoudit. Když potvrzená technická věta v praxi nedává smysl, zastav se a zeptej se, jestli ji smíš nepoužít. Neber ji jako zákaz, který se nikdy nesmí porušit, a neporušuj ji potichu.

## Po odpovědi

Nové rozhodnutí dopiš do [decisions.md](decisions.md) jednou nebo dvěma větami. Celý chat tam nekopíruj.

## Jak psát

- S Františkem česky a jednoduše. Je začátečník.
- Názvy tříd, metod a proměnných anglicky. Základní balíček je `com.bych`.
- Podrobné komentáře ve zdrojovém kódu česky.
- Texty pro hráče (chat, okna) česky.
- Server rozhoduje o hře. Klient jen kreslí a posílá požadavky, které server ověří.
- Nemazat fungující kód bez souhlasu. Když překáží opravě, nejdřív se zeptej.
- Verze Minecraftu, Fabricu a Javy neměň z vlastní iniciativy. Když na nich nejde pokračovat, zeptej se.
