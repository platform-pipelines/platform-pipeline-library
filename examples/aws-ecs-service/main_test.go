package main

import (
	"net/http"
	"net/http/httptest"
	"strings"
	"testing"
)

func get(t *testing.T, path string) *httptest.ResponseRecorder {
	t.Helper()
	rec := httptest.NewRecorder()
	newMux().ServeHTTP(rec, httptest.NewRequest(http.MethodGet, path, nil))
	return rec
}

func TestFee(t *testing.T) {
	cases := map[int64]int64{100: 3, 1000: 29, 12345: 358, 1: 0}
	for amount, want := range cases {
		got, err := fee(amount)
		if err != nil || got != want {
			t.Errorf("fee(%d) = %d, %v; want %d", amount, got, err, want)
		}
	}
	if _, err := fee(0); err == nil {
		t.Error("fee(0) should fail")
	}
}

func TestHealthz(t *testing.T) {
	if rec := get(t, "/healthz"); rec.Code != http.StatusOK || rec.Body.String() != "ok" {
		t.Fatalf("healthz = %d %q", rec.Code, rec.Body.String())
	}
}

func TestVersion(t *testing.T) {
	if rec := get(t, "/version"); !strings.Contains(rec.Body.String(), `"version"`) {
		t.Fatalf("version body = %q", rec.Body.String())
	}
}

func TestFeeEndpoint(t *testing.T) {
	if rec := get(t, "/fee?amount=1000"); rec.Code != http.StatusOK || !strings.Contains(rec.Body.String(), `"fee":29`) {
		t.Fatalf("fee = %d %q", rec.Code, rec.Body.String())
	}
	if rec := get(t, "/fee?amount=abc"); rec.Code != http.StatusBadRequest {
		t.Fatalf("non-numeric amount = %d, want 400", rec.Code)
	}
	if rec := get(t, "/fee?amount=-5"); rec.Code != http.StatusBadRequest {
		t.Fatalf("negative amount = %d, want 400", rec.Code)
	}
}

func TestNewServer(t *testing.T) {
	if srv := newServer("9090"); srv.Addr != ":9090" || srv.ReadHeaderTimeout == 0 {
		t.Fatalf("server = %+v", srv)
	}
}
