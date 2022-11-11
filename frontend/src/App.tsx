import React from 'react';
import './App.css';
import Demo from './MapDemo';

/**
 * This is the App class. It renders Demo and therefore produces a map
 * of Providence with redlining data overlayed.
 * @returns 
 */
function App() {
  return (
    <div className="App">
      <Demo/>      
    </div>
  );
}

export default App;
