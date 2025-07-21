import React from 'react';
import { Desktop, Tablet, Mobile } from './components/common/Responsive';

function App() {
  return (
    <div>
      <Desktop>
        <h1>Desktop View</h1>
      </Desktop>
      <Tablet>
        <h1>Tablet View</h1>
      </Tablet>
      <Mobile>
        <h1>Mobile View</h1>
      </Mobile>
    </div>
  );
}

export default App;
