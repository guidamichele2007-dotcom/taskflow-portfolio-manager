# 08 · Entità Salute (HLTH)

> Eredita il [MDC](00-modello-dati-comune.md). Modulo v1.x. Contiene la deroga di sincronizzazione più severa del sistema: **nessun dato grezzo lascia mai il dispositivo** (HLTH-R-02).

## DM-HLTH-01 · HealthPlatformReading (Lettura dalla piattaforma) — **entità in deroga totale**

**Descrizione**: **non un'entità nostra**: una cache locale minima e temporanea di ciò che la piattaforma Salute di sistema (HealthKit/Health Connect) espone, per i soli tipi per cui esiste una funzione attiva (HLTH-001, C-art. 4 — minimizzazione).

| Campo (cache locale, finestra 90 giorni) | Note |
|---|---|
| `tipo_dato` | Enum: passi · allenamenti · sonno · battito_a_riposo · peso |
| `valore`, `timestamp_evento` | Con il proprio timestamp di origine (non il nostro `creato_il`) |
| `consenso_attivo` | Booleano, revocabile per singolo tipo |

**Deroga dichiarata (MDC §9)**:
- **Mai sincronizzata sul cloud** — resta nella cache locale cifrata del dispositivo, punto (HLTH-R-02, C-art. 43/45).
- Nessun ciclo di vita MFC: sola lettura, gestita dall'app di sistema, non da noi.
- Nessun export nostro dei dati grezzi (l'export cita solo l'*effetto*, es. un'abitudine completata — EXP-001 esclusioni).
- Nessuna cronologia nostra: la piattaforma OS possiede la propria.

**Regole**: dati con soglie di implausibilità (es. 10 milioni di passi) non vengono mai usati per auto-completare un'abitudine (HLTH-001 scheda estesa, casi limite); doppio conteggio da più fonti (telefono+watch) è deduplicato dalla piattaforma OS, non da noi (dichiarato).

**Stati**: consenso concesso (per tipo) · consenso negato/revocato (HLTH-AC-03) · piattaforma assente sul device.

**Eventi collegati**: pubblica `hlth.workout.detected`, `hlth.steps.threshold` (consumati da Abitudini per l'auto-completamento).

**Riferimenti Functional Bible**: HLTH-001/002/003, HLTH-R-01…04.

---

## DM-HLTH-02 · ManualHealthMetric (Metrica manuale)

**Descrizione**: a differenza della lettura di piattaforma, questa **è** un'entità utente piena (HLTH-R-04): peso, umore, energia inseriti manualmente (HLTH-004).

| Campo | Tipo concettuale | Obbligatorio |
|---|---|---|
| `tipo` | Enum: peso · umore · energia | Sì |
| `valore` | Numero (umore/energia: scala 1-5; peso: numero con unità) | Sì |
| `data` | Data | Sì (default: oggi) |

**Relazioni**: nessuna relazione strutturale obbligatoria; può alimentare Insight trasversali (consumo in sola lettura, non un GraphLink formale).

**Regole**: ciclo MFC completo, incluso l'export (HLTH-R-04) — a differenza di DM-HLTH-01, questa entità **sincronizza normalmente** come ogni altra entità utente.

**Stati**: eredita MDC §6 senza deroghe.

**Eventi collegati**: pubblica `hlth.manual.logged` (consumato da Insight).

**Riferimenti Functional Bible**: HLTH-004, HLTH-R-04.

---

*Prossimo: [Entità Obiettivi](09-entita-obiettivi.md)*
