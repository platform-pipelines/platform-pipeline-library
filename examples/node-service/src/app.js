// Request handling, with no listening socket, so tests call it directly.

const VAT_RATE = 0.2;

export function quote(items) {
  if (!Array.isArray(items) || items.length === 0) {
    throw new Error('items must be a non-empty array');
  }
  const pence = items.reduce((sum, item) => {
    const price = Math.round(Number(item?.price) * 100);
    const quantity = Number(item?.quantity);
    if (!Number.isFinite(price) || price < 0 || !Number.isInteger(quantity) || quantity <= 0) {
      throw new Error(`invalid item: ${JSON.stringify(item)}`);
    }
    return sum + price * quantity;
  }, 0);
  const vat = Math.round(pence * VAT_RATE);
  return { net: pence / 100, vat: vat / 100, gross: (pence + vat) / 100 };
}

function send(res, status, body) {
  res.writeHead(status, { 'content-type': 'application/json' });
  res.end(JSON.stringify(body));
}

async function readJson(req) {
  let raw = '';
  for await (const chunk of req) {
    raw += chunk;
    if (raw.length > 64_000) {
      throw new Error('body too large');
    }
  }
  return raw ? JSON.parse(raw) : {};
}

export async function handler(req, res) {
  if (req.method === 'GET' && req.url === '/healthz') {
    return send(res, 200, { status: 'ok' });
  }
  if (req.method === 'GET' && req.url === '/version') {
    return send(res, 200, { version: process.env.APP_VERSION ?? 'dev' });
  }
  if (req.method === 'POST' && req.url === '/checkout/quote') {
    try {
      const body = await readJson(req);
      return send(res, 200, quote(body.items));
    } catch (err) {
      return send(res, 400, { error: err.message });
    }
  }
  return send(res, 404, { error: 'not found' });
}
