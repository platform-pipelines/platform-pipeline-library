import { createServer } from 'node:http';

import { handler } from './app.js';

const port = Number(process.env.PORT ?? 8080);

createServer(handler).listen(port, () => {
  console.log(`checkout-api ${process.env.APP_VERSION ?? 'dev'} listening on ${port}`);
});
