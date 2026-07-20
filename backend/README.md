# backend

**Scopo**: servizio del confine L6 di sincronizzazione esterno, "content-blind per costruzione" — riceve/serve blob cifrati e metadati di versione, mai contenuti in chiaro (Technical Architecture Bible §01 §6).

**Riferimento**: Technology Decision Record TDR-03 (Go, servizi stateless containerizzati), TDR-14 (CI/CD portabile).

**Perché così minimale**: l'articolazione interna di L6 (quali servizi, quale storage, quale instradamento) è **esplicitamente rinviata** dalla Technical Architecture Bible stessa (§01 §6: "fuori perimetro, decisione rinviata"). Questo bootstrap non inventa quella struttura — crea solo l'ossatura Go minima (modulo, entry point, un pacchetto placeholder che marca il confine `relay`) su cui quella decisione futura potrà innestarsi, senza sincronizzazione, autenticazione, database o API reali.

**Struttura**:
- `cmd/server/main.go` — entry point del binario, nessuna rotta esposta.
- `internal/relay/` — pacchetto placeholder che marca il confine content-blind; nessuna logica.

**Stato**: infrastruttura di bootstrap — nessuna funzionalità di business.

**Verifica in questo ambiente**: `go build ./...` e `go vet ./...` eseguiti con successo in questo sandbox (Go 1.24, toolchain standard, nessuna dipendenza esterna). Vedi [../README-BUILD.md](../README-BUILD.md).
