import { Link } from "react-router-dom";

function LinkTile({ dest, text, icon }) {
  return (
    <div className="w-full">
        <Link to={dest} >
          <div className="hover:bg-hover rounded-xl p-2 flex items-center">
            <img src={icon} alt="Navbar icon" className="h-6 w-6 mr-3" />
            <span>{text}</span>
          </div>
        </Link>
    </div>
  );
}

export default LinkTile;