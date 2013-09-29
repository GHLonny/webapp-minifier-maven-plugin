function myFunction13() {
	var x = "", i = 0;
	for (i = 0; i < 10; i++) {
		if (i == 3) {
			continue;
		}
		x = x + "The number is " + i + "<br>";
	}
	document.getElementById("demo").innerHTML = x;
}
