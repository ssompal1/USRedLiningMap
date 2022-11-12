import React from 'react';
import './App.css';
import RedLineMap from './Map';
import Demo from './Map';

/**
 * This is the App class. It renders Demo and therefore produces an interactive map with redlining data overlayed.
 * @returns 
 */
function App() {
  return (
    <div className="App">
      <RedLineMap/>      
    </div>
  );
}

export default App;
