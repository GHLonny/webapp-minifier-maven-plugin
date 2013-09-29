function myFunction2() {
	var x = "";
	var time = new Date().getHours();
	if (time < 20) {
		x = "Good day";
	} else {
		x = "Good evening";
	}
	document.getElementById("demo").innerHTML = x;
}
