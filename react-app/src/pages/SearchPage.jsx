import { useEffect } from "react";
import AllBooksSearchBar from "../components/AllBooksSearchBar";

function SearchPage() {
  return (
    <>
      <div className="flex h-screen flex-col px-6 pt-6 bg-background">
        <h1 className="text-5xl mb-7 text-center">Search Books</h1>
        <AllBooksSearchBar />
      </div>
    </>
  );
}

export default SearchPage;
