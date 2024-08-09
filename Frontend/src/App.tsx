import React from "react";
import { Navbar } from "./components/Global/Navbar";
import { Outlet } from "react-router-dom";
import './styles/app.css'

export const App: React.FC = () => {
  return (
    <div className="app-container">
      <Navbar />
      <main>
        <Outlet />
      </main>
    </div>
  )
}
