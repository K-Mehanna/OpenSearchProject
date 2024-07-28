import { useState, useEffect } from "react";
import ResultsTile from "../components/ResultsTile";
import api from "../api/axiosConfig";
import { FallingLines } from "react-loader-spinner";

function MyBooksPage() {
  const [storedItems, setStoredItems] = useState(null);
  const [books, setBooks] = useState(null);

  useEffect(() => {
    getMyBooks();
  }, [storedItems]);

  const getMyBooks = async () => {
    try {
      const response = await api.get("/api/v1/search/my-books");
      const foundBooks = response.data;
      setBooks(foundBooks);
      if (foundBooks !== null) {
        for (let i = 0; i < foundBooks.length; i++) {
          const book = foundBooks[i];
          let response = await api.put(`/api/v1/search/add-doc-${book.id}`);
        }
      }
    } catch (error) {
      console.error(
        "Error fetching books: ",
        error.response ? error.response.data : error.message
      );
    }
  };

  return (
    <>
      <div className="flex h-screen flex-col px-6 pt-6 bg-background">
        {books ? (
          books.length > 0 ? (
            <>
              <h1 className="text-5xl mb-7 text-center">
                My Books ({books ? books.length : "..."})
              </h1>
              <div className="flex-1 overflow-auto">
                <ul>
                  {books.map((book) => (
                    <li key={book.title}>
                      <ResultsTile
                        book={book}
                        myBooks={true}
                        setMyBooksState={setStoredItems}
                      />
                    </li>
                  ))}
                </ul>
              </div>
            </>
          ) : (
            // No books in my books
            <h1 className="text-5xl mb-7 text-center">My Books (0)</h1>
          ) // While loading display loading animation
        ) : (
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

export default MyBooksPage;
