import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Playground from './components/Playground/Playground';
import './styles/App.css';

const App = () => {
  return (
    <Router>
      <Switch>
        <Route path="/" exact component={Playground} />
      </Switch>
    </Router>
  );
}

export default App;