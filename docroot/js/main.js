AUI().ready(function(A) {
	A.one("#" + process).on('change', function() {
		console.log(process);
		var x = document.getElementById(process).value;

		switch (x) {
		case "convert":
			document.getElementById("convertId").style.display = "block";
			document.getElementById("segmentationId").style.display = "none";
			document.getElementById("extractionId").style.display = "none";
			document.getElementById("registrationId").style.display = "none";
			break;
		case "segmentation":
			document.getElementById("convertId").style.display = "none";
			document.getElementById("segmentationId").style.display = "block";
			document.getElementById("extractionId").style.display = "none";
			document.getElementById("registrationId").style.display = "none";
			break;
		case "extraction":
			document.getElementById("convertId").style.display = "none";
			document.getElementById("segmentationId").style.display = "none";
			document.getElementById("extractionId").style.display = "block";
			document.getElementById("registrationId").style.display = "none";
			break;
		case "registration":
			document.getElementById("convertId").style.display = "none";
			document.getElementById("segmentationId").style.display = "none";
			document.getElementById("extractionId").style.display = "none";
			document.getElementById("registrationId").style.display = "block";
			break;
		case "select":
			document.getElementById("convertId").style.display = "none";
			document.getElementById("segmentationId").style.display = "none";
			document.getElementById("extractionId").style.display = "none";
			document.getElementById("registrationId").style.display = "none";
			break;
		default:
			alert("Unrecognized process selected!!!");
			document.getElementById("convertId").style.display = "none";
			document.getElementById("segmentationId").style.display = "none";
			document.getElementById("extractionId").style.display = "none";
			document.getElementById("registrationId").style.display = "none";
			break;
		}
		document.getElementById(selectedProcess).value = x;
	});
});

function hideDiv(id) {
	document.getElementById(process).value = "select";
	document.getElementById(id).style.display = "none";
}