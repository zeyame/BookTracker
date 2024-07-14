import React from "react";
import { Navbar } from "./components/Navbar";
import './index.css'
import './app.css'

export const App: React.FC = () => {
  return (
    <div className="app-container">
      <Navbar />
      <main>
        <div className="page-content">
          hello
        </div>
      </main>
    </div>
  )
}
