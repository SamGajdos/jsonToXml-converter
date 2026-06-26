# JSON → XML Konvertor

## Popis projektu

Táto aplikácia slúži na spracovanie JSON súborov zo zadaného vstupného adresára a ich konverziu do XML formátu. Počas spracovania sú vstupné dáta validované a výsledné XML súbory sú uložené do výstupného adresára.

Aplikácia je implementovaná v jazyku Java a je zostavená pomocou nástroja Maven.

## Požiadavky

* Java 17 alebo novšia
* Apache Maven 3.9 alebo novší

Overenie inštalácie:

```bash
java --version
mvn --version
```

## Zostavenie projektu

V koreňovom adresári projektu spustite:

```bash
mvn clean package
```

Po úspešnom zostavení sa vytvorí spustiteľný JAR súbor:

```text
target/jsonToXml-converter-1.0-jar-with-dependencies.jar
```


## Spustenie aplikácie

Aplikácia sa spúšťa z príkazového riadka:

```bash
java -jar target/jsonToXml-converter-1.0-jar-with-dependencies.jar\
    --vstup /cesta/k/vstupnemu/adresaru \
    --vystup /cesta/k/vystupnemu/adresaru \
    --platnostOd 2026-01-01 \
    --platnostDo 2026-12-31
```

### Parametre

| Parameter        | Popis                                      |
| ---------------- | ------------------------------------------ |
| `--vstup`, `-i`  | Cesta k adresáru obsahujúcemu JSON súbory  |
| `--vystup`, `-o` | Cesta k výstupnému adresáru                |
| `--platnostOd`   | Dátum platnosti od vo formáte `YYYY-MM-DD` |
| `--platnostDo`   | Dátum platnosti do vo formáte `YYYY-MM-DD` |
| `--help`         | Zobrazenie nápovedy                        |

### Príklad

```bash
java -jar target/jsonToXml-converter-1.0.jar \
    -i ./input \
    -o ./output \
    --platnostOd 2026-01-01 \
    --platnostDo 2026-12-31
```

## Použité technológie

* Java
* Maven
* Jackson
* JCommander
* Hibernate Validator
* SLF4J
* JAXB - Generovanie tried zo schémy .xsd a serializácia (marshal) do .xml súborov.

## Autor

Samuel Gajdos (s pomocou umelej inteligencie)

