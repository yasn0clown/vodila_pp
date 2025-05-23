import { ChangeEventHandler } from "react";
import { Button } from "./Button";

export const UploadButton = ({
  text,
  onChange,
}: {
  text: string;
  onChange: ChangeEventHandler<HTMLInputElement>;
}) => {
  return (
    <div className="w-[100%]">
      <label
        htmlFor="file-upload"
        className="w-[100%] cursor-pointer inline-block"
      >
        <Button className="w-[100%]" variant="default" asChild>
          <span>{text}</span>
        </Button>
      </label>
      <input
        id="file-upload"
        accept="image/*"
        type="file"
        onChange={onChange}
        className="hidden"
      />
    </div>
  );
};
