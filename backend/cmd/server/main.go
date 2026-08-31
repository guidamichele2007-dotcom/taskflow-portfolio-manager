// Command server is the entry point of the L6 content-blind sync boundary
// (TDR-03: Go, stateless containerized service). Bootstrap scope only
// (Engineering Plan, EPIC-00): no routes, no auth, no sync, no database —
// see README-BUILD.md and backend/README.md.
package main

import (
	"fmt"

	_ "github.com/omnilife/backend/internal/relay"
)

func main() {
	fmt.Println("omnilife backend — bootstrap placeholder, no service implemented yet")
}
