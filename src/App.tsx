import React from "react";
import { Navbar } from "./components/Navbar";
import './index.css'

export const App: React.FC = () => {
  return (
    <div className="app-container">
      <nav>
        <Navbar />
      </nav>
      <main>
        <div className="page-content">
        </div>
      </main>
    </div>
  )
}
