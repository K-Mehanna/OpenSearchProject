import { useState, useEffect } from "react";
import ResultsTile from "../components/ResultsTile";
import AllBooksSearchBar from "../components/AllBooksSearchBar";
import api from "../api/axiosConfig";
import { FallingLines } from "react-loader-spinner";
import { useParams } from "react-router-dom";

function SpecificSearchPage() {
  const { term } = useParams();
  const [books, setBooks] = useState(null);

  useEffect(() => {
    searchBooks(term);
  }, [term]);

  const searchBooks = async (title) => {
    if (title === "") {
      return;
    }
    console.log("Searching books with term:", term);
    try {
      const response = await api.get(`/api/v1/search/${term}`);
      setBooks(response.data);
    } catch (error) {
      console.error(
        "Error fetching books:",
        error.response ? error.response.data : error.message
      );
    }
  };

  return (
    <>
      <div className="flex h-screen flex-col px-6 pt-6 bg-background">
        <h1 className="text-5xl mb-7 text-center">Search Books</h1>
        <AllBooksSearchBar />
        {books ? (
          books.length > 0 ? (
            <>
              <h2 className="text-4xl mb-4 left-0">
                Results ({books ? books.length : "..."})
              </h2>
              <div className="flex-1 overflow-auto">
                <ul>
                  {books.map((book, index) => (
                    <li key={index}>
                      {/* <li key={book.title.concat(book.download_count.toString())}> */}
                      <ResultsTile book={book} myBooks={false} />
                    </li>
                  ))}
                </ul>
              </div>
            </>
          ) : (
            <h1 className="text-3xl mb-7">No results found</h1>
          )
        ) : (
          <div className="flex justify-center items-center">
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

export default SpecificSearchPage;
