import React from 'react';
import ReactDOM from 'react-dom/client';
import {Navigate, RouterProvider, createBrowserRouter} from 'react-router-dom';
import { SearchPage } from './pages/explore/SearchPage';
import { ReadPage } from './pages/library/ReadPage';
import { ToReadPage } from './pages/library/ToReadPage';
import { ReadingPage } from './pages/library/ReadingPage';
import './styles/index.css';
import {App} from './App';
import { ErrorPage } from './pages/ErrorPage';
import { BookPage } from './pages/sub-pages/BookPage';
import { AllSimilarBooksPage } from './pages/sub-pages/AllSimilarBooksPage';
import { LoginPage } from './pages/account/LoginPage';
import { RegistrationPage } from './pages/account/RegistrationPage';
import { VerificationPage } from './pages/account/VerificationPage';

const router = createBrowserRouter([ 
  {
    path: "/",
    element: <Navigate to={"/user/registration"} replace />
  },
  { 
    path: '/user/registration',
    element: <RegistrationPage />,  
  },
  {
    path: '/user/verification',
    element: <VerificationPage />
  },
  {
    path: '/user/login',
    element: <LoginPage />
  },
  {
    path: '/app',
    element: <App />,
    children: [
      {
        path: '',
        element: <SearchPage />
      },
      {
        path: 'read',
        element: <ReadPage />
      },
      {
        path: 'currently-reading',
        element: <ReadingPage />
      },
      {
        path: 'to-read',
        element: <ToReadPage />
      },
      {
        path: 'book/:bookId',
        element: <BookPage />
      },
      {
        path: 'similar-books/:bookId',
        element: <AllSimilarBooksPage />
      }
    ],
    errorElement: <ErrorPage />
  }
]);

const element = document.getElementById('root') as HTMLElement;

if (element) {
  const root = ReactDOM.createRoot(element);
  root.render(
    <RouterProvider router={router} />
  );  
}
else {
  console.error('Root element not found');
}
