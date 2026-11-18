# Úkol 6 - RESTful API Server

[![UTB FAI Task](https://img.shields.io/badge/UTB_FAI-Task-yellow)](https://www.fai.utb.cz/)
[![Java](https://img.shields.io/badge/Java-007396.svg?logo=java&logoColor=white)](https://www.java.com/)
[![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?logo=gradle&logoColor=white)](https://gradle.org/)

|                                  |                                |
| -------------------------------- | ------------------------------ |
| __Maximální počet bodů__         | 9 bodů                         |
| __Způsob komunikace s aplikací__ | Parametry předané při spuštění |
| __Způsob testování aplikace__    | Na základě HTTP komunikace     |

---

## 📝 Zadání úkolu 

Naprogramujte RESTful API Server pomocí frameworku Spring Boot, který bude poskytovat služby pro správu seznamu uživatelů. Tato aplikace bude spuštěna na portu **8080** a bude využívat http připojení. Vaše aplikace bude podporovat následující operace:

1. __Získání seznamu uživatelů:__ API endpoint pro získání seznamu všech uživatelů v systému. 
2. __Získání jednoho uživatele:__ API endpoint pro získání jednoho konkrétního uživatele podle jeho id. Pokud není uživatel nalezen, tak navrátí HTTP status kód 404 Not Found.
3. __Vytvoření nového uživatele:__ API endpoint pro vytvoření nového uživatele v systému. Endpoint bude validovat zadané data (email a telefoní číslo). Při úspěšném vytvoření navratí HTTP status kód 201 spolu s nově vytvořeným uživatelem ve formatu JSON (třida uživatele). Pokud tyto data nebudou splňovat požadovaný formát, nebude uživatel v systému vytvořen a server navrátí HTTP status kód 400 Bad Request. 
4. __Aktualizace existujícího uživatele:__ API endpoint pro aktualizaci informací o existujícím uživateli v systému. Pokud takový uživatel není nalezen, tak navrátí HTTP status kód 404 Not Found. Pokud dojde k jiné chybě, navrátí HTTP status kód 400.
5. __Smazání uživatele:__ API endpoint pro smazání existujícího uživatele ze systému. Pokud dojde k nějaké chybě, navrátí HTTP status kód 400.
6. __Smazání všech uživatelů:__ API endpoint, který odstraní všechny uživatele z databáze. Pokud dojde k nějaké chybě, navrátí HTTP status kód 400.
7. __Vyhledávání uživatelů:__ API endpoint pro získání všech uživatelů, který umožňuje volitelně filtrovat výsledky podle jména, emailu nebo telefonního čísla.
8. __Ukládání uživatelů:__ Pro ukládání uživatelských dat musíte použít databázový systém. Data nesmí být ukládána pouze do paměti aplikace.
9. __Validace údajů uživatele:__ Před vytvořením i aktualizací musí aplikace ověřit, že jméno, email a telefonní číslo jsou platné. Při neplatných údajích vrátí HTTP status kód 400 Bad Request a data neuloží (Jméno nesmí být prázdné, email musí odpovídat standardnímu formátu `example@domain.com` a telefonní číslo musí být ve formátu `+420123456789`).

| Typ požadavku | Název endpointu                      | URL adresa endpointu | Předávané parametry                          | Formát odpovědi |
| ------------- | ------------------------------------ | -------------------- | -------------------------------------------- | --------------- |
| GET           | Získání seznamu uživatelů            | /users               | N/A                                          | JSON            |
| GET           | Vyhledání uživatelů                   | /users/search        | Volitelné query parametry name, email, phoneNumber | JSON            |
| GET           | Získání informací o jednom uživateli | /users/{id}          | ID uživatele v cestě                         | JSON            |
| POST          | Vytvoření nového uživatele           | /users               | Data nového uživatele                        | JSON            |
| PUT           | Aktualizace existujícího uživatele   | /users/{id}          | ID uživatele v cestě, data uživatele v těle  | JSON            |
| DELETE        | Smazání uživatele                    | /users/{id}          | ID uživatele v cestě                         | JSON            |
| DELETE        | Smazání všech uživatelů              | /users               | N/A                                          | JSON            |

Endpoint `/users/search` vrací všechny uživatele, pokud nejsou zadány žádné parametry. Parametry `name`, `email` a `phoneNumber` lze použít samostatně nebo společně; při kombinaci se výsledky filtrují podle všech zadaných hodnot (shoda části textu bez rozlišení velikosti písmen, u telefonu s rozlišením).

### Struktura odpovědí

JSON objekt uživatele obsahuje následující údaje:

```json
{
  "id": 1,
  "name": "Jan Novák",
  "email": "jan@example.com",
  "phoneNumber": "+420123456789"
}
```

Endpointy `GET /users` a `GET /users/search` vracejí pole těchto objektů (při prázdném výsledku `[]`). Endpointy `GET /users/{id}`, `POST /users` a `PUT /users/{id}` vracejí jeden objekt uživatele. Úspěšné vytvoření vrací stav `201 Created`; hodnotu `id` přiděluje server. `DELETE /users/{id}` vrací objekt smazaného uživatele a `DELETE /users` vrací prázdné pole `[]`.

>_**Poznámka:** Funkcionalitu vaší aplikace můžete manuálně otestovat pomocí nástroje cURL. https://curl.se/_ Pro získání všech uživatelů můžete použít tento příkaz: `curl -X GET http://localhost:8080/users -H 'Content-Type: application/json'`

### Datová struktura uživatele

Do této třídy budou v aplikaci ukládány data o uživateli. Bude potřeba dokončit její implementaci a i vhodně zvolit anotace, aby ji bylo možné ukládat do databáze prostřednictvím JPA repozitáře.

```java
package utb.fai.RESTAPIServer;

class MyUser {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;

    public User() {}

    public User(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    
    public boolean isUserDataValid() {
        // Add your validation logic here (e.g., email and phone number format
        // validation)
        return true;
    }
    
    // TODO: Getters and setters
}
```

Pro ukládání uživatelských dat použijte libovolný volně dostupný databázový systém (např. MySQL, PostgreSQL, H2, MongoDB atd.), který bude vaše aplikace využívat. Můžete využít například následující službu, která nabízí free hosting databází: https://aiven.io/

Přístup k vaší databázi bude třeba nakonfigurovat v souboru `application.properties`, který se nachází ve složce `src/main/resources`.

>_**Poznámka:** Implementace a struktura kódu aplikace je libovolná a je zcela na vás, jak tento problém vyřešíte. Je však důležité, aby aplikace splňovala zadané požadavky._

---

## 🏆 Způsob hodnocení

Vaše implementace bude hodnocena na základě chování aplikace při testování různých scénářů. Automatizovaný testovací nástroj bude předávat vaší aplikaci různé parametry, včetně platných a neplatných, aby otestoval její chování za různých podmínek. V případě testování síťové komunikace mezi více klienty, testovací nástroj bude vytvářet virtuální klienty/servery za účelem ověření funkcionality.

Výsledné hodnocení bude záviset na celkovém počtu úspěšných testovacích případů. Počet bodů získaných z úlohy bude tedy záviset na celkové úspěšnosti při testování. Váš výsledný počet bodů bude určen následujícím vzorcem.

__VP__ = __MB__ * (__UT__ / __CPT__)

### Popis symbolů:

* __VP:__ Výsledný počet bodů.
* __MB:__ Maximální počet bodů pro danou úlohu.
* __UT:__ Počet úspěšných testovacích případů.
* __CPT:__ Celkový počet testovacích případů.

## ⚙️ Jak spustit automatizované hodnocení lokálně na svém počítači?

Automatizované hodnocení můžete spustit lokálně za účelem ověření funkčnosti vaší aplikace. K tomu slouží předpřipravený skript, který je dostupný v repozitáři tohoto úkolu. Výsledný report testování se bude nacházet v souboru ```test_report.html```.

###  Pro uživatele systému Linux:
Spusťte skript s názvem ```run_local_test.sh```.

### Pro uživatele systému Windows:
Spusťte skript s názvem ```run_local_test.bat```.

