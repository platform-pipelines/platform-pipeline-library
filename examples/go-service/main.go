// Command edge-router is a minimal HTTP service used to exercise the
// platform-pipeline Go path end to end: gofmt, go vet, golangci-lint,
// race-enabled tests with coverage, a static binary, and a distroless image.
package main

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"os"
	"time"
)

// version is stamped at build time by goPackage:
// -ldflags "-X main.version=<APP_VERSION>".
var version = "dev"

func newMux() *http.ServeMux {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /healthz", func(w http.ResponseWriter, _ *http.Request) {
		w.WriteHeader(http.StatusOK)
		_, _ = w.Write([]byte("ok"))
	})
	mux.HandleFunc("GET /version", func(w http.ResponseWriter, _ *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		_ = json.NewEncoder(w).Encode(map[string]string{"version": version})
	})
	return mux
}

func newServer(port string) *http.Server {
	return &http.Server{
		Addr:              ":" + port,
		Handler:           newMux(),
		ReadHeaderTimeout: 5 * time.Second,
	}
}

func main() {
	srv := newServer(envOr("PORT", "8080"))

	log.Printf("edge-router %s listening on %s", version, srv.Addr)
	if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
		log.Fatal(err)
	}
}

func envOr(key, fallback string) string {
	if v := os.Getenv(key); v != "" {
		return v
	}
	return fallback
}
