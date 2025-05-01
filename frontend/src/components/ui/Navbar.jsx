import React from 'react';
import {Button} from '@mui/material';
import { Link } from 'react-router-dom';
import HistoryIcon from '@mui/icons-material/History';
import SettingsIcon from '@mui/icons-material/Settings';
import LogoutIcon from '@mui/icons-material/Logout';

const Navbar = (screen) => {
  return (
      <div className="text-[#F8F8F8] px-6 py-2 flex justify-between items-center relative">
        <div className="text-3xl font-extralight text-[#3ABEFF] font-sans">
          <Link to={'/'}>
            Qrion
          </Link>
        </div>
        <div className="flex gap-2.5">
          <Button sx={{ color: "#F8F8F8", textTransform: "none", fontFamily: "satoshi" }} variant='text'>
            <HistoryIcon sx={{ color: "#F8F8F8", fontSize: "1.25rem"}} />
          </Button>
          <Button sx={{ color: "#F8F8F8", textTransform: "none", fontFamily: "satoshi" }} variant='text'>
            <SettingsIcon sx={{ color: "#F8F8F8", fontSize: "1.25rem",}} />
          </Button>
          <Button sx={{ color: "#F8F8F8", textTransform: "none", fontFamily: "satoshi" }} variant='text'>
            <LogoutIcon sx={{ color: "#F8F8F8", fontSize: "1.25rem"}} />
          </Button>
        </div>
      </div>
  )
}

export default Navbar;