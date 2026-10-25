import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  root: '.',
  plugins: [react()],
  build: {
    outDir: 'dist',
    emptyOutDir: true,
  },
  // El servidor Ktor ocupa el 8080: Vite sirve en el 5173 y le reenvía la API.
  server: {
    port: 5173,
    proxy: { '/v1': 'http://localhost:8080' },
  },
});
