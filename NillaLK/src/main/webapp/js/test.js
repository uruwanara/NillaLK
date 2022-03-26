// Provide variables for the main interface
const contentHere = document.getElementById('render-content');
let markupArea = document.getElementById('markup-content');

// Before anything else gave it the value of localStorage
// I can do the storage checking too but the code looks simple this way
markupArea.value = localStorage.markupValue;

/* The main function to add the text value either from localStorage or the input value itself.
It then goes through the text and using the marked library to convert it into HTML.
After the conversion, the newly converted html is set to the #content div.
It the end it returns the raw markup so I can set it into localStorage.
P.S. This is my first time utilising localStorage & it was a pain at first.
*/
const typed = () => {
  let text = localStorage.markupValue || markupArea.value;
  const newText = marked(text);
  contentHere.innerHTML = newText;
  return markupArea.value;
};

// Calls the typed() function once when the window loads.
typed();

// Whenever someone types in the textarea this will run.
document.addEventListener('keyup', () => {
// localstorage will set itself to the value returned by typed() function.
  localStorage.setItem("markupValue", typed());
// typed() function is rendered to the page.
  typed();
});
