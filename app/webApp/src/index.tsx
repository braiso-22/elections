import React from 'react';
import ReactDOM from 'react-dom/client';
import { Elecciones } from './Elecciones.tsx';

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('Failed to find the root element');

ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
    <h1>Elecciones</h1>
    <Elecciones />
  </React.StrictMode>
);
