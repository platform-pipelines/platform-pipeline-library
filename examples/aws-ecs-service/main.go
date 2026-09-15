// Command payments-api is a minimal service deployed to ECS Fargate by
// platform-pipeline. The ALB target group health-checks GET /healthz.
package main

import (
	"encoding/json"
	"errors"
	"log"
	"net/http"
	"os"
	"strconv"
	"time"
)

var version = "dev"

// feeBasisPoints is the card processing fee: 2.9% as basis points.
const feeBasisPoints = 290

// fee returns the processing fee in cents for an amount in cents, rounded half up.
func fee(amountCents int64) (int64, error) {
	if amountCents <= 0 {
		return 0, errors.New("amount must be positive")
	}
	return (amountCents*feeBasisPoints + 5000) / 10000, nil
}

func newMux() *http.ServeMux {
	mux := http.NewServeMux()
	mux.HandleFunc("GET /healthz", func(w http.ResponseWriter, _ *http.Request) {
		_, _ = w.Write([]byte("ok"))
	})
	mux.HandleFunc("GET /version", func(w http.ResponseWriter, _ *http.Request) {
		writeJSON(w, http.StatusOK, map[string]string{"version": version})
	})
	mux.HandleFunc("GET /fee", func(w http.ResponseWriter, r *http.Request) {
		amount, err := strconv.ParseInt(r.URL.Query().Get("amount"), 10, 64)
		if err != nil {
			writeJSON(w, http.StatusBadRequest, map[string]string{"error": "amount must be an integer number of cents"})
			return
		}
		f, err := fee(amount)
		if err != nil {
			writeJSON(w, http.StatusBadRequest, map[string]string{"error": err.Error()})
			return
		}
		writeJSON(w, http.StatusOK, map[string]int64{"amount": amount, "fee": f})
	})
	return mux
}

func writeJSON(w http.ResponseWriter, status int, body any) {
	w.Header().Set("Content-Type", "application/json")
	w.WriteHeader(status)
	_ = json.NewEncoder(w).Encode(body)
}

func newServer(port string) *http.Server {
	return &http.Server{
		Addr:              ":" + port,
		Handler:           newMux(),
		ReadHeaderTimeout: 5 * time.Second,
	}
}

func main() {
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	srv := newServer(port)
	log.Printf("payments-api %s listening on %s", version, srv.Addr)
	if err := srv.ListenAndServe(); err != nil && !errors.Is(err, http.ErrServerClosed) {
		log.Fatal(err)
	}
}
