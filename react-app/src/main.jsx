import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import ReactDOM from "react-dom/client";
import SearchPage from "./pages/SearchPage.jsx";
import Navbar from "./components/Navbar.jsx";
import MyBooksPage from "./pages/MyBooksPage.jsx";
import NoPage from "./pages/NoPage.jsx";
import DetailsPage from "./pages/DetailsPage.jsx";
import SpecificSearchPage from "./pages/SpecificSearchPage.jsx";
import BookSearchPage from "./pages/BookSearchPage.jsx";
import PhraseDetailPage from "./pages/PhraseDetailPage.jsx";
import "./index.css";

export default function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Navbar />}>
            <Route index element={<SearchPage />} />
            <Route path="search-books/:term" element={<SpecificSearchPage />} />
            <Route path="search-books" element={<SearchPage />} />
            <Route path="my-books" element={<MyBooksPage />} />
            <Route path="details/:id" element={<DetailsPage />} />
            <Route path="book-search/:id" element={<BookSearchPage />} />
            <Route path="search-details" element={<PhraseDetailPage />} />
            <Route path="*" element={<NoPage />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
