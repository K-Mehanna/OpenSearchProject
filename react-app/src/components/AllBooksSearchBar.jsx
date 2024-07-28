import { useNavigate } from "react-router-dom";
import SearchBar from "./SearchBar";

function AllBooksSearchBar() {
  const navigate = useNavigate();

  function submitForm(event) {
    event.preventDefault();
    const value = event.target[0].value;
    navigate(`/search-books/${value}`);
  }

  return (
    <SearchBar
      className="mb-8"
      onSubmitFunc={submitForm}
      placeholderText="Search for a book..."
    />
  );
}

export default AllBooksSearchBar;
