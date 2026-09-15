"""HTTP routes. Kept free of I/O at import time so tests need no server."""

import os

from flask import Flask, jsonify, request

from orders_api.orders import OrderError, price_order


def create_app() -> Flask:
    app = Flask(__name__)

    @app.get("/healthz")
    def healthz() -> tuple[str, int]:
        return "ok", 200

    @app.get("/version")
    def version():  # type: ignore[no-untyped-def]
        return jsonify(version=os.environ.get("APP_VERSION", "dev"))

    @app.post("/orders/quote")
    def quote():  # type: ignore[no-untyped-def]
        payload = request.get_json(silent=True) or {}
        try:
            total = price_order(payload.get("items", []))
        except OrderError as exc:
            return jsonify(error=str(exc)), 400
        return jsonify(total=total)

    return app
