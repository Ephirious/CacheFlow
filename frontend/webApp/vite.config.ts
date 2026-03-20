import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';
import copy from 'rollup-plugin-copy'

export default defineConfig({
    root: '.',
    publicDir: 'public',
    base: '/',
    plugins: [
        react(),
        tsconfigPaths(),
        copy({
            targets: [
                {
                    src: '../k2ts-service/build/dist/js/productionExecutable/k2ts-service.js',
                    dest: 'public'
                }
            ]
        })
    ],
    build: {
        outDir: 'dist',
        emptyOutDir: true,
        minify: 'terser'
    },
    server: {
        port: 8080
    },
});