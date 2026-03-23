import sqlite3InitModule from "@sqlite.org/sqlite-wasm";
import {openDB} from 'idb';

const DB_KEY = 'app_db';
const STORE_NAME = 'table';
const IDB_NAME = "CacheFlowSqlStorage";

let db = null;
let sqlite3 = null;
let isTransactionActive = false;
let initPromise = null;

async function withIDB(mode, callback) {
    const idb = await openDB(IDB_NAME, 1, {
        upgrade(db) {
            if (!db.objectStoreNames.contains(STORE_NAME)) {
                db.createObjectStore(STORE_NAME);
            }
        },
        blocking() {
            idb.close();
        }
    });

    try {
        return await callback(idb);
    } finally {
        idb.close();
    }
}

let currentDbFileName = null;

async function init() {
    try {
        if (!sqlite3) sqlite3 = await sqlite3InitModule();

        const bytes = await withIDB('readonly', (idb) => idb.get(STORE_NAME, DB_KEY));

        if (currentDbFileName) {
            try {
                sqlite3.capi.sqlite3_js_posix_unlink(currentDbFileName);
            } catch (e) {
            }
        }

        if (bytes && bytes.byteLength > 100) {
            currentDbFileName = `live_db_${Date.now()}.db`;
            sqlite3.capi.sqlite3_js_posix_create_file(currentDbFileName, bytes);
            db = new sqlite3.oo1.DB(currentDbFileName, "c");
            console.debug('[App] SQL: IDB mode');
        } else {
            currentDbFileName = null;
            db = new sqlite3.oo1.DB(":memory:", "c");
            console.debug('[App] SQL: IDB mode (fresh)');
        }

        db.exec("PRAGMA journal_mode=DELETE; PRAGMA synchronous=OFF; PRAGMA auto_vacuum=INCREMENTAL;");

    } catch (err) {
        console.error('[App] SQL: Init error:', err);
        throw err;
    }
}

initPromise = init();

async function saveDb() {
    if (!db || !sqlite3) return;

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
    const {id, action, sql, params} = data;

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

            return {id, results: {values: rows || []}};

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

        case "reload_db":
            if (db) db.close();
            db = null;
            isTransactionActive = false;
            initPromise = init();
            await initPromise;
            return {id, results: {values: []}};

        default:
            throw new Error(`Unsupported action: ${action}`);
    }
}

self.onmessage = async (event) => {
    try {
        await initPromise;

        const response = await handleAction(event.data);
        postMessage(response);
    } catch (err) {
        postMessage({
            id: event.data.id,
            error: err.message || err.toString()
        });
    }
};