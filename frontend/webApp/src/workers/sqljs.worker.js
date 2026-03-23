import initSqlJs from 'sql.js/dist/sql-wasm.js';
import sqlWasmUrl from 'sql.js/dist/sql-wasm.wasm?url';

const DB_NAME = 'app_db';
const STORE_NAME = 'table';
const STORAGE_NAME = "CacheFlowSqlStorage";

let db = null;
let idbInstance = null;
let isTransactionActive = false;
let SQL = null;

async function init() {
    try {
        if (!SQL) {
            SQL = await initSqlJs({locateFile: file => `${sqlWasmUrl}`});
        }

        if (!idbInstance) {
            idbInstance = await new Promise((resolve, reject) => {
                const request = indexedDB.open(STORAGE_NAME, 1);
                request.onupgradeneeded = (e) => {
                    if (!e.target.result.objectStoreNames.contains(STORE_NAME)) {
                        e.target.result.createObjectStore(STORE_NAME);
                    }
                };
                request.onsuccess = (e) => resolve(e.target.result);
                request.onerror = (e) => reject(e.target.error);
            });
        }

        const savedData = await getDbBuffer();
        db = savedData ? new SQL.Database(new Uint8Array(savedData)) : new SQL.Database();

        console.debug('[WebWorker] DEBUG: Database ready');
        return db;
    } catch (err) {
        console.error('[WebWorker] ERROR: Init error:', err);
        throw err;
    }
}

let sqlModuleReady = init();

async function getDbBuffer() {
    return new Promise((resolve, reject) => {
        const tx = idbInstance.transaction(STORE_NAME, "readonly");
        const store = tx.objectStore(STORE_NAME);
        const request = store.get(DB_NAME);
        request.onsuccess = () => resolve(request.result);
        request.onerror = () => reject(request.error);
    });
}

async function saveDb() {
    if (!db || !idbInstance) return;

    const data = db.export();

    return new Promise((resolve, reject) => {
        const tx = idbInstance.transaction(STORE_NAME, "readwrite");
        const store = tx.objectStore(STORE_NAME);

        tx.oncomplete = () => resolve();
        tx.onerror = () => reject(tx.error);

        store.put(data, DB_NAME);
    });
}

async function handleMessage(event) {
    const {id, action, sql, params} = event.data;

    if (!db) await sqlModuleReady;

    switch (action) {
        case "begin_transaction":
            if (!isTransactionActive) {
                db.exec("BEGIN TRANSACTION;");
                isTransactionActive = true;
            }
            return {id, results: {values: []}};

        case "end_transaction":
            if (isTransactionActive) {
                db.exec("COMMIT;");
                isTransactionActive = false;
                await saveDb();
            }
            return {id, results: {values: []}};

        case "rollback_transaction":
            if (isTransactionActive) {
                db.exec("ROLLBACK;");
                isTransactionActive = false;
            }
            return {id, results: {values: []}};

        case "exec":
            const results = db.exec(sql, params)[0] ?? {values: []};
            const isWrite = /^\s*(INSERT|UPDATE|DELETE|CREATE|DROP|ALTER)/i.test(sql);
            if (isWrite && !isTransactionActive) {
                await saveDb();
            }
            return {id, results};

        case "reload_db":
            if (db) {
                try {
                    db.close();
                } catch (e) {
                }
                db = null;
            }
            isTransactionActive = false;
            sqlModuleReady = init();
            await sqlModuleReady;
            return {id, results: {values: []}};

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
        postMessage({id: event.data.id, error: err.toString()});
    }
};