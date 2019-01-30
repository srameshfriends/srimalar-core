function loadPage(url) {
    $("#page-main").load(url, function(responseTxt, statusTxt, xhr){
    	 if(statusTxt == "error") {
	        alert("Error: " + url + " \n " + xhr.status + ": " + xhr.statusText);
	     }
    });
}
function onPageInit() {
    $('.nav-link').unbind().click(function(evt){
	$('.navbar-collapse').collapse('hide');
        loadPage($(evt.currentTarget).data("url"));
        return false;
    });
}

