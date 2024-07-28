import { React, useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axiosConfig";
import { FallingLines } from "react-loader-spinner";

function DetailsPage() {
  const { id } = useParams();
  const [book, setBook] = useState(null);
  const [added, setAdded] = useState(null);

  useEffect(() => {
    searchBook();
  }, [id]);

  useEffect(() => {
    if (book) {
      bookInMyBooks();
    }
  }, [book]);

  const bookInMyBooks = async () => {
    try {
      const response = await api.get(`/api/v1/search/inMyBooks-${id}`);
      setAdded(response.data);
    } catch (error) {
      console.error(
        "Error fetching books:",
        error.response ? error.response.data : error.message
      );
    }
  };

  const searchBook = async () => {
    try {
      const response = await api.get(`/api/v1/search/details-${id}`);
      setBook(response.data);
    } catch (error) {
      console.error(
        "Error fetching books:",
        error.response ? error.response.data : error.message
      );
    }
  };

  const toggleMyBooks = async () => {
    const newVal = !added;
    try {
      var response;
      response = newVal
        ? await api.post(`/api/v1/search/add-book`, book)
        : await api.delete(`/api/v1/search/delete-book-${id}`);
    } catch (error) {
      console.error(
        "Error fetching books:",
        error.response ? error.response.data : error.message
      );
    }

    setAdded(newVal);
  };

  function formatName(name) {
    if (name.includes(",")) {
      const [lastName, firstName] = name.split(",").map((part) => part.trim());
      return `${firstName} ${lastName}`;
    }
    return name.trim();
  }

  function convertAuthorsToString() {
    const newArr = book.authors.map((author) => formatName(author.name));
    return newArr.join(", ");
  }
  // Change added to book
  return (
    <>
      <div className="flex h-screen flex-col px-6 pt-6 bg-background">
        {added !== null ? (
          <>
            <div className="flex justify-between items-center mb-7">
              <div className="flex-1 text-center mb-4">
                <h1 className="text-5xl">{book.title}</h1>
              </div>
              <button
                className="bg-primary p-2 rounded-xl text-onPrimary hover:bg-sky-900"
                onClick={toggleMyBooks}
              >
                <i
                  className={`fa ${added ? "fa-check" : "fa-plus"}`}
                  aria-hidden="true"
                ></i>
                <span className="ml-2">My Books</span>
              </button>
            </div>
            <div className="bg-container rounded-xl p-4 shadow border-2 border-primary text-3xl">
              <h2 className="mb-7">Authors: {convertAuthorsToString()}</h2>
              <h2 className="mb-7">Subjects: {book.subjects.join(", ")}</h2>
              <h2 className="mb-7">
                Bookshelves: {book.bookshelves.join(", ")}
              </h2>
              <h2 className="mb-7">
                Downloads (last 30 days): {book.download_count}
              </h2>
            </div>
          </>
        ) : (
          // If book is not loaded yet, show loading spinner
          <div className="flex justify-center items-center h-screen">
            <FallingLines
              color="#415f91"
              width="100"
              visible={true}
              ariaLabel="falling-circles-loading"
            />
          </div>
        )}
      </div>
    </>
  );
}

export default DetailsPage;
