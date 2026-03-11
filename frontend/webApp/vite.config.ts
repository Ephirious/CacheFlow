import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

export default defineConfig({
    root: '.',
    base: '/',
    plugins: [react(), tsconfigPaths()],
    build: {
        outDir: 'dist',
        emptyOutDir: true,
        minify: 'terser'
    },
    server: {
        port: 8080
    },
});