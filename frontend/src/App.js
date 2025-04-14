import './App.css';
import Register from './components/Register/Register';
import Home from './components/Home/Home';
import { Routes, Route } from 'react-router-dom';
import Login from './components/Login/Login';
import Playground from './components/Playground/Playground';

function App() {
  return (
    <>
      <Routes>
        <Route index path="/home" element={<Home/>}/>
        <Route path="/playground" element={<Playground/>}/>
        <Route path="/register" element={<Register />} />
        <Route path='/login' element={<Login/>}/>
      </Routes>
    </>
  );
}

export default App;
