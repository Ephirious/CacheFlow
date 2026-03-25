import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';
import path from 'path';
import crypto from "crypto";

const hash = crypto
    .createHash("md5")
    .update(Date.now().toString())
    .digest("hex")
    .slice(0, 8);

export default defineConfig({
    root: '.',
    publicDir: 'public',
    base: '/',
    worker: {
        format: 'es'
    },
    define: {
        '__BUILD_HASH__': JSON.stringify(hash)
    },
    plugins: [
        react(),
        tsconfigPaths(),
        {
            name: 'sw-headers',
            configureServer(server) {
                server.middlewares.use((req, res, next) => {
                    if (req.url?.includes('sw.js')) {
                        res.setHeader('Service-Worker-Allowed', '/');
                    }
                    next();
                });
            },
            configurePreviewServer(server) {
                server.middlewares.use((req, res, next) => {
                    if (req.url?.includes('sw.js')) {
                        res.setHeader('Service-Worker-Allowed', '/');
                    }
                    next();
                });
            }
        }
    ],
    build: {
        outDir: 'dist',
        emptyOutDir: true,
        minify: "terser",
        terserOptions: {
            keep_classnames: true,
            keep_fnames: true
        },
        sourcemap: true,
        rollupOptions: {
            input: {
                main: path.resolve(__dirname, 'index.html'),
                'sw': './src/workers/sw.js',
                'sqljs.worker': './src/workers/sqljs.worker.js'
            },
            output: {
                entryFileNames: (chunkInfo) => {
                    if (chunkInfo.name === 'sw' || chunkInfo.name === 'sqljs.worker') {
                        return 'src/workers/[name].js';
                    }
                    return 'assets/[name]-[hash].js';
                }
            }
        }
    },
    server: {
        port: 8080,
        headers: {
            'Cross-Origin-Opener-Policy': 'same-origin',
            'Cross-Origin-Embedder-Policy': 'require-corp',
        },

    },
    preview: {
        port: 4173,
        headers: {
            'Cross-Origin-Opener-Policy': 'same-origin',
            'Cross-Origin-Embedder-Policy': 'require-corp',
        },

    },
    optimizeDeps: {
        exclude: ['@sqlite.org/sqlite-wasm'],
    }

})
;