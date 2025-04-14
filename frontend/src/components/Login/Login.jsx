import React, { useState }  from 'react';
import { useNavigate } from 'react-router-dom';

const Login = () => {
    let navigate = useNavigate();
  const [formData, setFormData] = useState({
          userName: "",
          password: ""
      });
  
      const handleChange = (e) => {
          setFormData({ ...formData, [e.target.name]: e.target.value });
      };
  
      const handleSubmit = async (event) =>
      {
          event.preventDefault();
          try
          {
              const response = await fetch("http://localhost:8080/api/auth/login",{
                  method: 'POST',
                  headers: {
                      'Content-Type': 'application/json'
                  },
                  body: JSON.stringify(formData)
              });
  
              if (response.ok) {
                  alert('User logged in successfully!');
                  navigate('/home')
              } 
              else {
                  const error = await response.json();
                  alert(error.message);
          }
          } catch (error) {
              console.error('Error logging in:', error);
          }
      }
  
    return (
      <div className='min-h-screen flex items-center justify-center bg-gray-100'>
          <div className="bg-white p-8 rounded-2xl shadow-lg w-full max-w-md">
          <h2 className="text-2xl font-semibold text-center mb-6">Login</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
              <div className='input'>
                  <label htmlFor='userName'>
                      User Name
                  </label>
                  <input 
                  type='text' 
                  id='userName' 
                  name= 'userName'
                  value={formData.userName}
                  className="w-full p-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onChange={handleChange}
                  required
                  />
              </div>
              <div className='input'>
                  <label htmlFor='password'>
                      Password
                  </label>
                  <input 
                  type='password' 
                  id='password' 
                  name='password'
                  value={formData.password}
                  className="w-full p-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
                  onChange={handleChange}
                  required
                  />
              </div>
              <button type='submit' className='w-full bg-blue-500 text-white p-3 rounded-xl hover:bg-blue-600 transition'>
                  Submit
              </button>
          </form>
          {/* {message && <p>{message}</p>} */}
          </div>
      </div>
    )
}

export default Login