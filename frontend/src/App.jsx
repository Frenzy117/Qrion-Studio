import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

import Register from './components/Register/Register';
import Home from './components/Home/Home';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './components/Login/Login';
import Playground from './components/Playground/Playground';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route index path="/home" element={<Home/>}/>
        <Route path="/playground" element={<Playground/>}/>
        <Route path="/register" element={<Register />} />
        <Route path='/login' element={<Login/>}/>
      </Routes>
    </BrowserRouter>
  );
}

export default App;