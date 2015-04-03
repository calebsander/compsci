//for sudoku.com
var table = document.getElementById('table').childNodes[0].childNodes;
var outputString = "";
var row, innerHTML;
for (var i = 0, j; i < table.length; i++) {
	row = table[i].childNodes;
	for (j = 0; j < row.length; j++) {
		innerHTML = row[j].childNodes[0].innerHTML;
		if (innerHTML == '&nbsp;') outputString += ' ';
		else outputString += innerHTML;
	}
	outputString += '\n';
}
console.log(outputString);