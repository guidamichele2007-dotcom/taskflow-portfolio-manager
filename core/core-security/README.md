# core:core-security

**Scopo**: Servizio di Sicurezza — gerarchia di cifratura, sblocco locale, chiave di recupero (Sprint 3, Core Platform).

**Riferimento**: Functional Bible SEC-001…003, MFC §5; Technical Architecture Bible §10; 06-sicurezza-e-privacy.md; TDR-04, TDR-23.

## Componenti

| Componente | File | Ruolo |
|---|---|---|
| `CryptoService` | `CryptoService.kt` | Primitive generiche (AES-256-GCM, PBKDF2WithHmacSHA256, TDR-23) — mai la gerarchia di chiavi |
| `KeyManager` | `KeyManager.kt` | Gerarchia MK→KEK→DEK per dominio, envelope encryption, rotazione — **mai espone una chiave come valore**, solo operazioni (`encryptForDomain`/`decryptForDomain`) |
| `SecureStorage` | `SecureStorage.kt` | Persistenza delle sole chiavi *wrapped*; `InMemorySecureStorage` (JVM/test) + `platformSecureStorage()` (Android `EncryptedSharedPreferences`, iOS Keychain — scritti, non verificati in questo sandbox) |
| `AppLockService` | `AppLockService.kt` | Stato di blocco/sblocco puro (MFC-R-21/22, SEC-AC-01) — mai tocca la cifratura a riposo, solo la visibilità a schermo |
| `RecoveryKeyService` | `RecoveryKeyService.kt` | Frase di recupero a 24 parole (SEC-002), verifica "scrivi 3 parole a campione" |
| `FieldCipher` | `FieldCipher.kt` | Cifratura applicativa a livello di campo — l'"encrypted database" di questo sprint (non SQLCipher a livello di pagina, vedi TDR-23) |
| `SecurityService` | `SecurityService.kt` | Facciata che compone i precedenti per un futuro entry point app |

## Cosa NON fa questo modulo

- Non implementa l'autenticazione remota (TDR-04's token access/refresh) — fuori perimetro esplicito.
- Non collega `FieldCipher` a nessun modulo `domain-*` esistente (`domain-task`) — richiederebbe modificare un modulo fuori dal perimetro di questo sprint ("implementa esclusivamente i 4 sottosistemi Core Platform").
- Non implementa la cifratura SQLite a livello di pagina (SQLCipher, TDR-06) — richiede una libreria nativa non linkabile/verificabile in questo sandbox.

## Cosa è verificato in questo sandbox

Solo il target **JVM** (nessun SDK Android, nessun host macOS/Xcode — stesso vincolo di ogni sprint precedente). Gli `actual` Android/iOS sono scritti per intero ma non compilati qui; `crypto/PlatformCrypto.kt`'s iOS actual dichiara esplicitamente incompleta l'implementazione AES-GCM/PBKDF2 (nessun binding CryptoKit verificabile) — vedi TDR-23.

Vedi [../../README-BUILD.md](../../README-BUILD.md) per le convenzioni comuni a ogni modulo.
