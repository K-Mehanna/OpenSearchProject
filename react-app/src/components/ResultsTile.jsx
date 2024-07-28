import { Link } from "react-router-dom";
import api from "../api/axiosConfig";

function ResultsTile({ book, myBooks, setMyBooksState }) {
  function handleDeleteClick(event) {
    event.stopPropagation();
    event.preventDefault();
    removeBook();
  }

  function formatName(name) {
    if (name.includes(",")) {
      const [lastName, firstName] = name.split(",").map((part) => part.trim());
      return `${firstName} ${lastName}`;
    }
    return name.trim();
  }

  function listOfAuthors() {
    if (book.authors.length === 0) return "-----";
    const nameArray = book.authors.map((author) => formatName(author.name));
    return nameArray.join(", ");
  }

  const removeBook = async () => {
    try {
      const response = await api.delete(
        `/api/v1/search/delete-book-${book.id}`
      );
      setMyBooksState(response.data);
    } catch (error) {
      console.error(
        "Error deleting book: ",
        error.response ? error.response.data : error.message
      );
    }
  };

  return (
    <Link to={myBooks ? `/book-search/${book.id}` : `/details/${book.id}`}>
      <div className="shadow border-primary border bg-container rounded-2xl mb-3 w-full py-4 px-6 text-onContainer text-xl">
        <div className="flex justify-between items-center">
          <div>
            <h2 className="text-3xl">Title: {book.title}</h2>
            <h6>Author: {listOfAuthors()}</h6>
          </div>
          <p className="text-right flex-grow">
            No. downloads: {book.download_count}
          </p>
          {myBooks ? (
            <div
              onClick={handleDeleteClick}
              className="ml-4 hover:text-red-500"
            >
              <i className="fa fa-trash-o fa-lg"></i>
            </div>
          ) : null}
        </div>
      </div>
    </Link>
  );
}

export default ResultsTile;
