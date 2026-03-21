import fs from "fs";
import crypto from "crypto";

const hash = crypto
  .createHash("md5")
  .update(Date.now().toString())
  .digest("hex")
  .slice(0, 8);

const swPath = "public/sw-loader.js";

let content = fs.readFileSync(swPath, "utf8");

content = content.replace(
  /^const CACHE_NAME = .*/m,
  `const CACHE_NAME = 'cacheflow-h${hash}'; \/\/ changes dynamically on every build`
);

fs.writeFileSync(swPath, content);

console.log(`[SW] cache version updated → cacheflow-h${hash}`);