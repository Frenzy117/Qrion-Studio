import React, { useState } from 'react';
import Alert from '@mui/material/Alert';

const Register = () => {
    const [alert,setAlert] = useState(false);
    const [alertSev,setAlertSev] = useState(false);
    const [alertTitle,setAlertTitle] = useState(false);
    const [formData, setFormData] = useState({
        name: "",
        email: "",
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
            const response = await fetch("http://localhost:8080/api/auth/register",{
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(formData),
            });

            if (response.ok) {
                console.log('User registered successfully!');
            } 
            else {
                const error = await response.status;
                // alert(error);
                console.log(error);
                
            }
        } catch (error) {
            setAlert(true);
            setAlertSev('error');
            setAlertTitle('Error Registering');
            console.error('Error registering:', error);
        }
    }

  return (
        <div className='min-h-screen flex items-center justify-center  flex-col'>
            <div className="bg-white p-8 rounded-2xl shadow-lg w-full max-w-md">
                <h2 className="text-2xl font-semibold text-center mb-6">Register</h2>
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div className='input'>
                        <label htmlFor='name'>
                            Name
                        </label>
                        <input 
                        type='text' 
                        id='name' 
                        name='name'
                        value={formData.name}
                        className="w-full p-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
                        onChange={handleChange}
                        required
                        />
                    </div>
                    <div className='input'>
                        <label htmlFor='email'>
                            Email
                        </label>
                        <input 
                        type='email' 
                        id='email' 
                        name='email'
                        value={formData.email}
                        className="w-full p-3 border border-gray-300 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500"
                        onChange={handleChange}
                        required
                        />
                    </div>
                    <div className='input'>
                        <label htmlFor='userName'>
                            User Name
                        </label>
                        <input 
                        type='text' 
                        id='userName' 
                        name='userName'
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
            </div>
        </div>
  )
};

export default Register;