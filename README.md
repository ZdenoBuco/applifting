# Applifitng app

<p>Appka na monitorovanie požadovaných URL adres.</p>

## Postup spustenia:

<ol>
  <li>Nastaviť v docker-compose.yml premenné: MYSQL_ROOT_PASSWORD a MYSQL_DATABASE_NAME + volumes (path na host zariadení, kde sa uložia DB data).</li>
  <li>Nastav environment variables použité v application.properties - MYSQL_ROOT_PASSWORD a MYSQL_DATABASE_NAME s rovnakou hodnotou ako v docker-compose.yml, a MYSQL_DATABASE_USERNAME=root (default hodnota).</li>
  <li>docker-compose up -d (rozbehne MySQL kontajner)</li>
  <li>build + run</li>
</ol>

## Funkcionality appky:

### Základný popis:

<p>Appka umožňuje užívateľovi vytvorenie účtu, ktoré generuje token (accessToken), ktorý je potrebný na prístup k ďaľším funkcionalitám. Je potrebné, aby registrovaný užívateľ vložil daný token do headru requestu. Ten je využitý na autentifikáciu a autorizáciu requestov.
Pri odoslaní requestu sa kontroluje priložený token a ten obmädzuje prístum k zdrojom iba daného užívateľa. Pokiaľ sa v body requestu nachádza aj payload, pred jeho ďaľším spravocaním dochádza k jeho validácii. Prihlásený užívateľ môže vytvoriť, upraviť, vymazať
a zobraziť konkrétny endpoint alebo všetchy jeho endpointy na monitorovanie. Každý endpoint obsahuje zoznam výsledkov jeho monitorovania, ktorý je automaticky generovaný v zadanom intervale. Výsledky monitorovania sú zoradené zostupne podľa času vytvorenia výsledku 
a ich počet v zobrazení je možné obmädziť na požadované množstvo záznamov pomocou RequestParam. Každý výsledok monitorovania je tiež logovaný do konzoly. Podrobnejší popis funkcionalít:</p>

### Registrácia uživateľa:

<p>POST request na: localhost:8080/api/v1/auth/register Premenné username a email musia mať maximálne 255 znakov a ich hodnoty musia byt unikátne. Email musí splňovať platný format emailovej adresy. Nastavenie obmädzení vsupov je možné upraviť v AppUserRegistrationInDTOValidator.java. Príklad JSON body:<br>
{<br>
    "username": "exampleUser",<br>
    "email": "exampleUser@example.com"<br>
}<br><br>
Príklad odpovedi úspešnej registrácie:<br>
{<br>
    "accessToken": "e51eaa20-5ac4-46c3-8fb1-6369f614b855"<br>
}<br><br>
Vytvorený token vložiť do headru ďaľších requestov.</p>

### Vytvorenie endpointu:

<p>POST request na: localhost:8080/api/v1/monitoredEndpoint Premenné name a url musia mať maximálne 255 znakov. Premenná monitoringInterval je v sekundách a musí mať hodnotu 5 - 2147483647. Nastavenie obmädzení vsupov je možné upraviť v MonitoredEndpointInDTOValidator.java. Príklad JSON body:<br>
{<br>
    "name": "example endpoint 2",<br>
    "url": "https://example.com",<br>
    "monitoringInterval": 60<br>
}<br><br>
Príklad odpovedi úspešne vytvoreného endpointu:<br>
{<br>
    "id": "94edf403-8a72-4a5c-81da-6764bd539c3b",<br>
    "name": "example endpoint 2",<br>
    "url": "https://example.com",<br>
    "createdAt": "2024-05-26T18:45:08",<br>
    "lastCheck": null,<br>
    "monitoringInterval": 60,<br>
    "monitoringResults": null,<br>
    "ownerId": "c476ed8e-7ea2-4396-9adc-2b085a5999c5"<br>
}</p>

### Získanie všetých endpointov daného užívateľa:

<p>GET request na: localhost:8080/api/v1/monitoredEndpoint Daný request vratí zoznam všetkých endpointov so všetými výsledkami meraní zoradenými od najstaršieho. Pridaním parametru 'resultLimit' sa nastaví maximálny počet zobrazených výsledkov kontrol každého endpointu.<br>

Príklad odpovedi:<br>
{<br>
"id": "85f05949-9113-472a-81f5-5514928a4988",<br>
"name": "example endpoint 2",<br>
"url": "https://example.com",<br>
"createdAt": "2024-05-26T18:38:57",<br>
"lastCheck": "2024-05-26T18:58:57",<br>
"monitoringInterval": 60,<br>
"monitoringResults": [<br>
{<br>
"id": "223d0daa-61e4-403d-8aa7-1adea8af6cda",<br>
"checkedAt": "2024-05-26T18:58:57.730094",<br>
"httpCode": 200,<br>
`"payload": "<!doctype html>\n<html>\n<head>\n    <title>Example Domain</title>\n\n    <meta charset=\"utf-8\" />\n    <meta http-equiv=\"Content-type\" content=\"text/html; charset=utf-8\" />\n    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n    <style type=\"text/css\">\n    body {\n        background-color: #f0f0f2;\n        margin: 0;\n        padding: 0;\n        font-family: -apple-system, system-ui, BlinkMacSystemFont, \"Segoe UI\", \"Open Sans\", \"Helvetica Neue\", Helvetica, Arial, sans-serif;\n \n }\n    div {\n        width: 600px;\n        margin: 5em auto;\n        padding: 2em;\n        background-color: #fdfdff;\n        border-radius: 0.5em;\n        box-shadow: 2px 3px 7px 2px rgba(0,0,0,0.02);\n }\n    a:link, a:visited {\n        color: #38488f;\n        text-decoration: none;\n }\n @media (max-width: 700px) {\n        div {\n            margin: 0 auto;\n            width: auto;\n }\n }\n    </style>    \n</head>\n\n<body>\n<div>\n    <h1>Example Domain</h1>\n    <p>This domain is for use in illustrative examples in documents. You may use this\n    domain in literature without prior coordination or asking for permission.</p>\n    <p><a href=\"https://www.iana.org/domains/example\">More information...</a></p>\n</div>\n</body>\n</html>\n"`<br>}<br>
],<br>
"ownerId": "c476ed8e-7ea2-4396-9adc-2b085a5999c5"<br>
}<br></p>

### Získanie konkrétneho endpointu daného užívateľa:

<p>GET request na: localhost:8080/api/v1/monitoredEndpoint/{monitoredEndpointId} Daný request vratí endpoint s id rovným hodnote monitoredEndpointId so všetými výsledkami meraní zoradenými od najstaršieho. Pridaním parametru 'resultLimit' sa nastaví maximálny počet zobrazených výsledkov kontrol daného endpointu.<br>

Odpoveď ma rovnaký formát ako v predošlom bode.

### Zmena existujúceho endpointu:

<p>PUT request na: localhost:8080/api/v1/monitoredEndpoint/{monitoredEndpointId} Daný request upravý endpoint s id rovným hodnote monitoredEndpointId. V tele requestu sa posiela objekt obsahujúci parametry na zmenu - name a monitoringInterval [s] (zmena ostatných parametrov nie je možná). Príklad JSON body:<br>
{<br>
    "name": "example endpoint 20",<br>
    "monitoringInterval": 10<br>
}<br><br>
Príklad odpovedi úspešne zmeneného endpointu:<br>
{<br>
    "id": "85f05949-9113-472a-81f5-5514928a4988",<br>
    "name": "example endpoint 20",<br>
    "url": "https://example.com",<br>
    "createdAt": "2024-05-26T18:38:57",<br>
    "lastCheck": "2024-05-26T19:23:44",<br>
    "monitoringInterval": 10,<br>
    "monitoringResults": [],<br>
    "ownerId": "c476ed8e-7ea2-4396-9adc-2b085a5999c5"<br>
}</p>

### Zmazanie existujúceho endpointu:

<p>DELETE request na: localhost:8080/api/v1/monitoredEndpoint/{monitoredEndpointId} Daný request zma6e endpoint s id rovným hodnote monitoredEndpointId.<br>
Príklad odpovedi úspešne zmazaného endpointu:<br>
{<br>
"id": "85f05949-9113-472a-81f5-5514928a4988",<br>
"name": "example endpoint 20",<br>
"url": "https://example.com",<br>
"createdAt": "2024-05-26T18:38:57",<br>
"lastCheck": "2024-05-26T19:28:35",<br>
"monitoringInterval": 10,<br>
"monitoringResults": null,<br>
"ownerId": "c476ed8e-7ea2-4396-9adc-2b085a5999c5"<br>
}</p>