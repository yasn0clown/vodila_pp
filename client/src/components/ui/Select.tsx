import * as React from "react";
import * as SelectPrimitive from "@radix-ui/react-select";
import { Check, ChevronDown, ChevronUp } from "lucide-react";

const Select = SelectPrimitive.Root;
const SelectGroup = SelectPrimitive.Group;
const SelectValue = SelectPrimitive.Value;

const SelectTrigger = React.forwardRef<
  React.ElementRef<typeof SelectPrimitive.Trigger>,
  React.ComponentPropsWithoutRef<typeof SelectPrimitive.Trigger>
>(({ className, children, ...props }, ref) => (
  <SelectPrimitive.Trigger
    ref={ref}
    className={`
      flex h-10 w-full items-center justify-between 
      rounded-lg border border-neutral-200 bg-white/90
      px-4 py-2 text-sm font-medium shadow-sm
      backdrop-blur-sm transition-all
      hover:bg-neutral-50 hover:shadow-md
      focus:outline-none focus:ring-2 focus:ring-blue-500/30
      disabled:cursor-not-allowed disabled:opacity-50
      dark:border-neutral-600 dark:bg-neutral-800/90 dark:hover:bg-neutral-700/90
      ${className}
    `}
    {...props}
  >
    {children}
    <SelectPrimitive.Icon asChild>
      <ChevronDown className="h-4 w-4 opacity-70" />
    </SelectPrimitive.Icon>
  </SelectPrimitive.Trigger>
));

const SelectScrollUpButton = React.forwardRef<
  React.ElementRef<typeof SelectPrimitive.ScrollUpButton>,
  React.ComponentPropsWithoutRef<typeof SelectPrimitive.ScrollUpButton>
>(({ className, ...props }, ref) => (
  <SelectPrimitive.ScrollUpButton
    ref={ref}
    className={`
      flex cursor-default items-center justify-center py-1
      bg-gradient-to-b from-white/90 to-transparent
      dark:from-neutral-800/90 dark:to-transparent
      ${className}
    `}
    {...props}
  >
    <ChevronUp className="h-4 w-4 text-neutral-600 dark:text-neutral-400" />
  </SelectPrimitive.ScrollUpButton>
));

const SelectScrollDownButton = React.forwardRef<
  React.ElementRef<typeof SelectPrimitive.ScrollDownButton>,
  React.ComponentPropsWithoutRef<typeof SelectPrimitive.ScrollDownButton>
>(({ className, ...props }, ref) => (
  <SelectPrimitive.ScrollDownButton
    ref={ref}
    className={`
      flex cursor-default items-center justify-center py-1
      bg-gradient-to-t from-white/90 to-transparent
      dark:from-neutral-800/90 dark:to-transparent
      ${className}
    `}
    {...props}
  >
    <ChevronDown className="h-4 w-4 text-neutral-600 dark:text-neutral-400" />
  </SelectPrimitive.ScrollDownButton>
));

const SelectContent = React.forwardRef<
  React.ElementRef<typeof SelectPrimitive.Content>,
  React.ComponentPropsWithoutRef<typeof SelectPrimitive.Content>
>(({ className, children, position = "popper", ...props }, ref) => (
  <SelectPrimitive.Portal>
    <SelectPrimitive.Content
      ref={ref}
      className={`
        relative z-50 max-h-96 min-w-[8rem] overflow-hidden 
        rounded-xl border border-neutral-200 bg-white/95
        p-1 text-neutral-900 shadow-lg backdrop-blur-lg
        data-[state=open]:animate-in data-[state=closed]:animate-out
        data-[state=closed]:fade-out-0 data-[state=open]:fade-in-0
        data-[state=closed]:zoom-out-95 data-[state=open]:zoom-in-95
        dark:border-neutral-700 dark:bg-neutral-800/95 dark:text-neutral-100
        ${position === "popper" ? "data-[side=bottom]:translate-y-1" : ""}
        ${className}
      `}
      position={position}
      {...props}
    >
      <SelectScrollUpButton />
      <SelectPrimitive.Viewport
        className={`
          p-1
          ${
            position === "popper"
              ? "h-[var(--radix-select-trigger-height)] w-full"
              : ""
          }
        `}
      >
        {children}
      </SelectPrimitive.Viewport>
      <SelectScrollDownButton />
    </SelectPrimitive.Content>
  </SelectPrimitive.Portal>
));

const SelectLabel = React.forwardRef<
  React.ElementRef<typeof SelectPrimitive.Label>,
  React.ComponentPropsWithoutRef<typeof SelectPrimitive.Label>
>(({ className, ...props }, ref) => (
  <SelectPrimitive.Label
    ref={ref}
    className={`
      px-3 py-1.5 text-xs font-semibold text-neutral-500
      uppercase tracking-wider
      dark:text-neutral-400
      ${className}
    `}
    {...props}
  />
));

const SelectItem = React.forwardRef<
  React.ElementRef<typeof SelectPrimitive.Item>,
  React.ComponentPropsWithoutRef<typeof SelectPrimitive.Item>
>(({ className, children, ...props }, ref) => (
  <SelectPrimitive.Item
    ref={ref}
    className={`
      relative flex w-full cursor-default select-none items-center
      rounded-md py-1.5 pl-8 pr-2 text-sm outline-none
      transition-colors hover:bg-blue-50 hover:text-blue-600
      focus:bg-blue-50 focus:text-blue-600
      data-[disabled]:pointer-events-none data-[disabled]:opacity-50
      dark:hover:bg-blue-900/30 dark:hover:text-blue-300
      dark:focus:bg-blue-900/30 dark:focus:text-blue-300
      ${className}
    `}
    {...props}
  >
    <span className="absolute left-2 flex h-3.5 w-3.5 items-center justify-center">
      <SelectPrimitive.ItemIndicator>
        <Check className="h-4 w-4 text-blue-600 dark:text-blue-400" />
      </SelectPrimitive.ItemIndicator>
    </span>
    <SelectPrimitive.ItemText>{children}</SelectPrimitive.ItemText>
  </SelectPrimitive.Item>
));

const SelectSeparator = React.forwardRef<
  React.ElementRef<typeof SelectPrimitive.Separator>,
  React.ComponentPropsWithoutRef<typeof SelectPrimitive.Separator>
>(({ className, ...props }, ref) => (
  <SelectPrimitive.Separator
    ref={ref}
    className={`
      -mx-1 my-1 h-px bg-neutral-200
      dark:bg-neutral-700
      ${className}
    `}
    {...props}
  />
));

export {
  Select,
  SelectGroup,
  SelectValue,
  SelectTrigger,
  SelectContent,
  SelectLabel,
  SelectItem,
  SelectSeparator,
  SelectScrollUpButton,
  SelectScrollDownButton,
};
