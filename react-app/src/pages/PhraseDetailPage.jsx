import { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import api from "../api/axiosConfig";

function PhraseDetailPage() {
  const location = useLocation();
  const { splitText, combinedText, bookTitle } = location.state;
  const [sentiment, setSentiment] = useState("-------");

  useEffect(() => {
    getSentimentAPI();
  }, [combinedText]);

  // NLP library
  const getSentimentAPI = async () => {
    try {
      const response = await api.post(
        "/api/v1/search/get-nlp-info",
        combinedText
      );
      setSentiment(response.data);
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
        <>
          <div className="flex justify-between items-center mb-5">
            <div className="flex-1 text-center">
              <h1 className="text-5xl">{bookTitle}</h1>
            </div>
          </div>
          <div className="bg-container rounded-xl p-4 shadow border-2 border-primary text-3xl mb-4">
            <span>{splitText[0]}</span>
            <span>
              <strong>{splitText[1]}</strong>
            </span>
            <span>{splitText[2]}</span>
          </div>
          <div className="bg-container rounded-xl p-4 shadow border-2 border-primary text-2xl">
            <h2>Sentiment: {sentiment}</h2>
          </div>
        </>
      </div>
    </>
  );
}

export default PhraseDetailPage;
