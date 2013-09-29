function myFunction7() {
	var x;

	var person = prompt("Please enter your name", "Harry Potter");

	if (person != null) {
		x = "Hello " + person + "! How are you today?";
		document.getElementById("demo").innerHTML = x;
	}
}
