$(function() {
       
    /*list user*/
    $( "#dialog:ui-dialog" ).dialog( "destroy" );
    
    $( ".addEntity" ).click(function(ev) {        
        ev.preventDefault();
        var link = $(this).attr('id');
        openDialog(link);
    });
    
    $(".editar").click(function(ev) {
        ev.preventDefault();
        var link = $(this).attr('href');
        openDialog(link);
    });    
    
    function openDialog(link){
        $( "#dialog-form" ).
        load(link, function() {
            // abrir o diálogo somente após carregar form
            $( "#dialog-form" ).dialog( "open" );
            $(this).siblings(".ui-dialog-titlebar").children(".ui-dialog-title").text($(this).find("#dialog-form" ).attr("title"));
        });
    }

    
    $( "#dialog-form" ).dialog({
        autoOpen: false,
        modal: true,
        width: 655,
        //        height: $(window).height()-10,
        buttons: {
            "Salvar": function() {
                var d = $(this);
                $.post($("#entityUpdateForm").attr("action"), $("#entityUpdateForm").serialize()).done( function(data, status, jqxhr) {
                    if(jqxhr.status == 200) {                        
                        $( "#dialog-form" ).empty().append( data );
                    
                    }else {                        
                        $( "#dialog-form" ).empty();
                        $( d ).dialog( "close" );
                        window.location.reload();
                    }
                });
                
            },
            "Cancelar": function() {
                $( this ).empty();
                $( this ).dialog( "close" );
            }
        },
        close: function() {
            $( this ).empty();
        }
    });
});
