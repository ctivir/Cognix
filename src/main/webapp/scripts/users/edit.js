$(function() {
    /*editando user*/
    $( "#alterPass" ).click(function(ev){
        ev.preventDefault();
        $("#passwords").show();
        $(this).hide();
    });
    
    if($("#password").hasClass("error") || $("#confirmPass").hasClass("error")){
        $( "#alterPass" ).hide();
        $("#passwords").show();
    }
});