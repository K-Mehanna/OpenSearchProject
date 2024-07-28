import axios from "axios";
import { springUrl } from "../config";

const baseURLString = `http://${springUrl}:8080`;
console.log("API URL:", baseURLString);

export default axios.create({
  baseURL: baseURLString,
});
