package main

import (
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"
)

func TestHealthz(t *testing.T) {
	rec := httptest.NewRecorder()
	newMux().ServeHTTP(rec, httptest.NewRequest(http.MethodGet, "/healthz", nil))

	if rec.Code != http.StatusOK {
		t.Fatalf("status = %d, want 200", rec.Code)
	}
	if rec.Body.String() != "ok" {
		t.Fatalf("body = %q, want ok", rec.Body.String())
	}
}

func TestVersion(t *testing.T) {
	version = "1.4.0"
	rec := httptest.NewRecorder()
	newMux().ServeHTTP(rec, httptest.NewRequest(http.MethodGet, "/version", nil))

	var body map[string]string
	if err := json.NewDecoder(rec.Body).Decode(&body); err != nil {
		t.Fatal(err)
	}
	if body["version"] != "1.4.0" {
		t.Fatalf("version = %q, want 1.4.0", body["version"])
	}
}

func TestUnknownRouteIs404(t *testing.T) {
	rec := httptest.NewRecorder()
	newMux().ServeHTTP(rec, httptest.NewRequest(http.MethodGet, "/nope", nil))
	if rec.Code != http.StatusNotFound {
		t.Fatalf("status = %d, want 404", rec.Code)
	}
}

func TestNewServer(t *testing.T) {
	srv := newServer("9090")
	if srv.Addr != ":9090" {
		t.Fatalf("addr = %q, want :9090", srv.Addr)
	}
	if srv.ReadHeaderTimeout == 0 {
		t.Fatal("ReadHeaderTimeout must be set (slowloris)")
	}
}

func TestEnvOr(t *testing.T) {
	t.Setenv("EDGE_ROUTER_TEST", "set")
	if got := envOr("EDGE_ROUTER_TEST", "fallback"); got != "set" {
		t.Fatalf("envOr = %q, want set", got)
	}
	if got := envOr("EDGE_ROUTER_UNSET", "fallback"); got != "fallback" {
		t.Fatalf("envOr = %q, want fallback", got)
	}
}
