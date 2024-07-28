import { Link } from "react-router-dom";

function PhraseContainer({ text, title }) {
  const displayText = splitText();
  const combinedText = displayText.join("");

  function splitText() {
    const regex = /(<\/?em>[^<]*<\/em>)/;
    var result = text.split(regex);
    var highlighted = result[1];
    highlighted = highlighted.replace(/<em>/, "");
    highlighted = highlighted.replace(/<\/em>/, "");
    result[1] = highlighted;

    return result;
  }

  return (
    <Link
      to={"/search-details"}
      state={{
        splitText: displayText,
        combinedText: combinedText,
        bookTitle: title,
      }}
    >
      <div className="shadow border-primary border bg-container rounded-2xl mb-3 w-full py-4 px-6 text-onContainer text-2xl">
        <span>{displayText[0]}</span>
        <span>
          <strong>{displayText[1]}</strong>
        </span>
        <span>{displayText[2]}</span>
      </div>
    </Link>
  );
}

export default PhraseContainer;
