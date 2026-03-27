import sqlite3InitModule from "@sqlite.org/sqlite-wasm";
import { openDB } from 'idb';

const DB_KEY = 'app_db';
const STORE_NAME = 'table';
const IDB_NAME = "CacheFlowSqlStorage";
const OPFS_FILE_NAME = "cacheflow.db";

let db = null;
let sqlite3 = null;
let isTransactionActive = false;
let initPromise = null;

const syncChannel = new BroadcastChannel('sqlite_sync_channel');

async function withIDB(mode, callback) {
    const idb = await openDB(IDB_NAME, 1, {
        upgrade(db) {
            if (!db.objectStoreNames.contains(STORE_NAME)) {
                db.createObjectStore(STORE_NAME);
            }
        }
    });
    try {
        return await callback(idb);
    } finally {
        idb.close();
    }
}

async function init() {
    try {
        if (!sqlite3) sqlite3 = await sqlite3InitModule();

        const isOpfsSupported = !!sqlite3.opfs;

        if (isOpfsSupported) {
            console.debug('[App] SQL: OPFS mode');
            db = new sqlite3.oo1.OpfsDb(OPFS_FILE_NAME, "c");
        } else {
            console.warn('[App] SQL: IDB fallback mode');
            const bytes = await withIDB('readonly', (idb) => idb.get(STORE_NAME, DB_KEY));

            if (bytes && bytes.byteLength > 100) {
                const tempName = `restore_${Date.now()}.db`;
                sqlite3.capi.sqlite3_js_posix_create_file(tempName, bytes);
                db = new sqlite3.oo1.DB(tempName, "c");
            } else {
                db = new sqlite3.oo1.DB(":memory:", "c");
            }
        }

        db.exec("PRAGMA journal_mode=WAL; PRAGMA synchronous=NORMAL; PRAGMA auto_vacuum=INCREMENTAL;");

    } catch (err) {
        console.error('[App] SQL: Init error:', err);
        throw err;
    }
}

initPromise = init();

async function saveDb() {
    if (!db || !sqlite3) return;
        const isOpfsDb = sqlite3.oo1.OpfsDb && db instanceof sqlite3.oo1.OpfsDb;

        if (isOpfsDb) return;
    try {
        db.exec("PRAGMA incremental_vacuum(0); PRAGMA shrink_memory;");
        const bytes = sqlite3.capi.sqlite3_js_db_export(db.pointer);
        if (bytes) {
            await withIDB('readwrite', (idb) => idb.put(STORE_NAME, bytes, DB_KEY));
        }
    } catch (e) {
        console.error('[App] SQL: Save error:', e);
    }
}

async function handleAction(data) {
    const { id, action, sql, params } = data;

    switch (action) {
        case "exec":
            const rows = db.exec({
                sql: sql,
                bind: params || [],
                rowMode: "array",
                returnValue: "resultRows"
            });

            const isWrite = /^\s*(INSERT|UPDATE|DELETE|CREATE|DROP|ALTER|REPLACE)/i.test(sql);
            if (isWrite && !isTransactionActive) {
                await saveDb();
            }

            return { id, results: { values: rows || [] }, isWrite: isWrite };

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
            return { id, results: { values: [] }, isEnd: true };

        case "rollback_transaction":
            if (isTransactionActive) {
                db.exec("ROLLBACK;");
                isTransactionActive = false;
            }
            return { id, results: { values: [] } };

        default:
            throw new Error(`Unsupported action: ${action}`);
    }
}

self.onmessage = async (event) => {
    const data = event.data;

    try {
        await initPromise;
        const response = await handleAction(data);
        const shouldSync = response.isWrite || response.isEnd;

        delete response.isWrite;
        delete response.isEnd;

        postMessage(response);

        if (shouldSync) {
            if (data.sql) {
                response.tables = extractTables(data.sql);
            }
            syncChannel.postMessage(response);
        }
    } catch (err) {
        postMessage({
            id: data.id,
            error: err.message || err.toString()
        });
    }
};

function extractTables(sql) {
    const tables = [];

    const regex = /(?:FROM|UPDATE|INTO|TABLE|JOIN|DELETE\s+FROM)\s+([a-zA-Z0-9_]+)/gi;
    let match;
    while ((match = regex.exec(sql)) !== null) {
        tables.push(match[1]);
    }
    return [...new Set(tables)];
}