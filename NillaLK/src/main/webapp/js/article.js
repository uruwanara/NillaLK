/* ======================================================================================== */

/*ARTICLE MARKUP RENDERING METHOD*/
const contentHere = document.getElementById('render-content');
let markupArea = document.getElementById('markup-content');

markupArea.value = localStorage.markupValue;

const typed = () => {
	let text = localStorage.markupValue || markupArea.value;
	const newText = marked(text);
	contentHere.innerHTML = newText;
	return markupArea.value;
};

typed();

document.addEventListener('keyup', () => {
	localStorage.setItem("markupValue", typed());
	typed();
});
