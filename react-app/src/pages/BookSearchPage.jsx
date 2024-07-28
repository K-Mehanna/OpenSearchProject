import { useState, useEffect } from "react";
import { FallingLines } from "react-loader-spinner";
import api from "../api/axiosConfig";
import { useParams } from "react-router-dom";
import SingleBookSearchBar from "../components/SingleBookSearchBar";
import PhraseContainer from "../components/PhraseContainer";

function BookSearchPage() {
  const { id } = useParams();
  const [book, setBook] = useState(null);
  const [searchTerm, setSearchTerm] = useState(null);
  const [matchedPhrases, setMatchedPhrases] = useState(null);
  const [numResults, setNumResults] = useState(32);
  const [queryType, setQueryType] = useState("normal");

  useEffect(() => {
    searchBook(id);
  }, [id]);

  useEffect(() => {
    searchPhrase();
  }, [searchTerm, queryType]);

  const searchBook = async () => {
    try {
      const response = await api.get(`/api/v1/search/details-${id}`);
      setBook(response.data);
    } catch (error) {
      console.error(
        "Error fetching book:",
        error.response ? error.response.data : error.message
      );
    }
  };

  const typeToInt = (type) => {
    switch (type) {
      case "normal":
        return 0;
      case "wildcard":
        return 1;
      case "regex":
        return 2;
      case "neural":
        return 3;
      default:
        return 0;
    }
  };

  const searchPhrase = async () => {
    console.log("Searching for:", searchTerm);
    var isWildCard = typeToInt(queryType);
    if (isWildCard > 2) return;
    console.log("Wildcard:", isWildCard);
    if (!searchTerm) return;
    console.log(
      `API: /api/v1/search/search-in-doc-${id}-${numResults}-${isWildCard}`
    );
    const response = await api.post(
      `/api/v1/search/search-in-doc-${id}-${numResults}-${isWildCard}`,
      searchTerm
    );
    setMatchedPhrases(response.data);
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Submit", numResults);
    searchPhrase();
  };

  const radioChangeHandler = (e) => {
    setQueryType(e.target.value);
    console.log("Radio: ", e.target.value);
  };

  return (
    <>
      <div className="flex h-screen flex-col px-6 pt-6 bg-background">
        {book ? (
          <>
            <h1 className="text-5xl mb-8 text-center">{book.title}</h1>
            <SingleBookSearchBar stateFunc={setSearchTerm} />
            <div className="w-full justify-center flex">
              <div className="flex mb-8">
                <h3 className="text-2xl">Query type: </h3>
                <div className="pt-1">
                  <input
                    type="radio"
                    className="ml-4 mr-1 h-3 w-3"
                    id="normal"
                    value="normal"
                    name="queryType"
                    checked={queryType === "normal"}
                    onChange={radioChangeHandler}
                  ></input>
                  <label htmlFor="normal" className="text-xl">
                    Normal
                  </label>
                  <input
                    type="radio"
                    className="ml-4 mr-1 h-3 w-3"
                    id="wildcard"
                    value="wildcard"
                    name="queryType"
                    checked={queryType === "wildcard"}
                    onChange={radioChangeHandler}
                  ></input>
                  <label htmlFor="wildcard" className="text-xl">
                    Wildcard
                  </label>
                  <input
                    type="radio"
                    className="ml-4 mr-1 h-3 w-3"
                    value="regex"
                    id="regex"
                    name="queryType"
                    checked={queryType === "regex"}
                    onChange={radioChangeHandler}
                  ></input>
                  <label htmlFor="regex" className="text-xl">
                    Regex
                  </label>
                  <input
                    type="radio"
                    className="ml-4 mr-1 h-3 w-3"
                    value="neural"
                    id="neural"
                    name="queryType"
                    checked={queryType === "neural"}
                    onChange={radioChangeHandler}
                  ></input>
                  <label htmlFor="neural" className="text-xl">
                    Neural
                  </label>
                </div>
              </div>
            </div>
            {queryType === "neural" ? (
              <div className="w-full flex p-4 justify-center bg-red-600 text-white items-center rounded-lg">
                <h2 className="text-3xl text-center">
                  Neural search is not implemented due to a limitation with
                  highlighting.
                </h2>
              </div>
            ) : (
              <>
                <div className="flex justify-between w-full p-4">
                  <h1 className="text-4xl mb-6">
                    Results {matchedPhrases ? `(${matchedPhrases.length})` : ""}
                  </h1>
                  <div className="flex items-start mb-4">
                    <h3 className="text-3xl">Maximum number of results: </h3>
                    <form onSubmit={handleSubmit}>
                      <input
                        className="bg-background border-2 border-primary rounded-2xl text-3xl w-[90px] h-10 ml-4 p-2 text-center"
                        type="number"
                        id="numResults"
                        value={numResults}
                        onChange={(e) => setNumResults(e.target.value)}
                        placeholder={numResults.toString()}
                      ></input>
                    </form>
                  </div>
                </div>
                {matchedPhrases ? (
                  <div className="flex-1 overflow-auto">
                    <ul>
                      {matchedPhrases.map((phrase, index) => (
                        <li key={index}>
                          <PhraseContainer text={phrase} title={book.title} />
                        </li>
                      ))}
                    </ul>
                  </div>
                ) : (
                  <></>
                )}
              </>
            )}
          </>
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

export default BookSearchPage;
