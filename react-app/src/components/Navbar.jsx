import { Outlet, Link } from "react-router-dom";
import LinkTile from "./LinkTile";
import searchIcon from '../assets/search.png';
import bookIcon from '../assets/book.png';

function Navbar() {
  return (
    <> 
      <div className="flex">
        <div className="w-1/6 max-w-56 bg-navbar text-onContainer px-4 py-8 h-screen text-center text-xl">
          <nav>
            <ul>
              <li className="mb-4">
                <LinkTile dest={"/search-books"} text={"Search Books"} icon={searchIcon} />
              </li>
              <li className="mb-4">
              <LinkTile dest={"/my-books"} text={"My Books"} icon={bookIcon} />
              </li>
            </ul>
          </nav>
        </div>

        <div className="flex-1">
          <Outlet />
        </div>
      </div>
    </>
  );
}

export default Navbar;
