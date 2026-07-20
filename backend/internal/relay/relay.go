// Package relay marks the L6 "content-blind" external sync boundary
// (Technical Architecture Bible §01 §6): receives/serves encrypted blobs and
// version metadata only, never plaintext content.
//
// Its internal articulation (services, storage, routing) is explicitly out
// of scope for this bootstrap — Technical Architecture Bible §01 §6 defers
// that design ("fuori perimetro, decisione rinviata"). This package is a
// placeholder marking where that design will land; it has no business logic.
package relay

// Placeholder marks the package as intentionally non-empty during bootstrap.
// Removed once the first real relay type is added.
type Placeholder struct{}
