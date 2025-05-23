import { Link } from "react-router";
import LogoutButton from "./LogoutButton";

const Header = () => {
  return (
    <header className="flex justify-between w-[100%] items-center h-[70px]">
      <nav>
        <ul className="flex items-center gap-4">
          {
            <li>
              <Link to={"/"}>Домашняя страница</Link>
            </li>
          }
          {
            <li>
              <Link to={"/chart"}>График</Link>
            </li>
          }
        </ul>
      </nav>
      <LogoutButton />
    </header>
  );
};

export default Header;
