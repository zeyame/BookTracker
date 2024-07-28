import React from 'react';
import ReactDOM from 'react-dom/client';
import {RouterProvider, createBrowserRouter} from 'react-router-dom';
import { SearchPage } from './pages/explore/SearchPage';
import { TrendingBooksPage } from './pages/explore/TrendingBooksPage';
import { PopularAuthorsPage } from './pages/explore/PopularAuthorsPage';
import { AddBookPage } from './pages/explore/AddBookPage';
import { ReadPage } from './pages/library/ReadPage';
import { ToReadPage } from './pages/library/ToReadPage';
import { ReadingPage } from './pages/library/ReadingPage';
import { GoalsPage } from './pages/activity/GoalsPage';
import { StatsPage } from './pages/activity/StatsPage';
import './styles/index.css';
import {App} from './App';
import { ErrorPage } from './pages/ErrorPage';
import { BookPage } from './pages/BookPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        path: '/',
        element: <SearchPage />
      },
      {
        path: '/trending-books',
        element: <TrendingBooksPage />
      },
      {
        path: '/popular-authors',
        element: <PopularAuthorsPage />
      },
      {
        path: '/add-book',
        element: <AddBookPage />
      },
      {
        path: '/read',
        element: <ReadPage />
      },
      {
        path: '/reading',
        element: <ReadingPage />
      },
      {
        path: '/to-read',
        element: <ToReadPage />
      },
      {
        path: '/goals',
        element: <GoalsPage />
      },
      {
        path: '/stats',
        element: <StatsPage />
      },
      {
        path: '/book/:bookId',
        element: <BookPage />
      }
    ],
    errorElement: <ErrorPage />
  },  
]);

const element = document.getElementById('root') as HTMLElement;

if (element) {
  const root = ReactDOM.createRoot(element);
  root.render(
    <React.StrictMode>
      <RouterProvider router={router} />
    </React.StrictMode>
  );  
}
else {
  console.error('Root element not found');
}
