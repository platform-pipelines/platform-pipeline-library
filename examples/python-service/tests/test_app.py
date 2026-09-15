import pytest

from orders_api.app import create_app
from orders_api.orders import OrderError, price_order


@pytest.fixture
def client():
    return create_app().test_client()


def test_healthz(client):
    response = client.get("/healthz")
    assert response.status_code == 200
    assert response.text == "ok"


def test_version_reads_app_version(client, monkeypatch):
    monkeypatch.setenv("APP_VERSION", "1.4.0")
    assert client.get("/version").json == {"version": "1.4.0"}


def test_quote(client):
    response = client.post("/orders/quote", json={"items": [{"price": "2.50", "quantity": 2}]})
    assert response.status_code == 200
    assert response.json == {"total": "5.00"}


def test_quote_rejects_bad_items(client):
    response = client.post("/orders/quote", json={"items": [{"price": "x", "quantity": 1}]})
    assert response.status_code == 400


def test_bulk_discount():
    assert price_order([{"price": "1.00", "quantity": 10}]) == "9.00"


def test_no_discount_below_threshold():
    assert price_order([{"price": "1.00", "quantity": 9}]) == "9.00"


@pytest.mark.parametrize(
    "item",
    [{"price": "1.00"}, {"price": "-1", "quantity": 1}, {"price": "1", "quantity": 0}],
)
def test_invalid_items(item):
    with pytest.raises(OrderError):
        price_order([item])
