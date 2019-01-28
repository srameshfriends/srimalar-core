function onPageLoaded() {
    $('.nav-link').unbind().click(function(evt){
        var url = $(evt.currentTarget).data("url");
        $("#page-main").load(url);
        $('.navbar-collapse').collapse('hide');
        return false;
    });
}

