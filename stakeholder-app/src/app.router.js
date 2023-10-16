import React from 'react';
import { Route, Routes } from 'react-router-dom';
import AuthGuard from './guards/auth.guard';

import LoginComponent from "./components/login.component";
import ProfileComponent from "./components/profile.component";
import RegisterComponent from "./components/register.component";
import NotFoundComponent from "./components/not-found.component";
import EditTheatre  from './components/edit-theatre.component';
import TheatreList from './components/theatre-list.component';
const AppRouter = () => {
    return (
        <Routes>
            <Route exact path='/' element={<LoginComponent />} />
            <Route path='/register/:authtoken' element={<RegisterComponent />} />
            
            <Route path='/edit-theatre/' element={<EditTheatre />} />
            <Route path='/edit-theatre/:id' element={<EditTheatre />} />
            <Route path='/list-theatre' element={<TheatreList />} />
            
            <Route element={<AuthGuard />}>
                <Route path='/profile' element={<ProfileComponent />} />
            </Route>
            <Route path='*' element={<NotFoundComponent />} />
        </Routes>
    )
}

export default AppRouter;