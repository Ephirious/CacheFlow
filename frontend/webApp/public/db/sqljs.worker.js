importScripts('/db/sql-wasm.js');

let db = null;
let isTransactionActive = false;
const DB_NAME = 'app_db';
const STORE_NAME = 'table';

const STORAGE_NAME = "CacheFlowSqlStorage"

let SQL = null;
let sqlModuleReady = createDatabase();

async function createDatabase() {
  if (!SQL) {
    SQL = await initSqlJs({ locateFile: file => '/db/sql-wasm.wasm' });
  }
  const savedData = await getDbBuffer();
  db = savedData ? new SQL.Database(new Uint8Array(savedData)) : new SQL.Database();
}

async function getDbBuffer() {
  return new Promise((resolve) => {
    const request = indexedDB.open(STORAGE_NAME, 1);
    request.onupgradeneeded = (e) => e.target.result.createObjectStore(STORE_NAME);
    request.onsuccess = (e) => {
      const dbInstance = e.target.result;
      const tx = dbInstance.transaction(STORE_NAME, "readonly");
      const getReq = tx.objectStore(STORE_NAME).get(DB_NAME);
      getReq.onsuccess = () => resolve(getReq.result || null);
    };
  });
}

async function saveDb() {
  const data = db.export();
  const request = indexedDB.open(STORAGE_NAME, 1);
  request.onsuccess = (e) => {
    const dbInstance = e.target.result;
    const tx = dbInstance.transaction(STORE_NAME, "readwrite");
    tx.objectStore(STORE_NAME).put(data, DB_NAME);
  };
}

async function handleMessage(event) {
  const { id, action, sql, params } = event.data;

  switch (action) {
    case "begin_transaction":
      if (!isTransactionActive) {
        db.exec("BEGIN TRANSACTION;");
        isTransactionActive = true;
      }
      return { id, results: { values: [] } };

    case "end_transaction":
      if (isTransactionActive) {
        db.exec("COMMIT;");
        isTransactionActive = false;
        await saveDb();
      }
      return { id, results: { values: [] } };

    case "rollback_transaction":
      if (isTransactionActive) {
        db.exec("ROLLBACK;");
        isTransactionActive = false;
      }
      return { id, results: { values: [] } };

    case "exec":
      const results = db.exec(sql, params)[0] ?? { values: [] };
      const isWrite = /^\s*(INSERT|UPDATE|DELETE|CREATE|DROP|ALTER)/i.test(sql);
      if (isWrite && !isTransactionActive) {
        await saveDb();
      }
      return { id, results };

    case "reload_db":
      if (db) {
        try { db.close(); } catch (e) {}
        db = null;
      }
      isTransactionActive = false;
      sqlModuleReady = createDatabase();
      await sqlModuleReady;
      return { id, results: { values: [] } };

    default:
      throw new Error(`Unsupported action: ${action}`);
  }
}

self.onmessage = async (event) => {
  try {
    await sqlModuleReady;
    const response = await handleMessage(event);
    postMessage(response);
  } catch (err) {
    postMessage({ id: event.data.id, error: err.toString() });
  }
};