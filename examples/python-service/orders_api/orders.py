"""Pricing rules — pure functions, so they are trivial to test."""

from collections.abc import Iterable, Mapping
from decimal import ROUND_HALF_UP, Decimal
from typing import Any

BULK_THRESHOLD = 10
BULK_DISCOUNT = Decimal("0.10")


class OrderError(ValueError):
    """The order cannot be priced."""


def price_order(items: Iterable[Mapping[str, Any]]) -> str:
    """Total price for a list of {"price": "9.99", "quantity": 2} items.

    Ten or more units in total get a bulk discount. Returned as a string so the
    JSON response never carries a float rounding error.
    """
    total = Decimal("0")
    units = 0
    for item in items:
        try:
            price = Decimal(str(item["price"]))
            quantity = int(item["quantity"])
        except (KeyError, TypeError, ValueError, ArithmeticError) as exc:
            raise OrderError(f"invalid item: {item!r}") from exc
        if price < 0 or quantity <= 0:
            raise OrderError(f"invalid item: {item!r}")
        total += price * quantity
        units += quantity

    if units >= BULK_THRESHOLD:
        total -= total * BULK_DISCOUNT

    return str(total.quantize(Decimal("0.01"), rounding=ROUND_HALF_UP))
