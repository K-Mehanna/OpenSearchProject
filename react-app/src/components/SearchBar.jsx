import searchIcon from "../assets/search.png";

function SearchBar({ onSubmitFunc, placeholderText }) {
  return (
    <div className="justify-center flex">
      <form
        id="search"
        onSubmit={onSubmitFunc}
        className="max-w-screen-lg w-full"
      >
        <div className="mb-4 flex bg-container border-primary border items-center justify-center shadow rounded-2xl w-full py-2 px-3 text-onContainer text-xl h-16">
          <input
            className="appearance-none focus:outline-none flex-1 bg-container"
            name="searchTerm"
            type="text"
            id="searchTerm"
            placeholder={placeholderText}
          />
          <div
            className="ml-2 h-7 w-7 flex items-center justify-center cursor-pointer"
            onClick={() => document.getElementById("submitButton").click()}
          >
            <img src={searchIcon} alt="Search icon" />
            <button type="submit" id="submitButton" className="hidden" />
          </div>
        </div>
      </form>
    </div>
  );
}

export default SearchBar;
