function myFunction14() {
	var x;
	var txt = "";
	var person = {
		fname : "John",
		lname : "Doe",
		age : 25
	};

	for (x in person) {
		txt = txt + person[x];
	}

	document.getElementById("demo").innerHTML = txt;
}
