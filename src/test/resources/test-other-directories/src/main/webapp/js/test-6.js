function myFunction6() {
	var x;
	var r = confirm("Press a button!");
	if (r == true) {
		x = "You pressed OK!";
	} else {
		x = "You pressed Cancel!";
	}
	document.getElementById("demo").innerHTML = x;
}
