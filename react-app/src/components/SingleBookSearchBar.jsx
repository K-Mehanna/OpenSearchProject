import SearchBar from './SearchBar';

function SingleBookSearchBar({ stateFunc }) {

  function submitForm(event) {
    event.preventDefault();
    const value = event.target[0].value;
    stateFunc(value);
  }

  return (
    <SearchBar onSubmitFunc={submitForm} placeholderText="Search for phrases..." />
  );
}

export default SingleBookSearchBar;
