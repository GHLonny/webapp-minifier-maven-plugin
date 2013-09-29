function myFunction11() {
	var x = "", i = 0;
	do {
		x = x + "The number is " + i + "<br>";
		i++;
	} while (i < 5)
	document.getElementById("demo").innerHTML = x;
}
