# scripts/git-hooks

Hook condivisi e versionati (non in `.git/hooks/`, che non è tracciato da git). Attivazione locale una tantum:

```sh
git config core.hooksPath scripts/git-hooks
```

**`pre-commit`**: verifica la formattazione dei soli file Kotlin (`ktlintCheck`) e Go (`gofmt -l`) modificati nello staging. Nessuna build completa, nessun accesso di rete — deve restare abbastanza veloce da girare a ogni commit. Vedi [`../../README-BUILD.md`](../../README-BUILD.md) §8.
