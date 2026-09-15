import assert from 'node:assert/strict';
import { once } from 'node:events';
import { createServer } from 'node:http';
import { after, before, describe, it } from 'node:test';

import { handler, quote } from '../src/app.js';

describe('quote', () => {
  it('adds VAT to the net total', () => {
    assert.deepEqual(quote([{ price: '10.00', quantity: 2 }]), { net: 20, vat: 4, gross: 24 });
  });

  it('avoids float rounding errors', () => {
    assert.deepEqual(quote([{ price: 0.1, quantity: 3 }]), { net: 0.3, vat: 0.06, gross: 0.36 });
  });

  it('rejects empty and invalid items', () => {
    assert.throws(() => quote([]), /non-empty/);
    assert.throws(() => quote([{ price: 'x', quantity: 1 }]), /invalid item/);
    assert.throws(() => quote([{ price: 1, quantity: 0 }]), /invalid item/);
  });
});

describe('handler', () => {
  let server;
  let base;

  before(async () => {
    server = createServer(handler).listen(0);
    await once(server, 'listening');
    base = `http://127.0.0.1:${server.address().port}`;
  });

  after(() => server.close());

  it('serves healthz', async () => {
    const res = await fetch(`${base}/healthz`);
    assert.equal(res.status, 200);
    assert.deepEqual(await res.json(), { status: 'ok' });
  });

  it('serves the version', async () => {
    const res = await fetch(`${base}/version`);
    assert.equal(res.status, 200);
    assert.ok((await res.json()).version);
  });

  it('quotes a basket', async () => {
    const res = await fetch(`${base}/checkout/quote`, {
      method: 'POST',
      body: JSON.stringify({ items: [{ price: 5, quantity: 1 }] }),
    });
    assert.equal(res.status, 200);
    assert.deepEqual(await res.json(), { net: 5, vat: 1, gross: 6 });
  });

  it('returns 400 for a bad basket', async () => {
    const res = await fetch(`${base}/checkout/quote`, { method: 'POST', body: '{"items": []}' });
    assert.equal(res.status, 400);
  });

  it('returns 400 for malformed JSON', async () => {
    const res = await fetch(`${base}/checkout/quote`, { method: 'POST', body: '{nope' });
    assert.equal(res.status, 400);
  });

  it('returns 404 for unknown routes', async () => {
    const res = await fetch(`${base}/nope`);
    assert.equal(res.status, 404);
  });
});
