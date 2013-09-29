function myFunction10() {
	var x = "", i = 0;
	while (i < 5) {
		x = x + "The number is " + i + "<br>";
		i++;
	}
	document.getElementById("demo").innerHTML = x;
}
