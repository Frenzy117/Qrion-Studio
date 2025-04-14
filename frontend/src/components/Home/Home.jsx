import React from 'react'
import { Link, useNavigate } from 'react-router-dom';
import { SparklesCore } from "../ui/sparkles.jsx";
import { Button } from '@mui/material';

const Home = () => {
    let navigate = useNavigate();
    return (
      <div className='bg-[#121212] h-screen box-border'>
        <div className=" text-[#F8F8F8] px-6 py-2 flex justify-between items-end relative">
            <div className="flex gap-5">
              <Button sx={{color: "#F8F8F8", textTransform: "none", fontFamily: "satoshi"}} variant='text'>
                <Link to={'/playground'}>
                  Playground
                </Link>
                </Button>
              <Button sx={{color: "#F8F8F8", textTransform: "none", fontFamily: "satoshi"}} variant='text'>History</Button>
              <Button sx={{color: "#F8F8F8", textTransform: "none", fontFamily: "satoshi"}} variant='text'>Settings</Button>
            </div>
            <div className='flex gap-5'>
            <Button sx={{color: "#F8F8F8", textTransform: "none", fontFamily: "satoshi"}} variant='text'>
              <Link to={'/login'}>
                Login
              </Link>
              </Button>
            <Button sx={{color: "#F8F8F8", textTransform: "none", fontFamily: "satoshi"}} variant='text'>
              <Link to={'/register'}>
                Sign Up
              </Link>
              </Button>
            </div>
          </div>
        <div className="bg-[#121212] min-h-screen flex flex-col items-center justify-center">
          
          <h1 className='text-white text-xl font-extralight'>This is&thinsp;<br/>
            <span className='text-[#3ABEFF] text-7xl'>Qrion</span>
            </h1>
            <div className="w-[40rem] h-40 relative">
        {/* Gradients */}
        <div className="absolute inset-x-20 top-0 bg-gradient-to-r from-transparent via-indigo-500 to-transparent h-[2px] w-3/4 blur-sm" />
        <div className="absolute inset-x-20 top-0 bg-gradient-to-r from-transparent via-indigo-500 to-transparent h-px w-3/4" />
        <div className="absolute inset-x-60 top-0 bg-gradient-to-r from-transparent via-sky-500 to-transparent h-[5px] w-1/4 blur-sm" />
        <div className="absolute inset-x-60 top-0 bg-gradient-to-r from-transparent via-sky-500 to-transparent h-px w-1/4" />
 
        {/* Core component */}
        <SparklesCore
          background="transparent"
          minSize={0.4}
          maxSize={1}
          particleDensity={1200}
          className="w-full h-full"
          particleColor="#FFFFFF"
        />
 
        {/* Radial Gradient to prevent sharp edges */}
        <div className="absolute inset-0 w-full h-full bg-[#121212] [mask-image:radial-gradient(350px_200px_at_top,transparent_20%,white)]"></div>
      </div>
        </div>
        </div>
      );
}

export default Home