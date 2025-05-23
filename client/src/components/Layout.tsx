import { ReactNode } from "react";
import { Outlet } from "react-router";

const Layout = ({ header }: { header: ReactNode }) => {
  return (
    <div className="w-[100%] h-[100%] flex flex-col px-6">
      {header}
      <main className="h-[100%]">
        <Outlet />
      </main>
    </div>
  );
};

export default Layout;
