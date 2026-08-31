# 11 · Onboarding Experience

> Eredita [MUC](00-modello-ux-comune.md) e [FLOW-ONB-01](04-user-flows-core-mvp.md#flow-onb-01--onboarding-onb-001007). Copre l'intero arco temporale: prima apertura → prima settimana → primo mese, non solo le prime schermate.

## 1. Prima apertura (0–60 secondi)

Sequenza già dettagliata in FLOW-ONB-01: benvenuto (1 frase) → scelta di 1-2 moduli → prima cattura reale. **Nessun tutorial a schermate multiple, nessun tour guidato** (P115 — se una schermata richiede un tutorial, la schermata è sbagliata). L'apprendimento avviene facendo (ONB-003: la prima cattura è vera, non simulata).

## 2. Registrazione (posticipata, mai al primo avvio)

- **Quando appare**: solo quando c'è "qualcosa da proteggere" — al primo dato con valore percepito (es. dopo la 3ª entità creata, o dopo 24h di uso) tramite un banner contestuale non intrusivo: "Vuoi mettere al sicuro i tuoi dati?" (ONB-004, D-05).
- **Flusso**: email + password (o provider di sistema se disponibile) → generazione automatica delle chiavi → migrazione trasparente dei dati locali (nessuna azione richiesta all'utente, ONB-007).
- **Se rifiutata**: l'app continua a funzionare pienamente in locale, con un promemoria onesto e raro (non più di 1 volta ogni 2 settimane) del rischio di perdita in caso di perdita del dispositivo (mai un ricatto).

## 3. Login (dispositivo aggiuntivo)

- Email + password → eventuale 2FA → verifica chiave di recupero o sblocco tramite un dispositivo già fidato (più rapido, consigliato quando disponibile) → ripristino (FLOW-SYNC-01).
- **Microcopy di chiusura**: "Bentornato. Stiamo recuperando i tuoi dati." con barra di progresso onesta.

## 4. Permessi (tutti contestuali, mai in batch al primo avvio)

| Permesso | Momento di richiesta | Schermata "pre-permesso" nostra |
|---|---|---|
| Notifiche | Prima entità con orario/promemoria | "Ti avviso quando conta? Puoi cambiare idea quando vuoi." |
| Calendario | Attivazione modulo Calendario | "Per mostrarti tutto insieme, ho bisogno di leggere il tuo calendario. Non lo modifico senza chiedertelo." |
| Salute | Attivazione modulo Salute, per singolo tipo di dato | "Quali dati vuoi che legga? Scegli tu, uno per uno." |
| Microfono | Primo tentativo di cattura vocale | "Per dettare, mi serve il microfono solo mentre parli." |
| Posizione | Solo se l'utente crea un promemoria basato su luogo (v2.x) | "Per ricordartelo quando arrivi, serve la posizione." |

**Regola**: ogni schermata "pre-permesso" nostra precede sempre il dialogo di sistema, spiega il beneficio in una frase, e il rifiuto non blocca mai il resto dell'app (C-art. 32-33).

## 5. Tutorial (assente come artefatto separato)

Non esiste una sezione "tutorial": l'insegnamento è distribuito nei **suggerimenti contestuali** (max 1 per sessione, GAL-004/P36) e negli **stati vuoti didattici con dati d'esempio interattivi** (§9 empty states, punto 5). La scoperta è progressiva e volontaria (P85).

## 6. Prima nota / primo task / prima spesa / prima abitudine (il primo successo per modulo)

Ogni modulo, alla sua prima attivazione, ha un micro-onboarding di **massimo 3 schermate** (GAL-002) che culmina sempre nella creazione di un elemento reale, mai fittizio:
- **Attività**: "Scrivi la prima cosa da fare" → task creato → appare subito nella Vista Oggi.
- **Finanze**: "Qual è il tuo primo conto?" (default "Contanti" pre-compilato, skippabile) → "Registra la tua prima spesa" → transazione creata.
- **Abitudini**: "Cosa vuoi iniziare a fare con costanza?" con 3 esempi comuni cliccabili (Bere acqua / Camminare / Leggere) + libero → frequenza *prudente* preselezionata (3×/settimana, non 7) → prima spunta possibile subito.
- **Note**: nessun micro-onboarding necessario (l'editor si apre e basta, P26).
- **Calendario/Salute**: micro-onboarding = concessione permesso + selezione fonte (quali calendari/quali tipi di dato).
- **Obiettivi**: "Crea il tuo primo obiettivo" con esempio (viaggio, risparmio, abitudine) → invito a collegare un contributo esistente.

Il **primo successo** (prima cattura completata, prima spunta, primo obiettivo con un fronte collegato) riceve la microinterazione di completamento standard (§7 microinterazioni) — **mai** una celebrazione sproporzionata rispetto ai successi successivi (coerenza, non gerarchia artificiale di importanza).

## 7. Prima sincronizzazione

- Avviene silenziosamente non appena l'utente si registra (§2): nessuna schermata dedicata, solo l'indicatore discreto nel pannello di stato.
- Se l'utente non aggiunge mai un secondo dispositivo, la sincronizzazione resta comunque attiva (per il backup) senza mai reclamizzare "aggiungi un altro dispositivo" in modo invasivo — un solo suggerimento discreto in Impostazioni.

## 8. Prima settimana

- **Giorno 1–3**: nessuna sollecitazione oltre i promemoria che l'utente ha impostato lui stesso.
- **Giorno 3-5**: se l'uso mostra segnali coerenti con un secondo modulo utile (es. testo di cattura ripetutamente ambiguo verso Finanze), **una singola** proposta contestuale (GAL-004), mai ripetuta se ignorata.
- **Fine settimana 1**: se l'utente ha usato l'app almeno 3 giorni, viene proposto (opt-in, mai imposto) il rituale della Revisione settimanale (REV-004) con spiegazione di cosa fa.
- Nessuna notifica di "ci manchi" se l'utente non ha aperto l'app (vietato, C-art. 60).

## 9. Primo mese

- **Digest settimanale** (INS-002, se attivo) inizia a mostrare valore composto reale dopo 2-3 settimane di dati.
- **Primo insight trasversale** (INS-001): appare solo quando i dati lo giustificano statisticamente (INS-R-04) — mai forzato per "dare l'impressione" di intelligenza prematuramente.
- **Prompt di recensione** (se applicabile): solo dopo un momento di valore compiuto (es. 4 settimane di costanza su un'abitudine, o un obiettivo con progresso reale) — mai a freddo nei primi giorni (C-art. 65).
- **Verifica della chiave di recupero**: primo promemoria gentile di ri-verifica a 30 giorni dal setup (SEC-002), se non ancora confermata.

## 10. Anti-pattern esclusi esplicitamente dall'onboarding

- ❌ Richiesta di valutazione/recensione nei primi 7 giorni.
- ❌ Paywall a schermo intero prima di aver dimostrato valore.
- ❌ Raccolta di dati di profilo non essenziali ("quanti anni hai? qual è il tuo obiettivo nella vita?") — la personalizzazione avviene per uso reale, non per questionario.
- ❌ Tour con più di 1 schermata esplicativa non azionabile.
- ❌ Notifiche push durante l'onboarding stesso.

---

*Prossimo: [Accessibility Bible](12-accessibility-bible.md)*
