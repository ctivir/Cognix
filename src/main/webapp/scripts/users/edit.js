$(function() {
    /*editando user*/
    $( "#alterPass" ).button({
        icons: {
            primary: "ui-icon-key"
        }
    }).click(function(ev){
        ev.preventDefault();
        $("#passwords").show();
        $(this).hide();
    });
    
    if($("#password").hasClass("error") || $("#confirmPass").hasClass("error")){
        $( "#alterPass" ).hide();
        $("#passwords").show();
    }
});