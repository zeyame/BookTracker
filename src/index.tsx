import React from 'react';
import ReactDOM from 'react-dom/client';
import {RouterProvider, createBrowserRouter} from 'react-router-dom';
import { Search } from './pages/explore/Search';
import { TrendingBooks } from './pages/explore/TrendingBooks';
import { PopularAuthors } from './pages/explore/PopularAuthors';
import { AddBook } from './pages/explore/AddBook';
import { Read } from './pages/library/Read';
import { ToRead } from './pages/library/ToRead';
import { Reading } from './pages/library/Reading';
import { Goals } from './pages/activity/Goals';
import { Stats } from './pages/activity/Stats';

import './index.css';
import {App} from './App';
import { ErrorPage } from './pages/ErrorPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        path: '/',
        element: <Search />
      },
      {
        path: '/trending-books',
        element: <TrendingBooks />
      },
      {
        path: '/popular-authors',
        element: <PopularAuthors />
      },
      {
        path: '/add-book',
        element: <AddBook />
      },
      {
        path: '/read',
        element: <Read />
      },
      {
        path: '/reading',
        element: <Reading />
      },
      {
        path: '/to-read',
        element: <ToRead />
      },
      {
        path: '/goals',
        element: <Goals />
      },
      {
        path: '/stats',
        element: <Stats />
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
